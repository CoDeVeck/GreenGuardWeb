package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.service.CuponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cupon")
public class CuponController {

    @Autowired
    CuponService cuponService;
}
