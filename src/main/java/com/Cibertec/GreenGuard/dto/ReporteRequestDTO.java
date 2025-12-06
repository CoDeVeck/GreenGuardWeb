package com.Cibertec.GreenGuard.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // o @Getter/@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ReporteRequestDTO {
    private Integer idUsu;
    private String detalleRepo;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private Integer idDistrito;
    private MultipartFile imagen;
    
    // ✅ NUEVOS: Datos de clasificación pre-obtenidos
    private Integer idTipoIncidente;
    private Integer idClasificacion;
    private String descripcionIA;
    private Integer puntosEstimados;
}
