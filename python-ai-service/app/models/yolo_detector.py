import torch
from ultralytics import YOLO
import warnings
from typing import List, Dict

# Suprimir warnings de PyTorch
warnings.filterwarnings('ignore', category=UserWarning)

class YOLODetector:
    def __init__(self, model_path="yolov8n.pt"):
        self.model = YOLO(model_path)
        
        # Clases relevantes
        self.relevant_classes = {
            'person', 'bicycle', 'car', 'motorcycle', 'bus', 'truck',
            'traffic light', 'fire hydrant', 'stop sign', 'parking meter',
            'bench', 'bird', 'cat', 'dog', 'backpack', 'umbrella',
            'handbag', 'tie', 'suitcase', 'bottle', 'chair', 'potted plant'
        }

    def detect(self, image_path: str, confidence_threshold: float = 0.3) -> list:
        """Detecta objetos en una imagen"""
        print(f"Detectando objetos en: {image_path}")
        
        results = self.model(image_path, conf=confidence_threshold)
        
        detections = []
        for result in results:
            boxes = result.boxes
            for box in boxes:
                class_id = int(box.cls[0])
                class_name = self.model.names[class_id]
                confidence = float(box.conf[0])
                
                if class_name in self.relevant_classes:
                    detections.append({
                        "class": class_name,
                        "confidence": confidence,
                        "bbox": box.xyxy[0].tolist()
                    })
        
        print(f"✓ Se detectaron {len(detections)} objetos relevantes")
        return detections
