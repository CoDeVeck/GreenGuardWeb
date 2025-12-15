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

}
