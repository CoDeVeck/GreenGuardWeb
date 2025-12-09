package com.Cibertec.GreenGuard.dto;

import com.Cibertec.GreenGuard.enums.EstadoReporte;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DetalleReporteHistorialCliente {

    private Integer idReporte;
    private String imagenRepo;
    private Integer idTipoClasi;
    private EstadoReporte estado;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private LocalDateTime repoRegistado;
    private LocalDateTime repoProceso;
    private LocalDateTime repoResuelto;

}
