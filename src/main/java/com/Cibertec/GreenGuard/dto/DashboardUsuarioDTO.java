package com.Cibertec.GreenGuard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import com.Cibertec.GreenGuard.model.Reporte;

@Getter
@Setter
@AllArgsConstructor
public class DashboardUsuarioDTO {

    private List<ReporteStatsDTO> categoriasMasReportadas;
    private List<Reporte> reportesRecientes;
    private int totalReportes;

    private int puntosTotales;
    private int puntosMes;

    private int totalUsuariosBeneficiados;
}
