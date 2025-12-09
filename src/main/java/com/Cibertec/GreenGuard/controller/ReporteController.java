package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.model.Reporte;
import com.Cibertec.GreenGuard.service.CloudinaryService;
import com.Cibertec.GreenGuard.service.ReporteService;
import com.Cibertec.GreenGuard.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reporte")
public class ReporteController {

    @Autowired
    ReporteService reporteService;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    UsuarioService usuarioService;

    @PostMapping(value = "/registrar", consumes = {"multipart/form-data"})
    public ResponseEntity<?>registrarReporte(@ModelAttribute Reporte reporte, @AuthenticationPrincipal UserDetails userDetails){

        try {
            //Obtener el ID del usuario que previamente tiene q estar logeado
            String emailUsuario = userDetails.getUsername();
            Integer idUsuario = usuarioService.obtenerIdPorEmail(emailUsuario);

            //Obtenemos el tipo de incidente que manda la IA
            int tipoClasi = reporte.getTipoClasificacion().getIdTipoClasi();

            //Asignamos una carpeta para el tipo de incidente asiganado por la IA
            //(en este caso sera manual pero en la app la ia asigna automaticamente el tipo de incidente)
            String carpeta = switch (tipoClasi){
                case 1 -> "GreenGuard/riesgoBajo";
                case 2 -> "GreenGuard/riesgoMedio";
                case 3 -> "GreenGuard/riesgoAlto";
                case 4 -> "GreenGuard/riesgoMuyAlto";
                default -> "GreenGuard/riesgosGenerales";
            };

            //generamos la URL de la imagen para poder almacenar la referencia en la BD
            String urlImagen = cloudinaryService.uploadImage(reporte.getImagenUrl(), carpeta);
            reporte.setImagenRepo(urlImagen); //seteamos el campo con la url generada

            //guardamos el reporte
            Reporte reporteGuardado = reporteService.registrarReporte(reporte,idUsuario);

            return ResponseEntity.ok(reporteGuardado);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar reporte: " + e.getMessage());
        }
    }



}
