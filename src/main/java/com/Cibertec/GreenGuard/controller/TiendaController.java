package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.model.Cupon;
import com.Cibertec.GreenGuard.service.CuponService;
import com.Cibertec.GreenGuard.service.TiendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tienda")
public class TiendaController {

    @Autowired
    TiendaService tiendaService;

    @Autowired
    CuponService cuponService;

    @PostMapping("/nuevoCupon")
    public ResponseEntity<?> registrarNuevoCupon(@RequestBody Cupon cupon, @RequestParam("valorDescuento")double valorDescuento){

        try {
            Cupon cuponRegistrado = cuponService.crearNuevoCupon(cupon,valorDescuento);

            return ResponseEntity.ok(cuponRegistrado);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
