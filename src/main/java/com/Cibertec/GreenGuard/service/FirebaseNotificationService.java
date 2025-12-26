package com.Cibertec.GreenGuard.service;


import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FirebaseNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseNotificationService.class);

    public String sendNotification(String deviceToken, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Notificación enviada exitosamente: {}", response);
            return response;

        } catch (FirebaseMessagingException e) {
            logger.error("Error al enviar notificación: {}", e.getMessage());
            throw new RuntimeException("Error enviando notificación", e);
        }
    }

    // Para enviar a múltiples dispositivos
    public void sendNotificationToMultiple(List<String> tokens, String title, String body) {
        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            logger.info("{} notificaciones enviadas correctamente", response.getSuccessCount());
        } catch (FirebaseMessagingException e) {
            logger.error("Error al enviar notificaciones: {}", e.getMessage());
        }
    }
}