package com.Cibertec.GreenGuard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasGeneralesDTO {
    private Long totalReportes;
    private Long reportesPendientes;
    private Long reportesEnProceso;
    private Long reportesResueltos;
    private Long reportesCancelados;
}