package com.Cibertec.GreenGuard.util;

import com.Cibertec.GreenGuard.dto.NotificacionResponseDTO;
import com.Cibertec.GreenGuard.model.Notificacion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificacionMapper {

    public NotificacionResponseDTO toDTO(Notificacion notificacion) {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setIdNotificacion(notificacion.getIdNotificacion());
        dto.setTitulo(notificacion.getTitulo());
        dto.setMensaje(notificacion.getMensaje());
        dto.setTipoNotificacion(notificacion.getTipoNotificacion().getDescripcion());
        dto.setLeida(notificacion.getLeida());
        dto.setFechaCreacion(notificacion.getFechaCreacion());

        // Info del reporte (si existe)
        if (notificacion.getReporte() != null) {
            NotificacionResponseDTO.ReporteInfoDTO reporteInfo =
                    new NotificacionResponseDTO.ReporteInfoDTO(
                            notificacion.getReporte().getIdReporte(),
                            notificacion.getReporte().getNumReport(),
                            notificacion.getReporte().getTipoIncidente() != null ?
                                    notificacion.getReporte().getTipoIncidente().getDescTipoInci() : null
                    );
            dto.setReporte(reporteInfo);
        }

        // Info del cupón (si existe)
        if (notificacion.getUsuarioCupon() != null) {
            NotificacionResponseDTO.CuponInfoDTO cuponInfo =
                    new NotificacionResponseDTO.CuponInfoDTO(
                            notificacion.getUsuarioCupon().getIdUsuarioCupon(),
                            notificacion.getUsuarioCupon().getCodigoCupon(),
                            notificacion.getUsuarioCupon().getCupon() != null ?
                                    notificacion.getUsuarioCupon().getCupon().getNombreCupon() : null
                    );
            dto.setCupon(cuponInfo);
        }

        return dto;
    }

    public List<NotificacionResponseDTO> toDTOList(List<Notificacion> notificaciones) {
        return notificaciones.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}