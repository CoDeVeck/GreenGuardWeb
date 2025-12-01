package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reporte")
public class ReporteController {

    @Autowired
    ReporteService reporteService;
}
