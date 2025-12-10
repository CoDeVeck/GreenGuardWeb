import base64
import cv2
import requests
import json
import re
from typing import List, Dict, Tuple
from pathlib import Path

class FewShotClassifier:
    """
    Clasificador que usa imágenes de referencia (few-shot learning)
    para mejorar la precisión sin necesidad de fine-tuning
    """
    
    def __init__(self, reference_images_dir: str = "reference_images"):
        self.ollama_url = "http://localhost:11434/api/generate"
        self.model = "llava"
        self.reference_dir = Path(reference_images_dir)
        
        # Crear carpeta de referencias si no existe
        self.reference_dir.mkdir(exist_ok=True)
        
        self.tipos_incidente = {
            1: {"nombre": "BACHES", "ref_file": "bache_ejemplo.jpg", 
                "keywords": ["hueco", "asfalto", "calle", "pista"]}, # ✅ Agregado
            2: {"nombre": "POSTE CAÍDO", "ref_file": "poste_caido_ejemplo.jpg", 
                "keywords": ["poste", "cable", "tierra"]}, # ✅ Agregado
            3: {"nombre": "VEREDA RAJADA", "ref_file": "vereda_rajada_ejemplo.jpg", 
                "keywords": ["vereda", "acera", "rajada", "grieta"]}, # ✅ Agregado
            4: {"nombre": "PISTAS CON HUECOS", "ref_file": "pistas_huecos_ejemplo.jpg", 
                "keywords": ["multiples", "huecos", "asfalto", "pista"]}, # ✅ Agregado
            5: {"nombre": "SEMÁFORO MALOGRADO", "ref_file": "semaforo_ejemplo.jpg", 
                "keywords": ["semáforo", "luz", "tráfico", "apagado"]}, # ✅ Agregado
            6: {"nombre": "FUGA DE AGUA", "ref_file": "fuga_agua_ejemplo.jpg", 
                "keywords": ["agua", "tubo", "fuga", "chorro"]}, # ✅ Agregado
            7: {"nombre": "BASURA ACUMULADA", "ref_file": "basura_ejemplo.jpg", 
                "keywords": ["basura", "bolsas", "acumulada", "desecho"]}, # ✅ Agregado
            8: {"nombre": "ALUMBRADO PÚBLICO", "ref_file": "alumbrado_ejemplo.jpg", 
                "keywords": ["farola", "oscura", "noche", "luz"]}, # ✅ Agregado
            9: {"nombre": "ÁRBOL CAÍDO", "ref_file": "arbol_caido_ejemplo.jpg", 
                "keywords": ["árbol", "ramas", "tronco", "follaje"]}, # ✅ Agregado
            10: {"nombre": "OTROS", "ref_file": None, 
                 "keywords": ["indefinido", "otro"]} # ✅ Agregado
        }
        
        self.tipos_clasificacion = {
            1: "Riesgo Bajo",
            2: "Riesgo Medio",
            3: "Riesgo Alto",
            4: "Riesgo Crítico"
        }
    
    def _load_reference_image(self, filename: str) -> str:
        """Carga imagen de referencia como base64"""
        try:
            ref_path = self.reference_dir / filename
            if ref_path.exists():
                with open(ref_path, "rb") as f:
                    return base64.b64encode(f.read()).decode('utf-8')
        except Exception as e:
            print(f"⚠️ No se pudo cargar {filename}: {e}")
        return None
    
    def classify_with_examples(self, image_path: str, 
                               candidate_types: List[int] = None) -> Dict:
        """
        Clasifica mostrando ejemplos visuales al modelo
        
        Args:
            image_path: Ruta de la imagen a clasificar
            candidate_types: IDs de tipos candidatos (si None, usa todos)
        """
        
        # Cargar imagen a clasificar COMPRIMIDA
        img = cv2.imread(image_path)
        if img is None:
            return self._fallback_classification()
        
        # Comprimir imagen target
        height, width = img.shape[:2]
        max_dim = 640
        if height > max_dim or width > max_dim:
            scale = max_dim / max(height, width)
            new_width = int(width * scale)
            new_height = int(height * scale)
            img = cv2.resize(img, (new_width, new_height), interpolation=cv2.INTER_AREA)
        
        encode_param = [int(cv2.IMWRITE_JPEG_QUALITY), 85]
        _, buffer = cv2.imencode('.jpg', img, encode_param)
        target_image = base64.b64encode(buffer).decode('utf-8')
        
        # Si no hay candidatos, usar todos
        if not candidate_types:
            candidate_types = list(range(1, 10))
        
        print(f"\n🔍 Comparando contra {len(candidate_types)} categorías...")
        
        # Comparar contra cada tipo candidato
        results = []
        
        for tipo_id in candidate_types:
            config = self.tipos_incidente[tipo_id]
            ref_file = config.get("ref_file")
            
            if not ref_file or tipo_id == 10:
                continue
            
            ref_image = self._load_reference_image(ref_file)
            
            if not ref_image:
                print(f"⚠️ Saltando {config['nombre']}: sin imagen de referencia")
                continue
            
            # Crear prompt de comparación MEJORADO
            prompt = self._create_comparison_prompt(config["nombre"])
            
            # Enviar AMBAS imágenes
            payload = {
                "model": self.model,
                "prompt": prompt,
                "images": [ref_image, target_image],
                "stream": False,
                "format": "json",
                "options": {
                    "temperature": 0.05,  # ✅ MUY determinístico
                    "top_p": 0.75,
                    "top_k": 15,
                    "num_predict": 150,
                    "num_ctx": 4096
                }
            }
            
            # Retry con backoff
            max_retries = 2
            for attempt in range(max_retries):
                try:
                    response = requests.post(self.ollama_url, json=payload, timeout=180)
                    
                    if response.status_code == 500:
                        if attempt < max_retries - 1:
                            print(f"   ⚠️ Error 500 para {config['nombre']}, reintentando en 3s...")
                            import time
                            time.sleep(3)
                            continue
                        else:
                            raise Exception("Ollama sobrecargado")
                    
                    response.raise_for_status()
                    result = response.json()
                    response_text = result.get("response", "{}")
                    
                    # Parsear respuesta
                    data = self._parse_comparison(response_text)
                    similarity = data.get("similarity", 0.0)
                    
                    results.append({
                        "id": tipo_id,
                        "nombre": config["nombre"],
                        "similarity": similarity,
                        "explanation": data.get("explanation", "")
                    })
                    
                    print(f"   {config['nombre']}: {similarity:.2f}")
                    break
                    
                except Exception as e:
                    if attempt < max_retries - 1:
                        print(f"   ⚠️ Error: {e}, reintentando...")
                        continue
                    else:
                        print(f"❌ Error definitivo con {config['nombre']}: {e}")
                        continue
        
        # Ordenar por similitud
        results.sort(key=lambda x: x["similarity"], reverse=True)
        
        if not results:
            return self._fallback_classification()
        
        # Tomar el mejor match
        best_match = results[0]
        
        # ✅ DESEMPATE: Si hay múltiples con similitud alta (>0.85), usar explicación
        top_matches = [r for r in results if r["similarity"] >= 0.85]
        
        if len(top_matches) > 1:
            print(f"\n⚠️ DESEMPATE: {len(top_matches)} categorías con similitud alta:")
            for match in top_matches:
                print(f"   - {match['nombre']}: {match['similarity']:.2f}")
                print(f"     Explicación: {match['explanation']}")
            
            # Buscar en explicación la palabra clave del tipo
            for match in top_matches:
                explanation_lower = match["explanation"].lower()
                tipo_keywords = self.tipos_incidente[match["id"]]["keywords"]
                
                # Si la explicación menciona la keyword del tipo, ese es el ganador
                if any(kw in explanation_lower for kw in tipo_keywords):
                    print(f"   ✅ Ganador por explicación: {match['nombre']}")
                    best_match = match
                    break
        
        # Si la similitud es muy baja, clasificar como OTROS
        if best_match["similarity"] < 0.4:
            print(f"\n⚠️ Similitud baja ({best_match['similarity']:.2f}), clasificando como OTROS")
            return {
                "id_tipo_incidente": 10,
                "tipo_incidente": "OTROS",
                "id_clasificacion": 1,
                "clasificacion": "Riesgo Bajo",
                "descIA": "No coincide claramente con ningún tipo conocido",
                "confianza": 1.0 - best_match["similarity"],
                "razonamiento": f"Mayor similitud: {best_match['nombre']} ({best_match['similarity']:.2f}), insuficiente"
            }
        
        # Determinar nivel de riesgo
        id_clasificacion = self._determine_risk_level(best_match["id"], best_match["similarity"])
        
        print(f"\n✅ MEJOR MATCH: {best_match['nombre']} (similitud: {best_match['similarity']:.2f})")
        print(f"   Explicación: {best_match['explanation']}")
        
        return {
            "id_tipo_incidente": best_match["id"],
            "tipo_incidente": best_match["nombre"],
            "id_clasificacion": id_clasificacion,
            "clasificacion": self.tipos_clasificacion[id_clasificacion],
            "descIA": best_match["explanation"][:80],
            "confianza": best_match["similarity"],
            "razonamiento": f"Similitud con referencia: {best_match['similarity']:.2f}"
        }
    
    def _create_comparison_prompt(self, tipo_nombre: str) -> str:
        """Prompt ULTRA-ESPECÍFICO para comparar dos imágenes"""
        return f"""Compara estas dos imágenes con MÁXIMA PRECISIÓN:

IMAGEN 1 (REFERENCIA): Ejemplo oficial de "{tipo_nombre}"
IMAGEN 2 (A CLASIFICAR): Imagen que debes evaluar

🎯 PREGUNTA CLAVE: ¿La IMAGEN 2 muestra EXACTAMENTE el mismo tipo de problema que la REFERENCIA?

═══════════════════════════════════════════════════════════════════
🔴 REGLAS ESTRICTAS DE SIMILITUD
═══════════════════════════════════════════════════════════════════

⚠️ DIFERENCIAS CRÍTICAS (similarity debe ser < 0.3):

1. BASURA vs BACHE:
   - Si REF muestra OBJETOS/BOLSAS pero IMG2 muestra HUECOS → 0.1
   - Si REF muestra HUECOS pero IMG2 muestra OBJETOS/BOLSAS → 0.1

2. BACHE vs VEREDA:
   - Si REF es HUECO en PISTA pero IMG2 es GRIETA en VEREDA → 0.2
   - Si REF es GRIETA pero IMG2 es HUECO → 0.2

3. POSTE vs ÁRBOL:
   - Si REF es METAL/CONCRETO pero IMG2 tiene FOLLAJE/RAMAS → 0.1
   - Si REF es MADERA/FOLLAJE pero IMG2 es METAL → 0.1

4. SEMÁFORO vs FAROLA:
   - Si REF tiene LUCES RGB pero IMG2 es LUZ BLANCA → 0.2
   - Si REF es para ILUMINACIÓN pero IMG2 es para TRÁFICO → 0.2

✅ SIMILITUD ALTA (similarity > 0.8):
- Mismo tipo de OBJETO PRINCIPAL
- Mismo tipo de DAÑO/PROBLEMA
- Mismo CONTEXTO (pista, vereda, aérea)

⚖️ SIMILITUD MEDIA (0.4 - 0.7):
- Problema similar pero DIFERENTE SEVERIDAD
- Contexto similar pero DIFERENTES detalles

═══════════════════════════════════════════════════════════════════
📋 PROCESO DE ANÁLISIS
═══════════════════════════════════════════════════════════════════

PASO 1: Identifica el ELEMENTO PRINCIPAL de IMAGEN 2
¿Qué ves? → Basura, hueco, grieta, poste, árbol, agua, etc.

PASO 2: Compara con la REFERENCIA
¿Es el MISMO tipo de elemento? → SÍ/NO

PASO 3: Asigna similitud
- SI es el mismo → 0.8 - 1.0
- SI es parecido pero diferente → 0.4 - 0.7
- SI es completamente diferente → 0.0 - 0.3

═══════════════════════════════════════════════════════════════════
📤 RESPONDE SOLO JSON (sin markdown, sin texto antes/después):
═══════════════════════════════════════════════════════════════════

{{
    "similarity": <número 0.0 a 1.0>,
    "explanation": "IMG2 muestra [DESCRIBE ELEMENTO PRINCIPAL] que [coincide/difiere] con REF"
}}

✅ EJEMPLOS CORRECTOS:

REF: BASURA, IMG2: bolsas de basura
→ {{"similarity": 0.95, "explanation": "IMG2 muestra basura acumulada que coincide con REF"}}

REF: BASURA, IMG2: hueco en pista
→ {{"similarity": 0.15, "explanation": "IMG2 muestra hueco en asfalto que difiere de REF (basura)"}}

REF: BACHES, IMG2: hueco aislado
→ {{"similarity": 0.90, "explanation": "IMG2 muestra hueco en asfalto que coincide con REF"}}

REF: BACHES, IMG2: objetos dispersos
→ {{"similarity": 0.10, "explanation": "IMG2 muestra objetos/basura que difiere de REF (huecos)"}}

REF: VEREDA, IMG2: grietas en acera
→ {{"similarity": 0.88, "explanation": "IMG2 muestra grietas en vereda que coincide con REF"}}

REF: POSTE, IMG2: árbol con ramas
→ {{"similarity": 0.12, "explanation": "IMG2 muestra árbol con follaje que difiere de REF (poste)"}}

RESPONDE SOLO JSON."""
    
    def _parse_comparison(self, response_text: str) -> Dict:
        """Parsea respuesta de comparación"""
        try:
            json_match = re.search(r'\{.*\}', response_text, re.DOTALL)
            if json_match:
                data = json.loads(json_match.group(0))
                similarity = float(data.get("similarity", 0.0))
                return {
                    "similarity": max(0.0, min(1.0, similarity)),
                    "explanation": data.get("explanation", "")
                }
        except Exception as e:
            print(f"⚠️ Error parseando comparación: {e}")
        
        return {"similarity": 0.0, "explanation": "Error en análisis"}
    
    def _determine_risk_level(self, tipo_id: int, similarity: float) -> int:
        """Determina nivel de riesgo basado en tipo y similitud"""
        # Tipos críticos
        if tipo_id in [2, 4, 6, 9]:  # Poste caído, pistas, fuga, árbol
            return 4 if similarity > 0.8 else 3
        
        # Tipos de riesgo alto
        if tipo_id in [3, 5]:  # Vereda, semáforo
            return 3 if similarity > 0.7 else 2
        
        # Tipos de riesgo medio/bajo
        if tipo_id in [1, 7, 8]:  # Baches, basura, alumbrado
            return 2 if similarity > 0.6 else 1
        
        return 1
    
    def _fallback_classification(self) -> Dict:
        """Clasificación por defecto cuando todo falla"""
        return {
            "id_tipo_incidente": 10,
            "tipo_incidente": "OTROS",
            "id_clasificacion": 1,
            "clasificacion": "Riesgo Bajo",
            "descIA": "No se pudo comparar con referencias",
            "confianza": 0.1,
            "razonamiento": "Error en sistema de comparación"
        }
    

