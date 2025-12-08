package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.dto.ReporteFiltroEstadoIncidenteClasificacion;
import com.Cibertec.GreenGuard.model.Reporte;
import com.Cibertec.GreenGuard.service.ReporteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supervisor")
public class SupervisorController {

    @Autowired
    ReporteService reporteService;



    @GetMapping("/listadoGeneral")
    public ResponseEntity<?>listadoGeneral(){
        List<Reporte> listadoGeneral = reporteService.listadoGenerallistadoGeneral();

        return ResponseEntity.ok(listadoGeneral);
    }

    @PostMapping("/reportes")
    public ResponseEntity<?> filtrosPorEstadoIncidenteClasificacion(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false)Integer idIncidente,
            @RequestParam(required = false)Integer idClasificacion) {

        List<ReporteFiltroEstadoIncidenteClasificacion>lista =
                reporteService.listadoDeReportesPorFiltro(estado,idIncidente,idClasificacion);

        if(lista.isEmpty()){
            reporteService.listadoDeReportesPorFiltro(null,null,null);
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/reporte/{idReporte}")
    public ResponseEntity<?>reporteDetalle(@PathVariable("idReporte")Integer idReporte){
        if(idReporte == 0 ){
            throw new EntityNotFoundException("Ingresar un numero valido");
        }

        Reporte reporteEncontrado = reporteService.obtenerReportePorId(idReporte);

        return ResponseEntity.ok(reporteEncontrado);
    }

    @PutMapping("/reporteProceso/{idReporte}")
    public ResponseEntity<?>actualizarReporteEnProceso(@PathVariable("idReporte") Integer idReporte){
        if (idReporte == 0){
            throw new EntityNotFoundException("Ingresar un numero valido");
        }
        Reporte reporteActualizado = reporteService.cambiarEstadoEnProceso(idReporte);

        return ResponseEntity.ok(reporteActualizado);
    }

    @PutMapping("/reporteResuelto/{idReporte}")
    public ResponseEntity<?>actualizarReporteResuelto(@PathVariable("idReporte") Integer idReporte) throws IllegalAccessException {
       try {
           if (idReporte == 0){
               throw new EntityNotFoundException("Ingresar un numero valido");
           }
           Reporte reporteActualizado = reporteService.cambiarEstadoEnResuelto(idReporte);

           return ResponseEntity.ok(reporteActualizado);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }

    }

}
