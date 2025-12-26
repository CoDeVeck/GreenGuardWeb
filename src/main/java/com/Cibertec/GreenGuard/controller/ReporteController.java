package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.dto.EstadisticasGeneralesDTO;
import com.Cibertec.GreenGuard.dto.ReporteFiltroEstadoIncidenteClasificacion;
import com.Cibertec.GreenGuard.dto.ReporteRequestDTO;
import com.Cibertec.GreenGuard.dto.ReporteResponseDTO;
import com.Cibertec.GreenGuard.service.ReporteService;
import com.Cibertec.GreenGuard.model.Reporte;
import com.Cibertec.GreenGuard.service.CloudinaryService;
import com.Cibertec.GreenGuard.service.ExportacionReporteService;
import com.Cibertec.GreenGuard.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

	private final ReporteService reporteService;
	
	private final ExportacionReporteService exportacionService;

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
    
    
    @GetMapping("/estadisticas-generales")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasGenerales() {
        return ResponseEntity.ok(reporteService.obtenerEstadisticasGenerales());
    }

    @GetMapping("/por-clasificacion")
    public ResponseEntity<List<Map<String, Object>>> reportesPorClasificacion() {
        return ResponseEntity.ok(reporteService.reportesPorClasificacion());
    }

    @GetMapping("/por-tipo-incidente")
    public ResponseEntity<List<Map<String, Object>>> reportesPorTipoIncidente() {
        return ResponseEntity.ok(reporteService.reportesPorTipoIncidente());
    }

    @GetMapping("/por-distrito")
    public ResponseEntity<List<Map<String, Object>>> reportesPorDistrito() {
        return ResponseEntity.ok(reporteService.reportesPorDistrito());
    }

    @GetMapping("/ultimos")
    public ResponseEntity<List<Reporte>> obtenerUltimosReportes() {
        return ResponseEntity.ok(reporteService.obtenerUltimosReportes());
    }
    
    @GetMapping
    public ResponseEntity<List<Reporte>> listarTodosReportes() {
        return ResponseEntity.ok(reporteService.listadoGenerallistadoGeneral());
    }
    
    @GetMapping("/filtrar")
    public ResponseEntity<List<ReporteFiltroEstadoIncidenteClasificacion>> filtroReportes(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer incidente,
            @RequestParam(required = false) Integer clasificacion) {
        
        log.info("Filtrando reportes - Estado: {}, Incidente: {}, Clasificación: {}", 
                 estado, incidente, clasificacion);
        
        return ResponseEntity.ok(
            reporteService.listadoDeReportesPorFiltro(estado, incidente, clasificacion)
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerReportePorId(@PathVariable Integer id) {
        return ResponseEntity.ok(reporteService.obtenerReportePorId(id));
    }
    
    @PutMapping("/{id}/estado/en-proceso")
    public ResponseEntity<Reporte> cambiarAEnProceso(@PathVariable Integer id) {
        return ResponseEntity.ok(reporteService.cambiarEstadoEnProceso(id));
    }
    
    @PutMapping("/{id}/estado/resuelto")
    public ResponseEntity<Reporte> cambiarAResuelto(@PathVariable Integer id) throws IllegalAccessException {
        return ResponseEntity.ok(reporteService.cambiarEstadoEnResuelto(id));
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReporte(@PathVariable Integer id) {
        return ResponseEntity.ok(reporteService.cancelarReporte(id));
    }

    
    @GetMapping("/exportar/csv")
    public ResponseEntity<String> exportarCSV(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer incidente,
            @RequestParam(required = false) Integer clasificacion) {
        
        try {
            List<ReporteFiltroEstadoIncidenteClasificacion> reportes = 
                reporteService.listadoDeReportesPorFiltro(estado, incidente, clasificacion);
            
            String csv = exportacionService.generarCSV(reportes);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "reportes_" + 
                java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csv);
                    
        } catch (Exception e) {
            log.error("Error al exportar CSV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPDF(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer incidente,
            @RequestParam(required = false) Integer clasificacion) {
        
        try {
            List<ReporteFiltroEstadoIncidenteClasificacion> reportes = 
                reporteService.listadoDeReportesPorFiltro(estado, incidente, clasificacion);
            
            byte[] pdf = exportacionService.generarPDF(reportes);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reportes_" + 
                java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);
                    
        } catch (Exception e) {
            log.error("Error al exportar PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    

}
