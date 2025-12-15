package com.Cibertec.GreenGuard.dto;
import com.Cibertec.GreenGuard.model.TipoIncidentes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReporteStatsDTO {

    private TipoIncidentes categoria;
    private Long totalReportes;
}
