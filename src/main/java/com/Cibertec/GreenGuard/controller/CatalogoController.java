package com.Cibertec.GreenGuard.controller;


import com.Cibertec.GreenGuard.enums.EstadoReporte;
import com.Cibertec.GreenGuard.model.Distrito;
import com.Cibertec.GreenGuard.model.TipoClasificacion;
import com.Cibertec.GreenGuard.model.TipoIncidentes;
import com.Cibertec.GreenGuard.repository.IDistritoRepository;
import com.Cibertec.GreenGuard.repository.ITipoClasificacionRepository;
import com.Cibertec.GreenGuard.repository.ITipoIncidenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalogos")
@RequiredArgsConstructor
public class CatalogoController {

    private final ITipoIncidenteRepository tipoIncidenteRepository;
    private final ITipoClasificacionRepository tipoClasificacionRepository;
    private final IDistritoRepository distritoRepository;

    @GetMapping("/estados")
    public ResponseEntity<List<Map<String, String>>> obtenerEstados() {
        List<Map<String, String>> estados = Arrays.stream(EstadoReporte.values())
            .map(estado -> {
                Map<String, String> map = new HashMap<>();
                map.put("codigo", estado.name());
                map.put("descripcion", getEstadoDescripcion(estado));
                return map;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(estados);
    }

    @GetMapping("/tipos-incidentes")
    public ResponseEntity<List<TipoIncidentes>> obtenerTiposIncidentes() {
        return ResponseEntity.ok(tipoIncidenteRepository.findAll());
    }

    @GetMapping("/tipos-clasificacion")
    public ResponseEntity<List<TipoClasificacion>> obtenerTiposClasificacion() {
        return ResponseEntity.ok(tipoClasificacionRepository.findAll());
    }

    @GetMapping("/distritos")
    public ResponseEntity<List<Distrito>> obtenerDistritos() {
        return ResponseEntity.ok(distritoRepository.findAll());
    }

    private String getEstadoDescripcion(EstadoReporte estado) {
        return switch (estado) {
            case PE -> "Pendiente";
            case EP -> "En Proceso";
            case RE -> "Resuelto";
            case CA -> "Cancelado";
        };
    }
}