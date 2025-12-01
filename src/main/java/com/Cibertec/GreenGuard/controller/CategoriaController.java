package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    CategoriaService categoriaService;
}
