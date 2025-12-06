package com.Cibertec.GreenGuard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassificationResponseDTO {
    
    @JsonProperty("idTipoIncidente")
    private Integer idTipoIncidente;      // ✅ NUEVO
    
    @JsonProperty("tipoIncidente")
    private String tipoIncidente;
    
    @JsonProperty("idClasificacion")
    private Integer idClasificacion;      // ✅ NUEVO
    
    @JsonProperty("clasificacion")
    private String clasificacion;
    
    @JsonProperty("nivelRiesgo")
    private String nivelRiesgo;
    
    @JsonProperty("puntosEstimados")
    private Integer puntosEstimados;
    
    @JsonProperty("descIA")
    private String descIA;
    
    @JsonProperty("confianza")
    private Float confianza;
}