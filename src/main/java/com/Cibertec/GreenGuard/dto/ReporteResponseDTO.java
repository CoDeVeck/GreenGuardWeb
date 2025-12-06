package com.Cibertec.GreenGuard.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReporteResponseDTO {
    private Integer idReporte;
    private String numReport;
    private String imagenUrl;
    private String estado;
    private Integer puntosGanados;
    private String tipoIncidente;
    
    private String clasificacion;   // 👈 FALTABA
    private String nivelRiesgo;
    
    private String descripcionIA;
    private LocalDateTime repoRegistrado;
}
