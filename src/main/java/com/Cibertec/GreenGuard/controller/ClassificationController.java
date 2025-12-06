package com.Cibertec.GreenGuard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Cibertec.GreenGuard.dto.ClassificationResponseDTO;
import com.Cibertec.GreenGuard.service.PythonAIService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClassificationController {

    private final PythonAIService pythonAIService;

    @PostMapping("/classify")
    public ResponseEntity<ClassificationResponseDTO> classifyImage(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            log.info("Recibida solicitud de clasificación para: {}", file.getOriginalFilename());

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            ClassificationResponseDTO result = pythonAIService.classifyImage(file);
            
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error en clasificación", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}