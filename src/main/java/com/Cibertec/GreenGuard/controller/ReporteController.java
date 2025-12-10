package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.dto.ReporteRequestDTO;
import com.Cibertec.GreenGuard.dto.ReporteResponseDTO;
import com.Cibertec.GreenGuard.service.ReporteService;
import com.Cibertec.GreenGuard.model.Reporte;
import com.Cibertec.GreenGuard.service.CloudinaryService;
import com.Cibertec.GreenGuard.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

	private final ReporteService reporteService;
	private final CloudinaryService cloudinaryService;
	private final UsuarioService usuarioService;

    @PostMapping("/con-clasificacion")
	public ResponseEntity<ReporteResponseDTO> crearReporteConClasificacion(
	        @RequestParam("idUsu") Integer idUsu,
	        @RequestParam("detalleRepo") String detalleRepo,
	        @RequestParam("latitud") BigDecimal latitud,
	        @RequestParam("longitud") BigDecimal longitud,
	        @RequestParam("idDistrito") Integer idDistrito,
	        
	        // ✅ RECIBIR DATOS DE CLASIFICACIÓN YA OBTENIDOS
	        @RequestParam("idTipoIncidente") Integer idTipoIncidente,
	        @RequestParam("idClasificacion") Integer idClasificacion,
	        @RequestParam("descripcionIA") String descripcionIA,
	        @RequestParam("puntosEstimados") Integer puntosEstimados,
	        
	        @RequestParam("imagen") MultipartFile imagen
	) throws IOException {
	    
	    ReporteRequestDTO request = new ReporteRequestDTO();
	    request.setIdUsu(idUsu);
	    request.setDetalleRepo(detalleRepo);
	    request.setLatitud(latitud);
	    request.setLongitud(longitud);
	    request.setIdDistrito(idDistrito);
	    request.setImagen(imagen);
	    
	    // ✅ Datos de clasificación pre-obtenidos
	    request.setIdTipoIncidente(idTipoIncidente);
	    request.setIdClasificacion(idClasificacion);
	    request.setDescripcionIA(descripcionIA);
	    request.setPuntosEstimados(puntosEstimados);
	    
	    ReporteResponseDTO response = reporteService.crearReporteConClasificacion(request);
	    return ResponseEntity.ok(response);
	}
	

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
