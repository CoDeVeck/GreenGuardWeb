package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.dto.response.ResultadoResponse;
import com.Cibertec.GreenGuard.model.UsuarioCupon;
import com.Cibertec.GreenGuard.service.UsuarioCuponService;
import com.Cibertec.GreenGuard.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    UsuarioCuponService usuarioCuponService;

    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/comprarCupon/{idCupon}")
    public ResponseEntity<?> comprarCupon(@PathVariable("idCupon") Integer idCupon, @AuthenticationPrincipal UserDetails userDetails){

        try {
            String emailUsuario = userDetails.getUsername();
            Integer idUsuario = usuarioService.obtenerIdPorEmail(emailUsuario);

            ResultadoResponse resultadoResponse = usuarioCuponService.comprarCupon(idCupon,idUsuario);

            return ResponseEntity.ok(resultadoResponse);

        } catch (Exception e) {
            throw new RuntimeException("Error: ",e);
        }
    }

}
