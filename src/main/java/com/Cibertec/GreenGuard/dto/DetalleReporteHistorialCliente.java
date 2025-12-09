package com.Cibertec.GreenGuard.dto;

import com.Cibertec.GreenGuard.enums.EstadoReporte;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
