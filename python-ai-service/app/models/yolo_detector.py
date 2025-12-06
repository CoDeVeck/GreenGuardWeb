import torch
from ultralytics import YOLO
import warnings
from typing import List, Dict

# Suprimir warnings de PyTorch
warnings.filterwarnings('ignore', category=UserWarning)

class YOLODetector:
    def __init__(self, model_path="yolov8n.pt"):
        """
        Inicializa el detector YOLO, seleccionando el mejor dispositivo (CPU/GPU).
        """
        print(f"Inicializando YOLODetector con modelo: {model_path}")
        
        # Detección y asignación del dispositivo
        self.device = 'cuda' if torch.cuda.is_available() else 'cpu'
        print(f"Usando dispositivo: {self.device}")
        
        try:
            self.model = YOLO(model_path)
            self.model.to(self.device)
            print(f"✓ Modelo {model_path} cargado exitosamente en {self.device}")
        except Exception as e:
            print(f"✗ Error al cargar modelo: {e}")
            raise
        
    def detect(self, image_path: str, conf_thres: float = 0.5, iou_thres: float = 0.45) -> List[Dict]:
        """
        Detecta objetos en la imagen con umbrales de confianza e IOU.

        :param image_path: Ruta al archivo de imagen.
        :param conf_thres: Umbral de confianza (detecciones con conf < conf_thres se descartan).
        :param iou_thres: Umbral de IOU para la Supresión No Máxima (NMS).
        :return: Lista de diccionarios con las detecciones.
        """
        print(f"Detectando objetos en: {image_path} (Conf: {conf_thres})")
        
        try:
            # Pasar argumentos de optimización y dispositivo
            results = self.model(image_path, 
                                 conf=conf_thres, 
                                 iou=iou_thres, 
                                 device=self.device,
                                 verbose=False)
            
            detections = []
            for result in results:
                for box in result.boxes:
                    detection = {
                        "class": result.names[int(box.cls)],
                        "confidence": float(box.conf),
                        "bbox": box.xyxy[0].tolist()
                    }
                    detections.append(detection)
            
            print(f"✓ Se detectaron {len(detections)} objetos")
            return detections
            
        except Exception as e:
            print(f"✗ Error en detección: {e}")
            return []