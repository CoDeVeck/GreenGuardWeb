from fastapi import APIRouter, File, UploadFile, HTTPException
from pydantic import BaseModel
import json
import tempfile
import os
import re

from app.models.yolo_detector import YOLODetector
from app.models.ollama_classifier import OllamaClassifier
import traceback

router = APIRouter()

yolo_detector = YOLODetector()
ollama_classifier = OllamaClassifier()

class ClassificationResponse(BaseModel):
    idTipoIncidente: int          # ✅ ID para Spring
    tipoIncidente: str
    idClasificacion: int          # ✅ ID para Spring
    clasificacion: str
    nivelRiesgo: str              # Alias de clasificacion
    puntosEstimados: int
    descIA: str
    confianza: float

def extract_json_from_response(text: str) -> dict:
    """Extrae el JSON válido de una respuesta de Ollama"""
    try:
        return json.loads(text)
    except json.JSONDecodeError:
        pass
    
    try:
        start = text.find('{')
        end = text.rfind('}') + 1
        
        if start != -1 and end > start:
            json_str = text[start:end]
            return json.loads(json_str)
    except json.JSONDecodeError:
        pass
    
    try:
        json_pattern = r'\{(?:[^{}]|\{[^{}]*\})*\}'
        matches = re.findall(json_pattern, text, re.DOTALL)
        
        for match in matches:
            try:
                parsed = json.loads(match)
                if "id_tipo_incidente" in parsed and "id_clasificacion" in parsed:
                    return parsed
            except json.JSONDecodeError:
                continue
    except Exception:
        pass
    
    raise ValueError(f"No se pudo extraer JSON válido")

def calcular_puntos(id_clasificacion: int) -> int:
    """Calcula puntos según ID de clasificación"""
    puntos_map = {
        1: 25,   # Riesgo Bajo
        2: 55,   # Riesgo Medio
        3: 100,  # Riesgo Alto
        4: 150   # Riesgo Crítico
    }
    return puntos_map.get(id_clasificacion, 50)

@router.post("/classify", response_model=ClassificationResponse)
async def classify_image(file: UploadFile = File(...)):
    """Clasifica una imagen de incidente usando YOLO + Ollama"""
    temp_path = None
    try:
        with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as temp_file:
            content = await file.read()
            temp_file.write(content)
            temp_path = temp_file.name
        
        # 1. Detectar objetos con YOLO
        yolo_detections = yolo_detector.detect(temp_path)
        
        # 2. Clasificar con Ollama
        ollama_response = ollama_classifier.classify_incident(temp_path, yolo_detections)
        print("Respuesta completa de Ollama:", ollama_response)
        
        # 3. Extraer y parsear JSON
        classification_data = extract_json_from_response(ollama_response)
        print("JSON extraído:", json.dumps(classification_data, indent=2))
        
        # 4. Calcular puntos según ID de clasificación
        puntos = calcular_puntos(classification_data["id_clasificacion"])
        
        # 5. Construir respuesta
        response = ClassificationResponse(
            idTipoIncidente=classification_data["id_tipo_incidente"],
            tipoIncidente=classification_data["tipo_incidente"],
            idClasificacion=classification_data["id_clasificacion"],
            clasificacion=classification_data["clasificacion"],
            nivelRiesgo=classification_data["clasificacion"],  # Alias
            puntosEstimados=puntos,
            descIA=classification_data.get("descIA", ""),
            confianza=classification_data.get("confianza", 0.0)
        )
        
        return response
        
    except Exception as e:
        print("=== ERROR EN /api/classify ===")
        print(f"Tipo de error: {type(e).__name__}")
        print(f"Mensaje: {str(e)}")
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Error en clasificación: {str(e)}")
    
    finally:
        if temp_path and os.path.exists(temp_path):
            try:
                os.unlink(temp_path)
            except Exception as e:
                print(f"Error eliminando archivo temporal: {e}")