package com.Cibertec.GreenGuard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.Cibertec.GreenGuard.dto.ClassificationResponseDTO;

@Slf4j
@Service
public class PythonAIService {
    
    @Value("${python.ai.service.url}")
    private String pythonServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public PythonAIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public ClassificationResponseDTO classifyImage(MultipartFile file) {
        try {
            log.info("Enviando imagen a Python AI Service: {}/api/classify", pythonServiceUrl);

            // Preparar el request multipart
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Convertir MultipartFile a Resource
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = 
                new HttpEntity<>(body, headers);

            // Hacer la petición
            ResponseEntity<ClassificationResponseDTO> response = restTemplate.exchange(
            		pythonServiceUrl + "/api/classify",
                HttpMethod.POST,
                requestEntity,
                ClassificationResponseDTO.class
            );

            log.info("Respuesta de Python AI: {}", response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.error("Error llamando a Python AI Service", e);
            throw new RuntimeException("Error en clasificación de IA: " + e.getMessage());
        }
    }
}