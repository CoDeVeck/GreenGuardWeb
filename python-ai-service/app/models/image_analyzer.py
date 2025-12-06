import cv2
import numpy as np
from typing import Dict, List

class ImageAnalyzer:
    """
    Analiza imágenes con OpenCV para detectar características específicas
    que ayuden a la clasificación de Ollama
    """
    
    def __init__(self):
        self.features = {}
    
    def analyze_image(self, image_path: str) -> Dict:
        """
        Analiza la imagen y retorna características detectadas
        """
        img = cv2.imread(image_path)
        if img is None:
            return {"error": "No se pudo leer la imagen"}
        
        features = {
            "tilted_lines": self._detect_tilted_structures(img),
            "dark_regions": self._detect_dark_regions(img),
            "ground_defects": self._detect_ground_defects(img),
            "sidewalk_cracks": self._detect_sidewalk_cracks(img),
            "scattered_objects": self._detect_scattered_objects(img)
        }
        
        return features
    
    def _detect_tilted_structures(self, img) -> Dict:
        """ Detecta postes inclinados o estructuras verticales anormales """
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        edges = cv2.Canny(gray, 50, 150, apertureSize=3)
        lines = cv2.HoughLinesP(edges, 1, np.pi/180, 100, minLineLength=100, maxLineGap=10)
        
        tilted_count = 0
        vertical_count = 0
        
        if lines is not None:
            for line in lines:
                x1, y1, x2, y2 = line[0]
                if x2 - x1 == 0:
                    angle = 90
                else:
                    angle = abs(np.degrees(np.arctan((y2 - y1) / (x2 - x1))))
                
                if 80 <= angle <= 100:
                    vertical_count += 1
                elif 30 <= angle <= 75:
                    tilted_count += 1
        
        has_tilted = tilted_count > 2 and tilted_count > vertical_count * 0.3
        
        return {
            "detected": has_tilted,
            "tilted_count": tilted_count,
            "vertical_count": vertical_count,
            "confidence": min(tilted_count / 10, 1.0) if has_tilted else 0.0,
            "hint": "Posible POSTE CAÍDO" if has_tilted else None
        }
    
    def _detect_dark_regions(self, img) -> Dict:
        """ Detecta regiones oscuras (posible alumbrado público deficiente) """
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        
        avg_brightness = np.mean(gray)
        dark_pixels = np.sum(gray < 50)
        total_pixels = gray.size
        dark_ratio = dark_pixels / total_pixels
        
        # Umbral de detección ajustado para ser más sensible
        is_dark = avg_brightness < 90 and dark_ratio > 0.25 
        
        return {
            "detected": is_dark,
            "avg_brightness": float(avg_brightness),
            "dark_ratio": float(dark_ratio),
            "confidence": 1.0 - (avg_brightness / 255) if is_dark else 0.0,
            "hint": "Posible ALUMBRADO PÚBLICO deficiente" if is_dark else None
        }
    
    def _detect_ground_defects(self, img) -> Dict:
        """ Detecta baches, huecos o irregularidades en la pista/calle (mitad inferior) """
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        
        height = img.shape[0]
        ground_region = gray[int(height * 0.5):, :]
        
        _, thresh = cv2.threshold(ground_region, 70, 255, cv2.THRESH_BINARY_INV)
        contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        significant_holes = []
        for cnt in contours:
            area = cv2.contourArea(cnt)
            if 1000 < area < 100000: 
                significant_holes.append(area)
        
        has_holes = len(significant_holes) > 0
        
        return {
            "detected": has_holes,
            "hole_count": len(significant_holes),
            "total_area": sum(significant_holes) if has_holes else 0,
            "confidence": min(len(significant_holes) / 5, 1.0) if has_holes else 0.0,
            "hint": "Posibles BACHES o PISTAS CON HUECOS" if has_holes else None
        }
        
    def _detect_sidewalk_cracks(self, img) -> Dict:
        """ Detecta grietas finas o deterioro específico en la vereda/acera """
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        height, width = img.shape[:2]
        
        roi_left = gray[int(height * 0.6):, :int(width * 0.3)]
        roi_right = gray[int(height * 0.6):, int(width * 0.7):]
        
        total_cracks = 0
        
        for roi in [roi_left, roi_right]:
            if roi.size == 0: continue
            
            roi_eq = cv2.equalizeHist(roi)
            edges = cv2.Canny(roi_eq, 10, 50) 
            total_cracks += np.sum(edges > 0)
        
        has_cracks = total_cracks > (height * width * 0.005)
        
        return {
            "detected": has_cracks,
            "crack_intensity": int(total_cracks),
            "confidence": min(total_cracks / (height * width * 0.01), 1.0) if has_cracks else 0.0,
            "hint": "Posible VEREDA RAJADA (múltiples grietas finas)" if has_cracks else None
        }

    
    def _detect_scattered_objects(self, img) -> Dict:
        """ Detecta objetos dispersos (posible basura acumulada) """
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
        
        color_ranges = [
            ([0, 100, 100], [10, 255, 255]), # Rojo
            ([40, 100, 100], [80, 255, 255]), # Verde
            ([20, 100, 100], [30, 255, 255]), # Amarillo
        ]
        
        total_colored_pixels = 0
        for (lower, upper) in color_ranges:
            mask = cv2.inRange(hsv, np.array(lower), np.array(upper))
            total_colored_pixels += np.sum(mask > 0)
        
        total_pixels = img.shape[0] * img.shape[1]
        colored_ratio = total_colored_pixels / total_pixels
        
        # Umbral de detección ajustado para ser más sensible
        has_scattered = colored_ratio > 0.03 
        
        return {
            "detected": has_scattered,
            "colored_ratio": float(colored_ratio),
            "confidence": min(colored_ratio * 10, 1.0) if has_scattered else 0.0,
            "hint": "Posible BASURA ACUMULADA" if has_scattered else None
        }
    
    def get_hints_for_ollama(self, features: Dict) -> str:
        """
        Genera pistas textuales para ayudar a Ollama, incluyendo las métricas clave 
        incluso si la detección principal es False.
        """
        hints = []
        
        # 1. POSTE CAÍDO
        if features["tilted_lines"]["detected"]:
            hints.append(f"⚠️ DETECCIÓN OpenCV: {features['tilted_lines']['hint']} "
                         f"(confianza: {features['tilted_lines']['confidence']:.2f})")
        
        # 2. ALUMBRADO PÚBLICO (Reportar métrica y detección)
        brightness = features["dark_regions"]["avg_brightness"]
        if features["dark_regions"]["detected"]:
            hints.append(f"🌙 DETECCIÓN OpenCV: {features['dark_regions']['hint']} "
                         f"(brillo: {brightness:.0f}/255, < 90/255)")
        else:
             hints.append(f"🌙 MÉTRICA OpenCV: Brillo promedio de la imagen: {brightness:.0f}/255")

        # 3. VEREDA RAJADA
        if features["sidewalk_cracks"]["detected"]:
             hints.append(f"🩹 DETECCIÓN OpenCV: {features['sidewalk_cracks']['hint']} "
                           f"(intensidad: {features['sidewalk_cracks']['crack_intensity']})")
            
        # 4. BACHES/PISTAS CON HUECOS
        if features["ground_defects"]["detected"]:
            hints.append(f"🕳️ DETECCIÓN OpenCV: {features['ground_defects']['hint']} "
                         f"({features['ground_defects']['hole_count']} irregularidades detectadas)")
        
        # 5. BASURA ACUMULADA (Reportar métrica y detección)
        colored_ratio = features["scattered_objects"]["colored_ratio"]
        if features["scattered_objects"]["detected"]:
            hints.append(f"🗑️ DETECCIÓN OpenCV: {features['scattered_objects']['hint']} "
                         f"(objetos dispersos: {colored_ratio*100:.1f}%)")
        else:
            hints.append(f"🗑️ MÉTRICA OpenCV: Objetos dispersos (basura potencial): {colored_ratio*100:.1f}%")

        if not any(h.startswith(("⚠️", "🌙 DETECCIÓN", "🩹", "🕳️", "🗑️ DETECCIÓN")) for h in hints):
            hints.append("ℹ️ No se detectaron características específicas con OpenCV")
        
        return "\n".join(hints)