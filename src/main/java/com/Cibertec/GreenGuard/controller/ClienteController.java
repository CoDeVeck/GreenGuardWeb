package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.dto.CuponCatalagoDTO;
import com.Cibertec.GreenGuard.dto.UsuarioCuponDto;
import com.Cibertec.GreenGuard.dto.response.ResultadoResponse;
import com.Cibertec.GreenGuard.model.UsuarioCupon;
import com.Cibertec.GreenGuard.service.UsuarioCuponService;
import com.Cibertec.GreenGuard.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/misCupones")
    public ResponseEntity<?> misCupones(@AuthenticationPrincipal UserDetails userDetails){
        String emailUsuario = userDetails.getUsername();
        Integer idUsu = usuarioService.obtenerIdPorEmail(emailUsuario);

        List<UsuarioCuponDto> cupones = usuarioCuponService.obtenerCuponesUsuario(idUsu);

        return ResponseEntity.ok(cupones);
    }

    @PostMapping("/misCupones/{idCuponUsuario}/devolver")
    public ResponseEntity<?>devolverCupones(@PathVariable Integer idCuponUsuario,
                                            @AuthenticationPrincipal UserDetails userDetails){

        String emailUsuario = userDetails.getUsername();
        Integer idUsu = usuarioService.obtenerIdPorEmail(emailUsuario);

        ResultadoResponse resultado = usuarioCuponService.devolverCupon(idCuponUsuario, idUsu);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/catalogo")
    public ResponseEntity<?> catalogoCupnes(
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer categoria,
            @RequestParam(required = false) Integer puntosMin,
            @RequestParam(required = false) Integer puntosMax
    ){
        List<CuponCatalagoDTO> lista =
                usuarioCuponService.catalagoCupones(activo, nombre, categoria, puntosMin, puntosMax);

        return ResponseEntity.ok(lista);
    }


}
