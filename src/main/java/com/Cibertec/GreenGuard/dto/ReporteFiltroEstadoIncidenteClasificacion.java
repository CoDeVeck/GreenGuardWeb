package com.Cibertec.GreenGuard.dto;

import com.Cibertec.GreenGuard.enums.EstadoReporte;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class ReporteFiltroEstadoIncidenteClasificacion {

    private Integer idReporte;
    private String imagenRepo;
    private EstadoReporte estado;
    private Integer idTipoInci;
    private Integer idTipoClasi;
    private String detalleRepo; //si el detalle viene null se quita el campo detalle y solo queda fecha
    private LocalDateTime repoRegistado;


    private String descTipoInci;
    private String descTipoClasi;

    
    
    public ReporteFiltroEstadoIncidenteClasificacion(
            Integer idReporte,
            String imagenRepo,
            EstadoReporte estado,
            Integer idTipoInci,
            Integer idTipoClasi,
            String detalleRepo,
            LocalDateTime repoRegistado,
            String descTipoInci,
            String descTipoClasi) {
        this.idReporte = idReporte;
        this.imagenRepo = imagenRepo;
        this.estado = estado;
        this.idTipoInci = idTipoInci;
        this.idTipoClasi = idTipoClasi;
        this.detalleRepo = detalleRepo;
        this.repoRegistado = repoRegistado;
        this.descTipoInci = descTipoInci;
        this.descTipoClasi = descTipoClasi;
    }
}
