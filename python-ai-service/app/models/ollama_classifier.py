from PIL import Image
import base64
import io
import requests
import json
import re
from typing import List, Dict, Tuple
# Asegúrate de que la importación de ImageAnalyzer sea correcta en tu entorno
from app.models.image_analyzer import ImageAnalyzer 
#class ImageAnalyzer: # Mock para que el código sea independiente
 #   def analyze_image(self, path): return {}
  #  def get_hints_for_ollama(self, features): return "Análisis no disponible"

class OllamaClassifier:
    def __init__(self):
        self.ollama_url = "http://localhost:11434/api/generate"
        self.model = "llava"
        # self.analyzer = ImageAnalyzer() # Descomentar si usas el archivo real
        self.analyzer = ImageAnalyzer() 
        
        # Catálogo robusto de incidentes con palabras clave
        self.tipos_incidente = {
            1: {"nombre": "BACHES", "keywords": ["bache", "hueco", "hoyo", "pavimento roto", "asfalto dañado", "pequeño hueco", "calle dañada", "desperfecto"]},
            2: {"nombre": "POSTE CAÍDO", "keywords": ["poste", "caído", "inclinado", "alambrado", "cables", "estructura vertical caída", "poste de luz", "torre"]},
            3: {"nombre": "VEREDA RAJADA", "keywords": ["vereda", "acera", "rajada", "grieta", "rota", "losa", "cemento roto", "grietas finas", "deterioro acera", "pavimento peatonal"]}, 
            4: {"nombre": "PISTAS CON HUECOS", "keywords": ["pista", "carretera", "huecos", "deterioro", "múltiples baches", "via", "calzada", "huecos extensos", "camino roto", "asfalto destrozado"]}, 
            5: {"nombre": "SEMÁFORO MALOGRADO", "keywords": ["semáforo", "malogrado", "roto", "no funciona", "luz intermitente", "semáforo apagado", "luz de tráfico", "señal de tráfico"]},
            6: {"nombre": "FUGA DE AGUA", "keywords": ["fuga", "agua", "desagüe", "inundación", "charco grande", "tubería rota", "inundado", "agua saliendo", "desborde"]},
            7: {"nombre": "BASURA ACUMULADA", "keywords": ["basura", "residuos", "acumulada", "desperdicio", "desecho", "montón de basura", "desperdicios", "bolsas de basura"]},
            8: {"nombre": "ALUMBRADO PÚBLICO", "keywords": ["alumbrado", "luminaria", "luz", "lámpara", "apagado", "oscuridad", "farola", "foco quemado", "calle oscura"]},
            9: {"nombre": "ÁRBOL CAÍDO", "keywords": ["árbol", "caído", "rama", "vegetación", "bloqueando", "tronco", "poda", "peligro árbol"]},
            10: {"nombre": "OTROS", "keywords": ["otro", "diferente", "no clasificado", "ninguno", "vandalismo", "inapropiado", "incidente menor"]}
        }
        
        self.tipos_clasificacion = {
            1: "Riesgo Bajo", 2: "Riesgo Medio", 3: "Riesgo Alto", 4: "Riesgo Crítico"
        }

    def normalize_type_name(self, tipo_incidente: str, id_incidente: int) -> Tuple[int, str]:
        """
        Normaliza el nombre del tipo de incidente y valida contra el catálogo.
        """
        tipo_lower = tipo_incidente.lower().strip()
        
        # 1. Búsqueda por palabra clave y scoring
        mejor_match = None
        max_score = 0
        
        for id_tipo, config in self.tipos_incidente.items():
            score = 0
            if config["nombre"].lower() == tipo_lower:
                 score += 5 
            for keyword in config["keywords"]:
                if keyword in tipo_lower:
                    score += 1
            
            if score > max_score:
                max_score = score
                mejor_match = (id_tipo, config["nombre"])
        
        if mejor_match and max_score >= 1:
            return mejor_match
        
        # 2. Fallback al ID original (si es válido)
        if id_incidente in self.tipos_incidente:
            return (id_incidente, self.tipos_incidente[id_incidente]["nombre"])
        
        # 3. Default: OTROS
        return (10, self.tipos_incidente[10]["nombre"])

    def classify_incident(self, image_path: str, yolo_detections: List[Dict]) -> str:
        """Clasifica el incidente usando Ollama Vision + OpenCV"""
        
        cv_features = self.analyzer.analyze_image(image_path)
        cv_hints = self.analyzer.get_hints_for_ollama(cv_features)
        
        print("📸 Análisis OpenCV:")
        print(cv_hints)
        
        with open(image_path, "rb") as img_file:
            img_base64 = base64.b64encode(img_file.read()).decode('utf-8')
        
        yolo_info = "No se detectaron objetos específicos"
        if yolo_detections:
            yolo_info = ', '.join([f"{item['class']} (confianza: {item['confidence']:.2f})" 
                                   for item in yolo_detections])

        opciones_incidentes_str = "\n".join([
            f"{id}: {config['nombre']}" 
            for id, config in self.tipos_incidente.items()
        ])
        
        # INICIO DEL PROMPT CON PRIORIDADES CONDICIONALES PARA LOS 10 INCIDENTES
        prompt = f"""Analiza esta imagen de un reporte ciudadano de infraestructura urbana.

OBJETOS DETECTADOS POR YOLO:
{yolo_info}

ANÁLISIS AUTOMÁTICO CON VISIÓN POR COMPUTADORA (OpenCV):
{cv_hints}

---
✅ CATÁLOGO COMPLETO DE INCIDENTES (ELIGE SOLO UNO POR ID):
{opciones_incidentes_str}
---

INSTRUCCIONES PRIORITARIAS (Buscando Evidencia CLARA para los 10 tipos):
1.  **POSTE CAÍDO (ID 2) / ÁRBOL CAÍDO (ID 9)**: Busca estructuras verticales caídas o inclinadas (Confianza OpenCV/YOLO > 0.70).
2.  **SEMÁFORO MALOGRADO (ID 5)**: Busca semáforos apagados, rotos o con fallo obvio.
3.  **FUGA DE AGUA (ID 6)**: Busca agua saliendo de forma activa o inundación clara.
4.  **PISTAS CON HUECOS (ID 4)**: Deterioro EXTENSO, con MÚLTIPLES huecos grandes en la calzada (pista).
5.  **VEREDA RAJADA (ID 3)**: Deterioro, grietas o huecos grandes en el área peatonal (acera/vereda).
6.  **BACHES (ID 1)**: Uno o pocos huecos pequeños en la calzada.
7.  **ALUMBRADO PÚBLICO (ID 8)**: Si la imagen es muy oscura (MÉTRICA OpenCV Brillo bajo) o la luminaria está visiblemente dañada.
8.  **BASURA ACUMULADA (ID 7)**: Grandes montones de basura o residuos dispersos (MÉTRICA OpenCV ratio alto).

CRITERIOS DE RIESGO:
- Riesgo Bajo (1): Mantenimiento preventivo, sin peligro inmediato. (Típicamente: Basura, Baches muy pequeños).
- Riesgo Medio (2): Puede causar inconvenientes, atención en días. (Típicamente: Vereda rajada menor, Alumbrado, Baches medianos).
- Riesgo Alto (3): Peligro potencial, urgente (24-48h). (Típicamente: Poste inclinado, Semáforo, Pistas con huecos extensos).
- Riesgo Crítico (4): Peligro INMEDIATO, requiere acción AHORA. (Típicamente: Poste caído en vía, Fuga de agua peligrosa, Árbol caído bloqueando).

INSTRUCCIONES CRÍTICAS:
- **Priorizar Evidencia y Confianza**: La clasificación final debe basarse en la EVIDENCIA VISUAL más clara de la imagen.
- **Jerarquía de Decisión (Filtro)**:
    A. **SI** el objeto detectado con mayor prioridad tiene una confianza de detección (OpenCV/YOLO) **MAYOR a 0.70**, clasifica ese incidente.
    B. **SI NO hay detecciones de alta confianza (≤ 0.70)**, clasifica el **DAÑO FÍSICO MÁS OBVIO** que se observa en la escena, eligiendo entre los 10 tipos de incidentes.
- **Coherencia con Catálogo**: Elige el `id_tipo_incidente` y `tipo_incidente` EXACTOS de la lista de 10.
- Descripción: máximo 80 caracteres, clara y concisa, menciona el elemento principal.

RESPONDE SOLO CON JSON (sin texto extra, sin markdown, sin explicaciones):
{{
    "id_tipo_incidente": <número 1-10>,
    "tipo_incidente": "<NOMBRE EXACTO DEL CATÁLOGO>",
    "id_clasificacion": <número 1-4>,
    "clasificacion": "<nombre exacto del nivel de riesgo>",
    "descIA": "<descripción en español, max 80 chars>",
    "confianza": <0.0 a 1.0>
}}
"""

        payload = {
            "model": self.model,
            "prompt": prompt,
            "images": [img_base64],
            "stream": False,
            "format": "json",
            "options": {
                "temperature": 0.3,
                "top_p": 0.9,
                "top_k": 40,
                "num_predict": 200
            }
        }

        try:
            response = requests.post(self.ollama_url, json=payload, timeout=120)
            response.raise_for_status()
            result = response.json()
            response_text = result.get("response", "{}")
            
            # Parsear y validar JSON
            json_match = re.search(r'\{.*\}', response_text, re.DOTALL)
            if not json_match:
                raise json.JSONDecodeError("JSON no encontrado en la respuesta", response_text, 0)
            
            data = json.loads(json_match.group(0))
            
            # NORMALIZACIÓN: Corregir tipo_incidente y validar ID
            id_original = data.get("id_tipo_incidente", 10)
            tipo_original = data.get("tipo_incidente", "OTROS")
            
            id_corregido, nombre_correcto = self.normalize_type_name(tipo_original, id_original)
            
            if id_corregido != id_original or nombre_correcto != tipo_original:
                print(f"⚠️ Corrección aplicada: '{tipo_original}' → '{nombre_correcto}' (ID: {id_corregido})")
            
            # Construir respuesta validada
            validated_response = {
                "id_tipo_incidente": id_corregido,
                "tipo_incidente": nombre_correcto,
                "id_clasificacion": min(max(data.get("id_clasificacion", 1), 1), 4),
                "clasificacion": self.tipos_clasificacion.get(data.get("id_clasificacion", 1), "Riesgo Bajo"),
                "descIA": data.get("descIA", "Incidente reportado")[:80],
                "confianza": min(max(data.get("confianza", 0.5), 0.0), 1.0)
            }
            
            return json.dumps(validated_response, ensure_ascii=False)
                
        except Exception as e:
            print(f"❌ Error en clasificación Ollama: {e}")
            return json.dumps({
                "id_tipo_incidente": 10,
                "tipo_incidente": "OTROS",
                "id_clasificacion": 1,
                "clasificacion": "Riesgo Bajo",
                "descIA": f"Error en clasificación automática: {str(e)[:50]}...",
                "confianza": 0.01
            }, ensure_ascii=False)