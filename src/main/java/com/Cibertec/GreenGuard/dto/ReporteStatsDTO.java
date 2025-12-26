package com.Cibertec.GreenGuard.dto;
import com.Cibertec.GreenGuard.model.TipoIncidentes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReporteStatsDTO {

    private TipoIncidentes categoria;
    private Long totalReportes;
}
