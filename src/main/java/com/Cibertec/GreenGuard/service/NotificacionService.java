package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.dto.NotificacionResponseDTO;
import com.Cibertec.GreenGuard.model.Notificacion;
import com.Cibertec.GreenGuard.model.Usuario;
import com.Cibertec.GreenGuard.repository.*;
import com.Cibertec.GreenGuard.util.NotificacionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private INotificacionRepository notificacionRepository;

    @Autowired
    private ITipoNotificacionRepository tipoNotificacionRepository;

    @Autowired
    private IReporteRepository reporteRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IUsuarioCuponRepository usuarioCuponRepository;

    @Autowired
    private FirebaseNotificationService firebaseService;

    @Autowired
    NotificacionMapper notificacionMapper;

    public List<NotificacionResponseDTO> listarTodasNotificaciones(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuarioOrderByFechaCreacionDesc(usuario);

        return notificacionMapper.toDTOList(notificaciones);
    }

    // LISTAR solo NO LEÍDAS (retorna DTO)
    public List<NotificacionResponseDTO> listarNoLeidas(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(usuario);

        return notificacionMapper.toDTOList(notificaciones);
    }

    // Contar no leídas (para mostrar badge)
    public long contarNoLeidas(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return notificacionRepository.countByUsuarioAndLeidaFalse(usuario);
    }

    // Marcar como leída
    @Transactional
    public void marcarComoLeida(Integer idNotificacion) {
        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
    }

    // TIPO 1: Reporte registrado -done
    @Transactional
    public void notificarReporteRegistrado(Integer idUsuario, Integer idReporte, String numReporte) {
        String titulo = "¡Reporte Registrado!";
        String mensaje = "Tu reporte " + numReporte + " ha sido registrado exitosamente. Lo revisaremos pronto.";

        enviarNotificacion(idUsuario, 1, titulo, mensaje, idReporte, null);
    }

    // TIPO 2: Cambio de estado del reporte -done
    @Transactional
    public void notificarCambioEstadoReporte(Integer idUsuario, Integer idReporte,
                                             String numReporte, String nuevoEstado, Integer puntosGanados) {
        String titulo;
        String mensaje;

        switch (nuevoEstado) {
            case "EP":
                titulo = "Reporte en Proceso";
                mensaje = "Tu reporte " + numReporte + " está siendo atendido por las autoridades.";
                break;
            case "RE":
                titulo = "¡Reporte Resuelto!";
                mensaje = "Tu reporte " + numReporte + " ha sido resuelto. ¡Ganaste " + puntosGanados + " puntos!";
                // También notificar puntos ganados
                notificarPuntosGanados(idUsuario, puntosGanados, "reporte resuelto");
                break;
            case "CA":
                titulo = "Reporte Cancelado";
                mensaje = "Tu reporte " + numReporte + " ha sido cancelado.";
                break;
            default:
                return;
        }

        enviarNotificacion(idUsuario, 2, titulo, mensaje, idReporte, null);
    }

    // TIPO 3: Puntos ganados -DONE
    @Transactional
    public void notificarPuntosGanados(Integer idUsuario, Integer puntos, String razon) {
        String titulo = "¡Puntos Ganados!";
        String mensaje = "Has ganado " + puntos + " puntos por " + razon + ".";

        enviarNotificacion(idUsuario, 3, titulo, mensaje, null, null);
    }

    // TIPO 4: Cupón canjeado
    @Transactional
    public void notificarCuponCanjeado(Integer idUsuario, Integer idUsuarioCupon,
                                       String nombreCupon, Integer puntosUsados) {
        String titulo = "¡Cupón Canjeado!";
        String mensaje = "Has canjeado " + puntosUsados + " puntos por: " + nombreCupon;

        enviarNotificacion(idUsuario, 4, titulo, mensaje, null, idUsuarioCupon);
    }

    // TIPO 7: Cupón usado en tienda
    @Transactional
    public void notificarCuponUsado(Integer idUsuario, Integer idUsuarioCupon,
                                    String nombreCupon, String nombreTienda) {
        String titulo = "Cupón Utilizado";
        String mensaje = "Has usado tu cupón '" + nombreCupon + "' en " + nombreTienda + ". ¡Disfrútalo!";

        enviarNotificacion(idUsuario, 7, titulo, mensaje, null, idUsuarioCupon);
    }

    private void enviarNotificacion(Integer idUsuario, Integer tipoNotificacion,
                                    String titulo, String mensaje,
                                    Integer idReporte, Integer idUsuarioCupon) {
        try {
            Notificacion notificacion = new Notificacion();
            notificacion.setUsuario(usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
            notificacion.setTipoNotificacion(tipoNotificacionRepository.findById(tipoNotificacion)
                    .orElseThrow(() -> new RuntimeException("Tipo de notificación no encontrado")));

            notificacion.setTitulo(titulo);
            notificacion.setMensaje(mensaje);

            if (idReporte != null) {
                notificacion.setReporte(reporteRepository.findById(idReporte)
                        .orElseThrow(() -> new RuntimeException("Reporte no encontrado")));
            }

            if (idUsuarioCupon != null) {
                notificacion.setUsuarioCupon(usuarioCuponRepository.findById(idUsuarioCupon)
                        .orElseThrow(() -> new RuntimeException("Usuario Cupón no encontrado")));
            }

            notificacion.setFechaCreacion(LocalDateTime.now());
            notificacion.setLeida(false);

            notificacionRepository.save(notificacion);

            Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
            if (usuario != null && usuario.getFmcToken() != null) {
                firebaseService.sendNotification(usuario.getFmcToken(), titulo, mensaje);
            }

        } catch (Exception e) {
            System.err.println("Error enviando notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}