class HybridClassifier:
    """
    Combina tu clasificador actual con few-shot learning
    """
    
    def __init__(self):
        from app.models.enhaced_ollama_classifier import EnhancedOllamaClassifier
        
        self.standard_classifier = EnhancedOllamaClassifier()
        self.few_shot_classifier = FewShotClassifier()
    
    def classify(self, image_path: str, yolo_detections: List[Dict]) -> str:
        print("\n" + "="*70)
        print("🔄 ESTRATEGIA: SIEMPRE USA AMBOS MÉTODOS")
        print("="*70)
        
        # 1. Clasificador estándar
        result_standard = self.standard_classifier.classify_incident(image_path, yolo_detections)
        data_standard = json.loads(result_standard)
        
        tipo_standard = data_standard.get("id_tipo_incidente", 10)
        conf_standard = data_standard.get("confianza", 0.0)
        
        print(f"📊 Estándar: {data_standard['tipo_incidente']} (conf: {conf_standard:.2f})")
        
        # 2. Few-shot (siempre)
        candidates = self._get_candidate_types(yolo_detections, tipo_standard)
        result_fewshot = self.few_shot_classifier.classify_with_examples(
            image_path, 
            candidate_types=candidates
        )
        
        tipo_fewshot = result_fewshot.get("id_tipo_incidente", 10)
        conf_fewshot = result_fewshot.get("confianza", 0.0)
        
        print(f"📊 Few-shot: {result_fewshot['tipo_incidente']} (conf: {conf_fewshot:.2f})")
        
        # ✅ 3. GENERAR DESCRIPCIÓN REAL DEL INCIDENTE (independiente de la clasificación)
        descripcion_real = self._generar_descripcion_incidente(image_path, yolo_detections)
        
        # 4. Decisión: ¿Coinciden?
        if tipo_standard == tipo_fewshot:
            print("✅ AMBOS CONCUERDAN → Promediando confianza (prioridad few-shot)")
            # Priorizar few-shot: 70% few-shot + 30% estándar
            data_standard["confianza"] = (conf_fewshot * 0.7) + (conf_standard * 0.3)
            data_standard["descripcion_ia"] = descripcion_real  # ✅ Descripción real
            data_standard["razonamiento"] = (
                f"Coincidencia → Peso mayor al few-shot ({conf_fewshot:.2f})"
            )
            return json.dumps(data_standard, ensure_ascii=False)

        # 5. No coinciden → PRIORIDAD A FEW-SHOT
        print(f"⚠️ DIFIEREN: Estándar dice {data_standard['tipo_incidente']}, "
            f"Few-shot dice {result_fewshot['tipo_incidente']}")

        # 🔥 Regla principal: Few-shot manda si tiene buena similitud
        if conf_fewshot >= 0.45:
            print(f"✅ PRIORIDAD A FEW-SHOT (similaridad suficiente: {conf_fewshot:.2f})")
            result_fewshot["descripcion_ia"] = descripcion_real  # ✅ Descripción real
            return json.dumps(result_fewshot, ensure_ascii=False)

        # Si few-shot es bajo → usa estándar
        print(f"🔹 STANDARD GANA (few-shot bajo: {conf_fewshot:.2f})")
        data_standard["descripcion_ia"] = descripcion_real  # ✅ Descripción real
        return result_standard

    def _generar_descripcion_incidente(self, image_path: str, yolo_detections: List[Dict]) -> str:
        """
        Genera una descripción natural del incidente basada en lo detectado
        """
        # Extraer objetos principales detectados
        objetos_principales = []
        
        for det in yolo_detections:
            clase = det.get('class', '')
            conf = det.get('confidence', 0)
            
            if conf > 0.3:  # Solo objetos con confianza razonable
                if 'poste' in clase.lower() or 'pole' in clase.lower():
                    angulo = det.get('angle', 0)
                    if angulo > 20:
                        objetos_principales.append(f"poste inclinado ({angulo}°)")
                    else:
                        objetos_principales.append("poste")
                elif 'basura' in clase.lower() or 'trash' in clase.lower():
                    objetos_principales.append("acumulación de basura")
                elif 'hueco' in clase.lower() or 'pothole' in clase.lower():
                    objetos_principales.append("bache en la pista")
                elif 'traffic' in clase.lower():
                    objetos_principales.append("semáforo")
                else:
                    objetos_principales.append(clase)
        
        # Construir descripción natural
        if not objetos_principales:
            return "Incidente detectado en la vía pública que requiere atención."
        
        if len(objetos_principales) == 1:
            return f"Se observa {objetos_principales[0]} que requiere atención inmediata."
        elif len(objetos_principales) == 2:
            return f"Se detecta {objetos_principales[0]} junto con {objetos_principales[1]}."
        else:
            items = ", ".join(objetos_principales[:-1])
            return f"Se identifican múltiples problemas: {items} y {objetos_principales[-1]}."

    def _get_candidate_types(self, yolo_detections: List[Dict], 
                            standard_suggestion: int) -> List[int]:
        """Determina tipos candidatos para few-shot basándose en detecciones"""
        candidates = set()
        
        # Analizar detecciones YOLO
        detected_classes = {d['class'].lower() for d in yolo_detections}
        
        # ✅ Priorizar según lo que REALMENTE detectó
        for det in yolo_detections:
            clase = det['class'].lower()
            conf = det.get('confidence', 0)
            
            if conf < 0.3:  # Ignorar detecciones débiles
                continue
                
            # Mapeo más preciso
            if 'poste' in clase or 'pole' in clase:
                angulo = det.get('angle', 0)
                if angulo > 20:
                    candidates.add(6)  # Postes caídos
                else:
                    candidates.add(5)  # Infraestructura dañada
                    
            elif 'basura' in clase or 'trash' in clase:
                candidates.add(7)  # Basura acumulada
                
            elif 'hueco' in clase or 'pothole' in clase or 'bache' in clase:
                candidates.add(1)  # Baches
                
            elif 'traffic' in clase or 'semaforo' in clase:
                candidates.add(5)  # Semáforos
                
            elif any(v in clase for v in ['car', 'truck', 'bus', 'vehicle']):
                candidates.add(4)  # Pistas con huecos
        
        # Solo agregar sugerencia estándar si no hay candidatos fuertes
        if not candidates or len(candidates) < 2:
            candidates.add(standard_suggestion)
        
        # Limitar a 3 candidatos más relevantes
        return list(candidates)[:3]
