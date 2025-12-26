package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.dto.NotificacionResponseDTO;
import com.Cibertec.GreenGuard.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.Cibertec.GreenGuard.model.Notificacion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notificacion")
@CrossOrigin(origins = "*")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarTodasNotificaciones(
            @PathVariable Integer idUsuario) {
        try {
            List<NotificacionResponseDTO> notificaciones =
                    notificacionService.listarTodasNotificaciones(idUsuario);
            return ResponseEntity.ok(notificaciones);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 2. GET - Listar solo NO LEÍDAS
    @GetMapping("/usuario/{idUsuario}/no-leidas")
    public ResponseEntity<List<NotificacionResponseDTO>> listarNoLeidas(
            @PathVariable Integer idUsuario) {
        try {
            List<NotificacionResponseDTO> notificaciones =
                    notificacionService.listarNoLeidas(idUsuario);
            return ResponseEntity.ok(notificaciones);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/usuario/{idUsuario}/contador")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(@PathVariable Integer idUsuario) {
        try {
            long contador = notificacionService.contarNoLeidas(idUsuario);
            Map<String, Long> response = new HashMap<>();
            response.put("noLeidas", contador);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{idNotificacion}/marcar-leida")
    public ResponseEntity<Map<String, String>> marcarComoLeida(@PathVariable Integer idNotificacion) {
        try {
            notificacionService.marcarComoLeida(idNotificacion);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Notificación marcada como leída");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
