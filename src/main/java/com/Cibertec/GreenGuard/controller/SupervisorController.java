package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.dto.ReporteFiltroEstadoIncidenteClasificacion;
import com.Cibertec.GreenGuard.model.Reporte;
import com.Cibertec.GreenGuard.service.ReporteService;
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


}
