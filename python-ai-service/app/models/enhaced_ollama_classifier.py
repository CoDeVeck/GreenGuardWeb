from PIL import Image
import base64
import io
import requests
import json
import re
from typing import List, Dict, Tuple
from app.models.image_analyzer import ImageAnalyzer

class EnhancedOllamaClassifier:
    def __init__(self):
        self.ollama_url = "http://localhost:11434/api/generate"
        self.model = "llava"  # o "llava:13b" si lo tienes
        self.fallback_model = "llava"  # Fallback si falla el principal
        self.analyzer = ImageAnalyzer()
        
        self.tipos_incidente = {
            1: {
                "nombre": "BACHES",
                "keywords": ["bache", "hueco", "hoyo"],
                "visual_cues": [
                    "Huecos PEQUEÑOS y AISLADOS en asfalto",
                    "Depresiones circulares u ovaladas",
                    "Asfalto roto alrededor del hueco",
                    "NO confundir con: huecos de alcantarilla, registros, tuberías"
                ],
                "negative_examples": ["tubería", "alcantarilla", "tapa", "registro"]
            },
            2: {
                "nombre": "POSTE CAÍDO",
                "keywords": ["poste", "caído", "inclinado"],
                "visual_cues": [
                    "POSTE DE LUZ o CONCRETO cilíndrico INCLINADO >45°",
                    "Estructura con CABLES ELÉCTRICOS colgando",
                    "Poste de METAL o CONCRETO con base rota",
                    "NO confundir con: árboles (tienen follaje/ramas)"
                ],
                "negative_examples": ["árbol", "rama", "tronco con hojas"]
            },
            3: {
                "nombre": "VEREDA RAJADA",
                "keywords": ["vereda", "acera", "rajada", "grieta"],
                "visual_cues": [
                    "GRIETAS en área PEATONAL (acera/banqueta)",
                    "Losas de cemento rotas o levantadas",
                    "Deterioro en los BORDES de la calle",
                    "NO confundir con: grietas en pista central"
                ],
                "negative_examples": ["pista", "carretera", "asfalto central"]
            },
            4: {
                "nombre": "PISTAS CON HUECOS",
                "keywords": ["pista", "carretera", "huecos múltiples"],
                "visual_cues": [
                    "MÚLTIPLES HUECOS (3+) en área EXTENSA de la pista",
                    "Deterioro generalizado del asfalto",
                    "Área grande afectada (>5m²)",
                    "NO confundir con: un solo bache aislado"
                ],
                "negative_examples": ["un hueco", "bache único"]
            },
            5: {
                "nombre": "SEMÁFORO MALOGRADO",
                "keywords": ["semáforo", "malogrado", "luz de tráfico"],
                "visual_cues": [
                    "SEMÁFORO VISIBLE con luces apagadas",
                    "Carcasa rota o colgando",
                    "Luces parpadeando incorrectamente",
                    "Señal de tráfico vehicular (NO señal peatonal simple)"
                ],
                "negative_examples": ["poste de luz", "luminaria", "farola"]
            },
            6: {
                "nombre": "FUGA DE AGUA",
                "keywords": ["fuga", "agua", "tubería", "inundación"],
                "visual_cues": [
                    "AGUA FLUYENDO activamente de tubería/suelo",
                    "Charco GRANDE con agua limpia (no lluvia)",
                    "Tubería visible ROTA con agua saliendo",
                    "Inundación en calle sin lluvia aparente",
                    "NO confundir con: charcos normales, lluvia reciente"
                ],
                "negative_examples": ["charco", "lluvia", "agua estancada"]
            },
            7: {
                "nombre": "BASURA ACUMULADA",
                "keywords": ["basura", "residuos", "desechos"],
                "visual_cues": [
                    "MONTÓN VISIBLE de bolsas de basura",
                    "Residuos ACUMULADOS en un punto",
                    "Objetos descartados (cajas, botellas, papeles)",
                    "NO confundir con: objetos individuales o decoración"
                ],
                "negative_examples": ["un objeto", "decoración", "mobiliario"]
            },
            8: {
                "nombre": "ALUMBRADO PÚBLICO",
                "keywords": ["alumbrado", "luminaria", "farola", "luz apagada"],
                "visual_cues": [
                    "IMAGEN MUY OSCURA (brillo <80/255)",
                    "FAROLA VISIBLE apagada o rota",
                    "Calle oscura cuando debería estar iluminada",
                    "Poste con luminaria dañada en la parte superior"
                ],
                "negative_examples": ["día soleado", "luz natural"]
            },
            9: {
                "nombre": "ÁRBOL CAÍDO",
                "keywords": ["árbol", "caído", "rama", "tronco"],
                "visual_cues": [
                    "TRONCO con RAMAS y FOLLAJE visible caído",
                    "Vegetación grande bloqueando vía",
                    "Corteza de árbol, hojas, ramas",
                    "NO confundir con: postes (sin follaje, material uniforme)"
                ],
                "negative_examples": ["poste", "estructura de metal/concreto"]
            },
            10: {
                "nombre": "OTROS",
                "keywords": ["otro", "diferente"],
                "visual_cues": [
                    "Cualquier incidente que NO encaje claramente en 1-9",
                    "Vandalismo, grafiti, objetos extraños",
                    "Situaciones ambiguas o poco comunes"
                ],
                "negative_examples": []
            }
        }
        
        self.tipos_clasificacion = {
            1: "Riesgo Bajo",
            2: "Riesgo Medio",
            3: "Riesgo Alto",
            4: "Riesgo Crítico"
        }
    
    def generate_detailed_prompt(self, yolo_info: str, cv_hints: str) -> str:
        """Genera un prompt ULTRA DETALLADO con todas las pistas visuales"""
        no_incident_section = """
═══════════════════════════════════════════════════════════════════
🚫 IMÁGENES QUE NO SON INCIDENTES - DESCARTAR INMEDIATAMENTE
═══════════════════════════════════════════════════════════════════

Si la imagen muestra PRINCIPALMENTE:
❌ Interiores de casas/edificios → OTROS (id: 10)
❌ Personas/mascotas sin contexto vial → OTROS (id: 10)
❌ Objetos domésticos (sillas, mesas, decoración) → OTROS (id: 10)
❌ Paredes, pisos de casa, jardines privados → OTROS (id: 10)
❌ Comida, productos, selfies → OTROS (id: 10)

⚠️ REGLA CRÍTICA: Si NO hay infraestructura vial visible (calle, vereda, poste, etc.), 
entonces automáticamente es OTROS (id: 10) con confianza baja.

✅ Para ser un INCIDENTE VÁLIDO debe mostrar:
- Infraestructura pública (calles, veredas, postes)
- Deterioro o daño visible en esa infraestructura
- Contexto urbano/vial claro
"""
        # Construir descripción detallada de cada tipo
        tipo_descriptions = []
        for id_tipo, config in self.tipos_incidente.items():
            visual_cues_str = "\n     • ".join(config["visual_cues"])
            neg_examples = ", ".join(config["negative_examples"]) if config["negative_examples"] else "N/A"
            
            tipo_descriptions.append(f"""
{id_tipo}. **{config["nombre"]}**:
   📋 Características visuales:
     • {visual_cues_str}
   ❌ NO confundir con: {neg_examples}
""")
        
        catalogo_detallado = "\n".join(tipo_descriptions)
        
        prompt = f"""Eres un experto en análisis de infraestructura urbana. Analiza esta imagen con MÁXIMA PRECISIÓN.
{no_incident_section}
═══════════════════════════════════════════════════════════════════
📊 INFORMACIÓN DE DETECCIÓN AUTOMÁTICA
═══════════════════════════════════════════════════════════════════

🤖 OBJETOS DETECTADOS (YOLO):
{yolo_info}

🔍 ANÁLISIS VISUAL (OpenCV):
{cv_hints}

═══════════════════════════════════════════════════════════════════
📚 CATÁLOGO COMPLETO DE INCIDENTES (ELEGIR SOLO UNO)
═══════════════════════════════════════════════════════════════════
{catalogo_detallado}

═══════════════════════════════════════════════════════════════════
🎯 PROCESO DE CLASIFICACIÓN - SEGUIR ESTRICTAMENTE
═══════════════════════════════════════════════════════════════════

PASO 1: IDENTIFICAR ELEMENTOS PRINCIPALES
¿Qué veo en la imagen? Identificar:
- Estructuras verticales → ¿Son POSTES (metal/concreto) o ÁRBOLES (madera/follaje)?
- Deterioro en suelo → ¿Es en PISTA (centro) o VEREDA (borde peatonal)?
- Líquidos → ¿Es AGUA FLUYENDO (tubería rota) o CHARCO (lluvia)?
- Objetos → ¿Es BASURA ACUMULADA o un objeto aislado?

PASO 2: VALIDAR CON CARACTERÍSTICAS VISUALES
Comparar lo identificado con las "Características visuales" del catálogo.
✅ Debe cumplir AL MENOS 2 características del tipo elegido.
❌ Descartar si coincide con "NO confundir con".

PASO 3: CONSIDERAR DETECCIONES AUTOMÁTICAS
- Si OpenCV/YOLO tienen confianza >0.70, CONSIDERAR SERIAMENTE esa categoría.
- Si hay conflicto, PRIORIZAR evidencia visual directa sobre métricas.

PASO 4: APLICAR REGLAS DE DESAMBIGUACIÓN ESPECÍFICAS

🔴 ÁRBOL vs POSTE:
   → ÁRBOL: Tiene ramas, follaje, corteza natural irregular
   → POSTE: Cilindro uniforme, metal/concreto, cables eléctricos

🔴 BACHE vs FUGA DE AGUA:
   → BACHE: Hueco seco en asfalto, depresión sin líquido activo
   → FUGA: Agua FLUYENDO, tubería visible, suelo mojado extenso

🔴 BACHES vs PISTAS CON HUECOS:
   → BACHES: 1-2 huecos pequeños aislados
   → PISTAS CON HUECOS: 3+ huecos o deterioro extenso (>5m²)

🔴 VEREDA vs PISTA:
   → VEREDA: Área peatonal (bordes, aceras)
   → PISTA: Área vehicular (centro de calle)

PASO 5: ASIGNAR NIVEL DE RIESGO
- Bajo (1): Sin peligro inmediato (basura, baches muy pequeños)
- Medio (2): Inconvenientes menores (vereda rajada, alumbrado)
- Alto (3): Peligro potencial urgente (poste inclinado, pistas deterioradas)
- Crítico (4): Peligro INMEDIATO (poste caído en vía, fuga grande, árbol bloqueando)

═══════════════════════════════════════════════════════════════════
📤 FORMATO DE RESPUESTA - SEGUIR EXACTAMENTE
═══════════════════════════════════════════════════════════════════

RESPONDE SOLO CON JSON PURO (sin texto antes o después, sin markdown):

{{
    "id_tipo_incidente": <NÚMERO del 1 al 10>,
    "tipo_incidente": "<NOMBRE EXACTO de la lista arriba>",
    "id_clasificacion": <NÚMERO del 1 al 4>,
    "clasificacion": "<EXACTAMENTE: 'Riesgo Bajo' o 'Riesgo Medio' o 'Riesgo Alto' o 'Riesgo Crítico'>",
    "descIA": "<descripción max 80 caracteres del elemento PRINCIPAL>",
    "confianza": <NÚMERO entre 0.0 y 1.0, sin comillas>,
    "razonamiento": "<1-2 líneas explicando por qué elegiste esta categoría>"
}}

✅ EJEMPLO CORRECTO de BACHES:
{{
    "id_tipo_incidente": 1,
    "tipo_incidente": "BACHES",
    "id_clasificacion": 2,
    "clasificacion": "Riesgo Medio",
    "descIA": "Hueco aislado de 30cm en asfalto, centro de calzada",
    "confianza": 0.85,
    "razonamiento": "Se observa un hueco individual en el asfalto sin deterioro extenso"
}}

✅ EJEMPLO CORRECTO de PISTAS CON HUECOS:
{{
    "id_tipo_incidente": 4,
    "tipo_incidente": "PISTAS CON HUECOS",
    "id_clasificacion": 3,
    "clasificacion": "Riesgo Alto",
    "descIA": "Múltiples huecos en carretera, deterioro extenso",
    "confianza": 0.90,
    "razonamiento": "Se observan 5+ huecos distribuidos en un área grande de la pista"
}}

✅ EJEMPLO CORRECTO de ÁRBOL CAÍDO:
{{
    "id_tipo_incidente": 9,
    "tipo_incidente": "ÁRBOL CAÍDO",
    "id_clasificacion": 4,
    "clasificacion": "Riesgo Crítico",
    "descIA": "Tronco con ramas bloqueando vía completamente",
    "confianza": 0.95,
    "razonamiento": "Se ve claramente un árbol con follaje y ramas bloqueando la calle"
}}

⚠️ ERRORES COMUNES A EVITAR:
❌ NO pongas el nombre de otro incidente en "clasificacion" (ej: "clasificacion": "BACHES")
❌ NO pongas "confianza" como string (ej: "0.85" ❌) → debe ser número: 0.85 ✅
❌ NO confundas el ID con el nombre (ej: id_tipo_incidente: 3 pero tipo_incidente: "PISTAS CON HUECOS" ❌)
    → Siempre verifica que el ID corresponda al nombre en la lista del catálogo

⚠️ RECORDATORIOS FINALES:
2. El campo "clasificacion" SOLO puede ser: "Riesgo Bajo", "Riesgo Medio", "Riesgo Alto", o "Riesgo Crítico"
3. La descripción debe mencionar EL ELEMENTO PRINCIPAL identificado
4. La confianza es un NÚMERO decimal (0.0 a 1.0), NO un string
5. Si dudas entre 2 tipos, elige el de MAYOR RIESGO y baja la confianza a 0.6-0.7
"""
        return prompt
    
    def _prefilter_with_yolo(self, yolo_detections: List[Dict]) -> Dict:
        """Pre-filtra casos obvios que NO son incidentes"""
    
    # Objetos que indican que NO es un incidente vial
        non_incident_objects = {
        'person', 'cat', 'dog', 'bird', 'chair', 'dining table',
        'couch', 'bed', 'tv', 'laptop', 'mouse', 'keyboard',
        'cell phone', 'book', 'clock', 'vase', 'scissors',
        'teddy bear', 'toothbrush', 'fork', 'knife', 'spoon'
    }
    
    # Objetos que SÍ indican contexto vial
        vial_objects = {
        'car', 'truck', 'bus', 'motorcycle', 'bicycle',
        'traffic light', 'stop sign', 'parking meter', 'fire hydrant'
    }
    
        detected_classes = [d['class'] for d in yolo_detections]
    
        has_vial = any(obj in vial_objects for obj in detected_classes)
        has_non_incident = any(obj in non_incident_objects for obj in detected_classes)
    
    # Si hay muchos objetos no-incidente y ninguno vial
        if has_non_incident and not has_vial:
            return {
                "is_likely_non_incident": True,
                "reason": f"Detectados objetos no-viales: {', '.join(set(detected_classes) & non_incident_objects)}"
            }
    
        return {"is_likely_non_incident": False}
    
    def classify_incident(self, image_path: str, yolo_detections: List[Dict]) -> str:
        """Clasifica con validación cruzada y mejor prompt"""
        prefilter = self._prefilter_with_yolo(yolo_detections)
    
        if prefilter["is_likely_non_incident"]:
            print(f"⚠️ PRE-FILTRO: {prefilter['reason']}")
            return json.dumps({
            "id_tipo_incidente": 10,
            "tipo_incidente": "OTROS",
            "id_clasificacion": 1,
            "clasificacion": "Riesgo Bajo",
            "descIA": f"No es incidente vial: {prefilter['reason'][:60]}",
            "confianza": 0.95,
            "razonamiento": "Imagen no contiene infraestructura vial"
        }, ensure_ascii=False)
        
        # Análisis OpenCV
        cv_features = self.analyzer.analyze_image(image_path)
        cv_hints = self.analyzer.get_hints_for_ollama(cv_features)
        
        print("\n" + "="*70)
        print("📸 ANÁLISIS OPENCV:")
        print("="*70)
        print(cv_hints)
        print("="*70 + "\n")
        
        # Preparar imagen
        with open(image_path, "rb") as img_file:
            img_base64 = base64.b64encode(img_file.read()).decode('utf-8')
        
        # Info YOLO
        yolo_info = "No se detectaron objetos específicos"
        if yolo_detections:
            yolo_info = ', '.join([
                f"{item['class']} (conf: {item['confidence']:.2f})" 
                for item in yolo_detections
            ])
        
        # Generar prompt detallado
        prompt = self.generate_detailed_prompt(yolo_info, cv_hints)
        
        # Configuración optimizada
        payload = {
            "model": self.model,
            "prompt": prompt,
            "images": [img_base64],
            "stream": False,
            "format": "json",
            "options": {
                "temperature": 0.2,  # ✅ MÁS determinístico (antes 0.3)
                "top_p": 0.85,       # ✅ MÁS conservador
                "top_k": 30,         # ✅ MÁS restrictivo
                "num_predict": 300   # Más espacio para razonamiento
            }
        }
        
        try:
            print(f"🤖 Enviando a Ollama modelo: {self.model}")
            response = requests.post(self.ollama_url, json=payload, timeout=180)
            response.raise_for_status()
            result = response.json()
            response_text = result.get("response", "{}")
            
            print("\n" + "="*70)
            print("🤖 RESPUESTA OLLAMA:")
            print("="*70)
            print(response_text[:500] + "..." if len(response_text) > 500 else response_text)
            print("="*70 + "\n")
            
            # Parsear JSON
            data = self._parse_and_validate(response_text)
            
            # Mostrar razonamiento si existe
            if "razonamiento" in data:
                print(f"💭 Razonamiento: {data['razonamiento']}")
            
            return json.dumps(data, ensure_ascii=False)
            
        except Exception as e:
            print(f"❌ Error con {self.model}: {e}")
            
            # Intentar con modelo fallback
            if self.model != self.fallback_model:
                print(f"🔄 Intentando con modelo fallback: {self.fallback_model}")
                payload["model"] = self.fallback_model
                try:
                    response = requests.post(self.ollama_url, json=payload, timeout=180)
                    result = response.json()
                    data = self._parse_and_validate(result.get("response", "{}"))
                    return json.dumps(data, ensure_ascii=False)
                except Exception as e2:
                    print(f"❌ Fallback también falló: {e2}")
            
            # Respuesta por defecto
            return json.dumps({
                "id_tipo_incidente": 10,
                "tipo_incidente": "OTROS",
                "id_clasificacion": 1,
                "clasificacion": "Riesgo Bajo",
                "descIA": f"Error en clasificación: {str(e)[:50]}",
                "confianza": 0.01,
                "razonamiento": "Sistema de clasificación falló"
            }, ensure_ascii=False)
    
    def _parse_and_validate(self, response_text: str) -> dict:
        """Parsea y valida la respuesta JSON con corrección de errores"""
        # Extraer JSON
        json_match = re.search(r'\{.*\}', response_text, re.DOTALL)
        if not json_match:
            raise ValueError("No se encontró JSON en la respuesta")
        
        data = json.loads(json_match.group(0))
        
        print(f"📋 Datos recibidos de Ollama: {data}")
        
        # ✅ Validar y normalizar ID de tipo de incidente
        id_original = data.get("id_tipo_incidente", 10)
        tipo_original = data.get("tipo_incidente", "OTROS")
        
        # Normalizar nombre (corrige inconsistencias)
        id_corregido, nombre_correcto = self.normalize_type_name(tipo_original, id_original)
        
        if id_corregido != id_original or nombre_correcto != tipo_original:
            print(f"⚠️ Corrección tipo: '{tipo_original}' (ID:{id_original}) → '{nombre_correcto}' (ID:{id_corregido})")
        
        # ✅ Validar y normalizar clasificacion (nivel de riesgo)
        id_clasificacion_raw = data.get("id_clasificacion", 1)
        clasificacion_raw = data.get("clasificacion", "")
        
        # Si "clasificacion" no es un nivel de riesgo válido, intentar corregir
        id_clasificacion = self._fix_clasificacion(id_clasificacion_raw, clasificacion_raw)
        
        # ✅ Normalizar confianza (puede venir como string)
        confianza_raw = data.get("confianza", 0.5)
        try:
            confianza = float(confianza_raw)
        except (ValueError, TypeError):
            print(f"⚠️ Confianza inválida: '{confianza_raw}' → usando 0.5")
            confianza = 0.5
        
        confianza = min(max(confianza, 0.0), 1.0)
        
        validated_data = {
            "id_tipo_incidente": id_corregido,
            "tipo_incidente": nombre_correcto,
            "id_clasificacion": id_clasificacion,
            "clasificacion": self.tipos_clasificacion[id_clasificacion],
            "descIA": data.get("descIA", "Incidente detectado")[:80],
            "confianza": confianza,
            "razonamiento": data.get("razonamiento", "")
        }
        
        print(f"✅ Datos validados: {validated_data}")
        
        return validated_data
    
    def _fix_clasificacion(self, id_raw, clasificacion_str: str) -> int:
        """
        Corrige el ID de clasificación si viene mal
        """
        # Intentar usar el ID si es válido
        try:
            id_val = int(id_raw)
            if 1 <= id_val <= 4:
                return id_val
        except (ValueError, TypeError):
            pass
        
        # Si el ID es inválido, intentar deducir del string
        clasificacion_lower = str(clasificacion_str).lower()
        
        if any(word in clasificacion_lower for word in ["bajo", "baja", "low"]):
            print(f"⚠️ Corrigiendo clasificación: '{clasificacion_str}' → Riesgo Bajo (1)")
            return 1
        elif any(word in clasificacion_lower for word in ["medio", "media", "medium"]):
            print(f"⚠️ Corrigiendo clasificación: '{clasificacion_str}' → Riesgo Medio (2)")
            return 2
        elif any(word in clasificacion_lower for word in ["alto", "alta", "high"]):
            print(f"⚠️ Corrigiendo clasificación: '{clasificacion_str}' → Riesgo Alto (3)")
            return 3
        elif any(word in clasificacion_lower for word in ["crítico", "critico", "critical"]):
            print(f"⚠️ Corrigiendo clasificación: '{clasificacion_str}' → Riesgo Crítico (4)")
            return 4
        
        # Default: Riesgo Bajo
        print(f"⚠️ No se pudo determinar clasificación de '{clasificacion_str}' → Riesgo Bajo (1)")
        return 1
    
    def normalize_type_name(self, tipo_incidente: str, id_incidente: int) -> Tuple[int, str]:
        """Normaliza y valida el tipo de incidente con prioridad al ID"""
        tipo_lower = tipo_incidente.lower().strip()
        
        # ✅ PRIORIDAD 1: Si el ID es válido, usar ese
        if 1 <= id_incidente <= 10:
            nombre_correcto = self.tipos_incidente[id_incidente]["nombre"]
            if nombre_correcto.lower() == tipo_lower:
                # ID y nombre coinciden perfectamente
                return (id_incidente, nombre_correcto)
            else:
                # ID válido pero nombre no coincide → CORREGIR nombre basado en ID
                print(f"⚠️ ID {id_incidente} válido pero nombre '{tipo_incidente}' no coincide con '{nombre_correcto}'")
                return (id_incidente, nombre_correcto)
        
        # ✅ PRIORIDAD 2: Buscar por matching de nombre/keywords
        mejor_match = None
        max_score = 0
        
        for id_tipo, config in self.tipos_incidente.items():
            score = 0
            
            # Match exacto de nombre
            if config["nombre"].lower() == tipo_lower:
                score += 10
            
            # Match por keywords
            for keyword in config["keywords"]:
                if keyword in tipo_lower:
                    score += 2
            
            # Penalizar si coincide con negative_examples
            for neg in config.get("negative_examples", []):
                if neg in tipo_lower:
                    score -= 3
            
            if score > max_score:
                max_score = score
                mejor_match = (id_tipo, config["nombre"])
        
        if mejor_match and max_score >= 2:
            return mejor_match
        
        # ✅ PRIORIDAD 3: Default OTROS
        return (10, "OTROS")