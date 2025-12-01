package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificacion")
public class NotificacionController {

    @Autowired
    NotificacionService notificacionService;
}
