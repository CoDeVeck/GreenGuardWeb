package com.Cibertec.GreenGuard.dto;

import com.Cibertec.GreenGuard.enums.EstadoReporte;
import lombok.*;

import java.time.LocalDateTime;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReporteFiltroEstadoIncidenteClasificacion {

    private Integer idReporte;
    private String imagenRepo;
    private EstadoReporte estado;
    private Integer idTipoInci;
    private Integer idTipoClasi;
    private String detalleRepo; //si el detalle viene null se quita el campo detalle y solo queda fecha
    private LocalDateTime repoRegistado;



}
