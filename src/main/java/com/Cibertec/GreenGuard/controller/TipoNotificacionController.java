package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.service.TipoNotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tipoNotificacion")
public class TipoNotificacionController {

    @Autowired
    TipoNotificacionService notificaService;
}
