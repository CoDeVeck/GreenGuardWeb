package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.service.DistritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/distrito")
public class DistritoController {

    @Autowired
    DistritoService distritoService;
}
