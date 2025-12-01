package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.service.TiendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tienda")
public class TiendaController {

    @Autowired
    TiendaService tiendaService;
}
