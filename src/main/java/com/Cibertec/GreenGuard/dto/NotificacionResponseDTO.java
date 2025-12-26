package com.Cibertec.GreenGuard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO para listar notificaciones (todas o no leídas)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponseDTO {
    private Integer idNotificacion;
    private String titulo;
    private String mensaje;
    private String tipoNotificacion; // Ej: "REPORTE_REGISTRADO"
    private Boolean leida;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLectura;

    // Datos opcionales según el tipo
    private ReporteInfoDTO reporte;
    private CuponInfoDTO cupon;

    // Clases internas para info resumida
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReporteInfoDTO {
        private Integer idReporte;
        private String numReport;
        private String tipoIncidente;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuponInfoDTO {
        private Integer idUsuarioCupon;
        private String codigoCupon;
        private String nombreCupon;
    }
}