package com.Cibertec.GreenGuard.dto;

import com.Cibertec.GreenGuard.enums.EstadoReporte;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DetalleReporteHistorialCliente {

    private Integer idReporte;
    private String numeroReporte;
    private String imagenRepo;
    private Integer idTipoClasi;
    private String incidente;
    private EstadoReporte estado;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String descripcion;
    private int puntosGanados;
    private LocalDateTime repoRegistado;
    private LocalDateTime repoProceso;
    private LocalDateTime repoResuelto;

}
