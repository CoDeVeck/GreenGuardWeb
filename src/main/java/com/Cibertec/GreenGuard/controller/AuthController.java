package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.dto.ResultadoResponse;
import com.Cibertec.GreenGuard.model.Usuario;
import com.Cibertec.GreenGuard.service.CloudinaryService;
import com.Cibertec.GreenGuard.service.UsuarioService;
import com.Cibertec.GreenGuard.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    CloudinaryService cloudinaryService;

    @PostMapping(value= "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<?> registrarUsuario(@ModelAttribute Usuario usuario){

        try {
            String urlImagen = cloudinaryService.uploadImage(
                    usuario.getImagenUrl(), "GreenGuard/Users");
            usuario.setImagenUsu(urlImagen);

            ResultadoResponse resultado = usuarioService.createUser(usuario);

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error registrando usuario: " + e.getMessage());
        }
    }

}
