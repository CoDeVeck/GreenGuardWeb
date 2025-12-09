import cv2
import numpy as np
from typing import Dict, List

class ImageAnalyzer:
    """
    Versión mejorada con detección más precisa para todas las categorías
    """
    
    def __init__(self):
        self.debug_mode = False  # Cambiar a True para ver imágenes intermedias
    
    def analyze_image(self, image_path: str) -> Dict:
        """Analiza la imagen con métodos mejorados"""
        img = cv2.imread(image_path)
        if img is None:
            return {"error": "No se pudo leer la imagen"}
        
        # Preprocesamiento común
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
        
        features = {
            "ground_defects": self._detect_ground_defects_v2(img, gray),
            "scattered_objects": self._detect_scattered_objects_v2(img, hsv),
            "dark_regions": self._detect_dark_regions_v2(img, gray),
            "sidewalk_cracks": self._detect_sidewalk_cracks_v2(img, gray),
            "tilted_lines": self._detect_tilted_structures_v2(img, gray),
            "multiple_holes": self._detect_multiple_holes(img, gray),  # NUEVO
            "traffic_light": self._detect_traffic_light_v2(img, hsv),  # MEJORADO
            "water_leak": self._detect_water_leak_v2(img, hsv),  # MEJORADO
            "fallen_tree": self._detect_fallen_tree_v2(img, hsv, gray),  # MEJORADO
        }
        
        return features
    
    # ════════════════════════════════════════════════════════════════════
    # CATEGORÍA 1: BACHES (ya funciona bien, ligera mejora)
    # ════════════════════════════════════════════════════════════════════
    
    def _detect_ground_defects_v2(self, img, gray) -> Dict:
        """Detecta BACHES INDIVIDUALES en la pista"""
        height, width = img.shape[:2]
        
        # Región inferior (donde está el suelo)
        ground_region = gray[int(height * 0.4):, :]
        
        # Mejorar contraste
        clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8,8))
        enhanced = clahe.apply(ground_region)
        
        # Detectar huecos oscuros
        _, thresh = cv2.threshold(enhanced, 60, 255, cv2.THRESH_BINARY_INV)
        
        # Limpiar ruido
        kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (5,5))
        cleaned = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)
        
        contours, _ = cv2.findContours(cleaned, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        # Filtrar por tamaño y forma
        holes = []
        for cnt in contours:
            area = cv2.contourArea(cnt)
            if 1500 < area < 50000:  # Tamaño razonable
                # Verificar que sea circular/irregular (no rectangular)
                perimeter = cv2.arcLength(cnt, True)
                circularity = 4 * np.pi * area / (perimeter * perimeter + 1e-5)
                
                if circularity > 0.3:  # No es una línea recta
                    holes.append({"area": area, "circularity": circularity})
        
        # ✅ Detectar BACHE: 1-3 huecos moderados
        is_pothole = 1 <= len(holes) <= 3
        
        return {
            "detected": is_pothole,
            "hole_count": len(holes),
            "total_area": sum(h["area"] for h in holes),
            "avg_circularity": np.mean([h["circularity"] for h in holes]) if holes else 0,
            "confidence": min(len(holes) / 3.0, 0.9) if is_pothole else 0.0,
            "hint": "BACHES (1-3 huecos aislados en pista)" if is_pothole else None
        }
    
    # ════════════════════════════════════════════════════════════════════
    # CATEGORÍA 4: PISTAS CON HUECOS (NUEVA - separada de BACHES)
    # ════════════════════════════════════════════════════════════════════
    
    def _detect_multiple_holes(self, img, gray) -> Dict:
        """Detecta MÚLTIPLES HUECOS distribuidos (no baches aislados)"""
        height, width = img.shape[:2]
        
        # Analizar toda la imagen, no solo abajo
        clahe = cv2.createCLAHE(clipLimit=3.0, tileGridSize=(8,8))
        enhanced = clahe.apply(gray)
        
        # Detectar huecos
        _, thresh = cv2.threshold(enhanced, 55, 255, cv2.THRESH_BINARY_INV)
        
        kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (7,7))
        cleaned = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)
        
        contours, _ = cv2.findContours(cleaned, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        # Buscar MUCHOS huecos pequeños/medianos
        holes = []
        for cnt in contours:
            area = cv2.contourArea(cnt)
            if 800 < area < 30000:  # Más pequeños que baches
                holes.append(area)
        
        # ✅ Detectar PISTAS CON HUECOS: 4+ huecos distribuidos
        is_multiple = len(holes) >= 4
        
        # Verificar distribución espacial
        if is_multiple and len(contours) >= 4:
            # Calcular centros de los huecos
            centers = []
            for cnt in contours[:10]:  # Primeros 10
                M = cv2.moments(cnt)
                if M["m00"] != 0:
                    cx = int(M["m10"] / M["m00"])
                    cy = int(M["m01"] / M["m00"])
                    centers.append((cx, cy))
            
            # Calcular dispersión
            if len(centers) >= 4:
                centers_array = np.array(centers)
                std_x = np.std(centers_array[:, 0])
                std_y = np.std(centers_array[:, 1])
                
                # Si están muy dispersos → PISTAS CON HUECOS
                is_distributed = std_x > width * 0.15 or std_y > height * 0.15
            else:
                is_distributed = False
        else:
            is_distributed = False
        
        confidence = min(len(holes) / 8.0, 0.85) if is_multiple and is_distributed else 0.0
        
        return {
            "detected": is_multiple and is_distributed,
            "hole_count": len(holes),
            "total_area": sum(holes),
            "distributed": is_distributed,
            "confidence": confidence,
            "hint": f"PISTAS CON HUECOS ({len(holes)} huecos distribuidos)" if is_multiple else None
        }
    
    # ════════════════════════════════════════════════════════════════════
    # CATEGORÍA 2: POSTE CAÍDO (MEJORADO - distinguir de árbol)
    # ════════════════════════════════════════════════════════════════════
    
    def _detect_tilted_structures_v2(self, img, gray) -> Dict:
        """Detecta POSTES caídos (metal/concreto, no orgánico)"""
        
        # 1. Detectar líneas largas
        edges = cv2.Canny(gray, 30, 100, apertureSize=3)
        lines = cv2.HoughLinesP(edges, 1, np.pi/180, 60, minLineLength=120, maxLineGap=20)
        
        tilted_lines = []
        if lines is not None:
            for line in lines:
                x1, y1, x2, y2 = line[0]
                length = np.sqrt((x2-x1)**2 + (y2-y1)**2)
                
                if x2 - x1 == 0:
                    angle = 90
                else:
                    angle = abs(np.degrees(np.arctan((y2 - y1) / (x2 - x1))))
                
                # Líneas diagonales (no perfectamente verticales ni horizontales)
                if 25 <= angle <= 75 and length > 100:
                    tilted_lines.append({"angle": angle, "length": length})
        
        has_tilted = len(tilted_lines) >= 3
        
        if not has_tilted:
            return {"detected": False, "confidence": 0.0, "hint": None}
        
        # 2. Verificar que NO sea orgánico (diferencia con árbol)
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
        
        # Máscara de colores orgánicos (verde/marrón)
        brown_mask = cv2.inRange(hsv, np.array([8, 40, 20]), np.array([25, 255, 200]))
        green_mask = cv2.inRange(hsv, np.array([30, 30, 30]), np.array([90, 255, 255]))
        organic_mask = cv2.bitwise_or(brown_mask, green_mask)
        
        organic_ratio = np.sum(organic_mask > 0) / organic_mask.size
        
        # ✅ Es POSTE si: tiene líneas diagonales PERO poca vegetación
        is_pole = has_tilted and organic_ratio < 0.20  # Clave: < 20% orgánico
        
        confidence = 0.0
        if is_pole:
            avg_angle = np.mean([l["angle"] for l in tilted_lines])
            confidence = min(len(tilted_lines) / 10.0, 0.75)
            
            # Bonus si el ángulo es muy diagonal (45-60°)
            if 40 <= avg_angle <= 60:
                confidence = min(confidence + 0.15, 0.85)
        
        return {
            "detected": is_pole,
            "tilted_count": len(tilted_lines),
            "organic_ratio": float(organic_ratio),
            "avg_angle": float(np.mean([l["angle"] for l in tilted_lines])) if tilted_lines else 0,
            "confidence": confidence,
            "hint": "POSTE CAÍDO (estructura metálica/concreto inclinada)" if is_pole else None
        }
    
    # ════════════════════════════════════════════════════════════════════
    # CATEGORÍA 5: SEMÁFORO MALOGRADO (MEJORADO)
    # ════════════════════════════════════════════════════════════════════
    
    def _detect_traffic_light_v2(self, img, hsv) -> Dict:
        """Detecta SEMÁFOROS (estructura vertical con 3 luces RGB)"""
        
        height, width = img.shape[:2]
        
        # 1. Buscar colores de semáforo (rojo, amarillo, verde)
        red_mask1 = cv2.inRange(hsv, np.array([0, 120, 120]), np.array([10, 255, 255]))
        red_mask2 = cv2.inRange(hsv, np.array([170, 120, 120]), np.array([180, 255, 255]))
        red_mask = cv2.bitwise_or(red_mask1, red_mask2)
        
        yellow_mask = cv2.inRange(hsv, np.array([15, 150, 150]), np.array([35, 255, 255]))
        green_mask = cv2.inRange(hsv, np.array([40, 80, 80]), np.array([85, 255, 255]))
        
        # 2. Contar cuántos colores están presentes
        colors_present = []
        if np.sum(red_mask > 0) > 200:
            colors_present.append("red")
        if np.sum(yellow_mask > 0) > 200:
            colors_present.append("yellow")
        if np.sum(green_mask > 0) > 200:
            colors_present.append("green")
        
        # 3. Buscar estructura vertical
        combined_mask = cv2.bitwise_or(red_mask, cv2.bitwise_or(yellow_mask, green_mask))
        contours, _ = cv2.findContours(combined_mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        vertical_structures = 0
        for cnt in contours:
            x, y, w, h = cv2.boundingRect(cnt)
            aspect_ratio = h / (w + 1e-5)
            
            # Características de semáforo: alto y delgado, en parte superior
            if aspect_ratio > 1.5 and h > 60 and y < height * 0.6:
                vertical_structures += 1
        
        # ✅ Es SEMÁFORO si: tiene 2+ colores Y estructura vertical
        is_traffic_light = len(colors_present) >= 2 and vertical_structures > 0
        
        confidence = 0.0
        if is_traffic_light:
            confidence = min((len(colors_present) / 3.0) * 0.5 + 
                           (vertical_structures / 2.0) * 0.5, 0.80)
        
        return {
            "detected": is_traffic_light,
            "colors_present": colors_present,
            "vertical_structures": vertical_structures,
            "confidence": confidence,
            "hint": f"SEMÁFORO ({len(colors_present)} colores, estructura vertical)" if is_traffic_light else None
        }
    
    # ════════════════════════════════════════════════════════════════════
    # CATEGORÍA 6: FUGA DE AGUA (MEJORADO)
    # ════════════════════════════════════════════════════════════════════
    
    def _detect_water_leak_v2(self, img, hsv) -> Dict:
        """Detecta FUGAS DE AGUA (charcos extensos + movimiento/brillo)"""
        
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        height, width = gray.shape
        
        # 1. Detectar agua por brillo especular (reflejos intensos)
        _, bright_mask = cv2.threshold(gray, 200, 255, cv2.THRESH_BINARY)
        
        # 2. Detectar agua por baja saturación (agua es grisácea)
        _, saturation, _ = cv2.split(hsv)
        low_sat_mask = cv2.inRange(saturation, 0, 60)
        
        # 3. Combinar: brillo + baja saturación
        water_mask = cv2.bitwise_and(bright_mask, low_sat_mask)
        
        # Limpiar ruido
        kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (11,11))
        water_mask = cv2.morphologyEx(water_mask, cv2.MORPH_OPEN, kernel)
        water_mask = cv2.morphologyEx(water_mask, cv2.MORPH_CLOSE, kernel)
        
        contours, _ = cv2.findContours(water_mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        # 4. Buscar charcos GRANDES (no pequeños reflejos)
        large_water_areas = []
        for cnt in contours:
            area = cv2.contourArea(cnt)
            if area > 20000:  # Charco significativo
                # Verificar que esté en el suelo (mitad inferior)
                M = cv2.moments(cnt)
                if M["m00"] != 0:
                    cy = int(M["m01"] / M["m00"])
                    if cy > height * 0.4:  # En el suelo
                        large_water_areas.append(area)
        
        # ✅ Es FUGA si: 1+ charco grande en el suelo
        is_leak = len(large_water_areas) > 0
        
        confidence = 0.0
        if is_leak:
            total_area = sum(large_water_areas)
            confidence = min((total_area / (width * height)) * 5.0, 0.75)
        
        return {
            "detected": is_leak,
            "large_areas": len(large_water_areas),
            "total_water_area": sum(large_water_areas) if large_water_areas else 0,
            "confidence": confidence,
            "hint": f"FUGA DE AGUA ({len(large_water_areas)} charco(s) extenso(s))" if is_leak else None
        }
    
    # ════════════════════════════════════════════════════════════════════
    # CATEGORÍA 9: ÁRBOL CAÍDO (MEJORADO - distinguir de poste)
    # ════════════════════════════════════════════════════════════════════
    
    def _detect_fallen_tree_v2(self, img, hsv, gray) -> Dict:
        """Detecta ÁRBOLES caídos (textura orgánica + follaje)"""
        
        # 1. Detectar textura orgánica (irregular, alta varianza)
        laplacian = cv2.Laplacian(gray, cv2.CV_64F)
        texture_variance = laplacian.var()
        
        # 2. Buscar colores orgánicos (CLAVE: verde + marrón abundante)
        brown_mask = cv2.inRange(hsv, np.array([8, 50, 30]), np.array([25, 255, 200]))
        green_mask = cv2.inRange(hsv, np.array([30, 40, 40]), np.array([90, 255, 255]))
        organic_mask = cv2.bitwise_or(brown_mask, green_mask)
        
        organic_ratio = np.sum(organic_mask > 0) / organic_mask.size
        
        # 3. Detectar líneas horizontales (tronco caído)
        edges = cv2.Canny(gray, 40, 120)
        lines = cv2.HoughLinesP(edges, 1, np.pi/180, 70, minLineLength=100, maxLineGap=20)
        
        horizontal_lines = 0
        if lines is not None:
            for line in lines:
                x1, y1, x2, y2 = line[0]
                if abs(x2 - x1) > 0:
                    angle = abs(np.degrees(np.arctan((y2 - y1) / (x2 - x1))))
                    if angle < 25:  # Casi horizontal
                        horizontal_lines += 1
        
        # ✅ Es ÁRBOL si: alta textura + MUCHO contenido orgánico (>25%)
        is_fallen_tree = (
            texture_variance > 1000 and       # Textura muy irregular
            organic_ratio > 0.28 and          # MUCHA vegetación (clave)
            horizontal_lines > 4              # Tronco horizontal
        )
        
        confidence = 0.0
        if is_fallen_tree:
            # Confianza basada en contenido orgánico
            confidence = min(organic_ratio * 2.5, 0.70)
        
        return {
            "detected": is_fallen_tree,
            "texture_variance": float(texture_variance),
            "organic_ratio": float(organic_ratio),
            "horizontal_lines": horizontal_lines,
            "confidence": confidence,
            "hint": f"ÁRBOL CAÍDO ({organic_ratio*100:.1f}% vegetación, textura orgánica)" if is_fallen_tree else None
        }
    
    # ════════════════════════════════════════════════════════════════════
    # Métodos que ya funcionan bien (solo versión limpia)
    # ════════════════════════════════════════════════════════════════════
    
    def _detect_scattered_objects_v2(self, img, hsv) -> Dict:
        """Detecta BASURA (ya funciona bien)"""
        color_ranges = [
            ([0, 100, 100], [10, 255, 255]),    # Rojo
            ([40, 100, 100], [80, 255, 255]),   # Verde
            ([20, 100, 100], [30, 255, 255]),   # Amarillo
        ]
        
        total_colored = 0
        for (lower, upper) in color_ranges:
            mask = cv2.inRange(hsv, np.array(lower), np.array(upper))
            total_colored += np.sum(mask > 0)
        
        ratio = total_colored / (img.shape[0] * img.shape[1])
        detected = ratio > 0.02
        
        return {
            "detected": detected,
            "colored_ratio": float(ratio),
            "confidence": min(ratio * 10, 0.85) if detected else 0.0,
            "hint": "BASURA ACUMULADA (objetos coloridos dispersos)" if detected else None
        }
    
    def _detect_dark_regions_v2(self, img, gray) -> Dict:
        """Detecta ALUMBRADO PÚBLICO (ya funciona bien)"""
        avg_brightness = np.mean(gray)
        dark_ratio = np.sum(gray < 50) / gray.size
        
        detected = avg_brightness < 90 and dark_ratio > 0.25
        
        return {
            "detected": detected,
            "avg_brightness": float(avg_brightness),
            "dark_ratio": float(dark_ratio),
            "confidence": 1.0 - (avg_brightness / 255) if detected else 0.0,
            "hint": f"ALUMBRADO PÚBLICO (brillo: {avg_brightness:.0f}/255)" if detected else None
        }
    
    def _detect_sidewalk_cracks_v2(self, img, gray) -> Dict:
        """Detecta VEREDA RAJADA (ya funciona bien)"""
        height, width = gray.shape
        
        roi_left = gray[int(height*0.6):, :int(width*0.3)]
        roi_right = gray[int(height*0.6):, int(width*0.7):]
        
        total_cracks = 0
        for roi in [roi_left, roi_right]:
            if roi.size == 0:
                continue
            enhanced = cv2.equalizeHist(roi)
            edges = cv2.Canny(enhanced, 10, 50)
            total_cracks += np.sum(edges > 0)
        
        detected = total_cracks > (height * width * 0.3)
        
        return {
            "detected": detected,
            "crack_intensity": int(total_cracks),
            "confidence": min(total_cracks / (height * width * 0.08), 0.85) if detected else 0.0,
            "hint": "VEREDA RAJADA (múltiples grietas finas)" if detected else None
        }
    
    # ════════════════════════════════════════════════════════════════════
    # GENERADOR DE HINTS MEJORADO
    # ════════════════════════════════════════════════════════════════════
    
    def get_hints_for_ollama(self, features: Dict) -> str:
        """Genera hints con TODAS las detecciones relevantes (Top 3-5)"""
        
        all_detections = []
        
        # Recolectar todas las detecciones
        detection_map = {
            "ground_defects": ("BACHES", "🕳️"),
            "multiple_holes": ("PISTAS CON HUECOS", "🛣️"),
            "tilted_lines": ("POSTE CAÍDO", "⚡"),
            "sidewalk_cracks": ("VEREDA RAJADA", "🩹"),
            "traffic_light": ("SEMÁFORO", "🚦"),
            "water_leak": ("FUGA DE AGUA", "💧"),
            "scattered_objects": ("BASURA", "🗑️"),
            "dark_regions": ("ALUMBRADO", "🌙"),
            "fallen_tree": ("ÁRBOL CAÍDO", "🌳"),
        }
        
        for key, (name, emoji) in detection_map.items():
            feature = features.get(key, {})
            if feature.get("detected") and feature.get("confidence", 0) > 0.15:
                all_detections.append({
                    "name": name,
                    "emoji": emoji,
                    "confidence": feature["confidence"],
                    "hint": feature.get("hint", name),
                    "details": self._get_details(key, feature)
                })
        
        # Ordenar por confianza
        all_detections.sort(key=lambda x: x["confidence"], reverse=True)
        
        # Tomar Top 5 (no solo 3)
        top = all_detections[:5]
        
        hints = []
        for det in top:
            hints.append(
                f"{det['emoji']} DETECCIÓN OpenCV: {det['hint']} "
                f"(conf: {det['confidence']:.2f}) {det['details']}"
            )
        
        # Siempre incluir brillo
        brightness = features.get("dark_regions", {}).get("avg_brightness", 128)
        hints.append(f"🌙 MÉTRICA: Brillo promedio {brightness:.0f}/255")
        
        if not top:
            hints.append("ℹ️ No se detectaron características específicas")
        
        return "\n".join(hints)
    
    def _get_details(self, key: str, feature: Dict) -> str:
        """Genera detalles específicos por tipo de detección"""
        if key == "ground_defects":
            return f"({feature['hole_count']} hueco(s))"
        elif key == "multiple_holes":
            return f"({feature['hole_count']} huecos distribuidos)"
        elif key == "tilted_lines":
            return f"(ángulo: {feature.get('avg_angle', 0):.0f}°, org: {feature.get('organic_ratio', 0)*100:.0f}%)"
        elif key == "traffic_light":
            return f"({', '.join(feature.get('colors_present', []))})"
        elif key == "water_leak":
            return f"({feature.get('large_areas', 0)} área(s))"
        elif key == "fallen_tree":
            return f"({feature.get('organic_ratio', 0)*100:.0f}% vegetación)"
        elif key == "scattered_objects":
            return f"({feature.get('colored_ratio', 0)*100:.1f}% objetos)"
        elif key == "sidewalk_cracks":
            return f"(intensidad: {feature.get('crack_intensity', 0)})"
        elif key == "dark_regions":
            return f"({feature.get('avg_brightness', 0):.0f}/255)"
        return ""