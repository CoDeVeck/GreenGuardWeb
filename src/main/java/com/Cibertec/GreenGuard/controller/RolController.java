package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rol")
public class RolController {

    @Autowired
    RolService rolService;
}
