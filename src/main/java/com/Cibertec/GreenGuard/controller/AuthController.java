package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.dto.ResultadoResponse;
import com.Cibertec.GreenGuard.model.Usuario;
import com.Cibertec.GreenGuard.service.CloudinaryService;
import com.Cibertec.GreenGuard.service.UsuarioService;
import com.Cibertec.GreenGuard.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestParam("correoUsu")String correoUsu,
                                          @RequestParam("passwordUsu") String passwordUsu){

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(correoUsu, passwordUsu)
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String token = jwtUtil.generateToken(correoUsu, roles);
        return  ResponseEntity.ok(Map.of("token", token));

    }


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
