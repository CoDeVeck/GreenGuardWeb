package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.service.TipoIncidenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/incidente")
public class TipoIncidentesController {

    @Autowired
    TipoIncidenteService incidenteService;
}
