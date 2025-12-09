package com.Cibertec.GreenGuard.dto;

import com.Cibertec.GreenGuard.enums.EstadoReporte;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteHistorialCliente {
    private Integer idReporte;
    private String imagenRepo;
    private Integer idTipoClasi;
    private EstadoReporte estado;
    private LocalDateTime repoRegistado;
    private LocalDateTime repoProceso;
    private LocalDateTime repoResuelto;
}
