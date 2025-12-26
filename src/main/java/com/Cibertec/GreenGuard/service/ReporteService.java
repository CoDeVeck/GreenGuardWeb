package com.Cibertec.GreenGuard.service;


import com.Cibertec.GreenGuard.dto.ReporteRequestDTO;
import com.Cibertec.GreenGuard.dto.ReporteResponseDTO;
import com.Cibertec.GreenGuard.dto.ReporteStatsDTO;
import com.Cibertec.GreenGuard.enums.EstadoReporte;
import com.Cibertec.GreenGuard.model.Distrito;
import com.Cibertec.GreenGuard.model.Reporte;
import com.Cibertec.GreenGuard.model.TipoClasificacion;
import com.Cibertec.GreenGuard.model.TipoIncidentes;
import com.Cibertec.GreenGuard.model.Usuario;
import com.Cibertec.GreenGuard.repository.IDistritoRepository;
import com.Cibertec.GreenGuard.repository.IReporteRepository;
import com.Cibertec.GreenGuard.repository.ITipoClasificacionRepository;
import com.Cibertec.GreenGuard.repository.ITipoIncidenteRepository;
import com.Cibertec.GreenGuard.repository.IUsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Cibertec.GreenGuard.dto.DashboardUsuarioDTO;
import com.Cibertec.GreenGuard.dto.ReporteFiltroEstadoIncidenteClasificacion;
import com.Cibertec.GreenGuard.dto.response.ResultadoResponse;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {
    private final IReporteRepository reporteRepository;
    private final CloudinaryService cloudinaryService;
    private final IUsuarioRepository usuarioRepository;
    private final ITipoIncidenteRepository tipoIncidenteRepository;
    private final ITipoClasificacionRepository tipoClasificacionRepository;
    private final IDistritoRepository distritoRepository;
    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;
       
    @Transactional
    public ReporteResponseDTO crearReporteConClasificacion(ReporteRequestDTO request) throws IOException {

        log.info("Creando reporte CON clasificación pre-obtenida para usuario: {}", request.getIdUsu());

        if (request.getImagen() == null || request.getImagen().isEmpty()) {
            throw new IllegalArgumentException("La imagen es requerida");
        }
        
        if (request.getIdTipoIncidente() == null || request.getIdClasificacion() == null) {
            throw new IllegalArgumentException("Datos de clasificación requeridos");
        }

        log.info("Usando clasificación pre-obtenida: idTipo={}, idClasif={}",
            request.getIdTipoIncidente(),
            request.getIdClasificacion());

        log.info("Subiendo imagen a Cloudinary...");
        String carpeta = obtenerCarpetaPorClasificacion(request.getIdClasificacion());
        String imageUrl = cloudinaryService.uploadImage(request.getImagen(), carpeta);
        log.info("Imagen subida: {}", imageUrl);

        String numReport = generarNumeroReporte();

        Usuario usuario = usuarioRepository.findById(request.getIdUsu())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        int puntosActuales = usuario.getPuntosUsu();
        int puntosNuevos = request.getPuntosEstimados() != null ? request.getPuntosEstimados() : 0;
        usuario.setPuntosUsu(puntosActuales + puntosNuevos);
        usuarioRepository.save(usuario); // Guardar usuario con puntos actualizados
        
        log.info("✅ Puntos actualizados para usuario {}: {} -> {}", 
            usuario.getIdUsu(), puntosActuales, usuario.getPuntosUsu());

        Reporte reporte = new Reporte();
        reporte.setNumReport(numReport);
        reporte.setDetalleRepo(request.getDetalleRepo());
        reporte.setImagenRepo(imageUrl);
        reporte.setLatitud(request.getLatitud());
        reporte.setLongitud(request.getLongitud());
        reporte.setRepoRegistado(LocalDateTime.now());
        reporte.setEstado(EstadoReporte.PE);
        reporte.setUsuario(usuario);
        reporte.setPuntosGanados(puntosNuevos); 
        Distrito distrito = distritoRepository.findById(request.getIdDistrito())
                .orElseThrow(() -> new RuntimeException("Distrito no encontrado"));
        reporte.setDistrito(distrito);

        TipoIncidentes tipoInc = tipoIncidenteRepository.findById(request.getIdTipoIncidente())
                .orElseThrow(() -> new RuntimeException(
                    "Tipo incidente no encontrado con ID: " + request.getIdTipoIncidente()
                ));
        reporte.setTipoIncidente(tipoInc);

        TipoClasificacion tipoClas = tipoClasificacionRepository.findById(request.getIdClasificacion())
                .orElseThrow(() -> new RuntimeException(
                    "Tipo clasificación no encontrada con ID: " + request.getIdClasificacion()
                ));
        reporte.setTipoClasificacion(tipoClas);

        Reporte reporteGuardado = reporteRepository.save(reporte);
        log.info("✅ Reporte creado: {}", reporteGuardado.getNumReport());


        //Pa la noti
        notificacionService.notificarReporteRegistrado(
                reporte.getUsuario().getIdUsu(),
                reporte.getIdReporte(),
                reporte.getNumReport()
        );

        return ReporteResponseDTO.builder()
                .idReporte(reporteGuardado.getIdReporte())
                .numReport(reporteGuardado.getNumReport())
                .imagenUrl(imageUrl)
                .estado(reporteGuardado.getEstado().name())
                .puntosGanados(puntosNuevos)
                .tipoIncidente(tipoInc.getDescTipoInci())
                .clasificacion(tipoClas.getDescTipoClasi())
                .nivelRiesgo(tipoClas.getDescTipoClasi())
                .descripcionIA(request.getDescripcionIA())
                .repoRegistrado(reporteGuardado.getRepoRegistado())
                .build();
    }

    public DashboardUsuarioDTO obtenerDashboard(Integer idUsuario) {

        Pageable top5 = PageRequest.of(0, 5);
        LocalDateTime inicioMes = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime finMes = LocalDate.now()
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(23, 59, 59);
        return new DashboardUsuarioDTO(
            reporteRepository.categoriasMasReportes(top5),
            reporteRepository.reportesRecientes(idUsuario, top5),
            usuarioRepository.totalReportes(idUsuario),
            usuarioRepository.findById(idUsuario)
                       .orElseThrow()
                       .getPuntosUsu(),
           usuarioRepository.puntosMesActual(
                   idUsuario,
                   inicioMes,
                   finMes
               ),
           usuarioRepository.totalPersonasBeneficiadas()
        );
    }
    
    private String generarNumeroReporte(){
        Long count = reporteRepository.count() + 1;
        return String.format("rep-%d-%d-%05d",LocalDateTime.now().getYear(), LocalDateTime.now().getDayOfMonth(),count);
    }


    //region Listas y Filtros en reportes

    public List<Reporte>listadoGenerallistadoGeneral(){
        return reporteRepository.findAll();
    }


    //Filtrado triple de reportes
    public List<ReporteFiltroEstadoIncidenteClasificacion> listadoDeReportesPorFiltro(String estado, Integer incidente, Integer clasificacion){
        return reporteRepository.filtrarReportes(estado,incidente,clasificacion);
    }

    public Reporte obtenerReportePorId(Integer idReporte){
        return reporteRepository.findById(idReporte).orElseThrow();
    }

    //endregion

    public Reporte cambiarEstadoEnProceso(Integer idReporte){
        Reporte reportencontrado = obtenerReportePorId(idReporte);


       if (reportencontrado == null){
           throw new RuntimeException("Reporte no encontrado con ID: " + idReporte);
       }

       reportencontrado.setEstado(EstadoReporte.EP);

        notificacionService.notificarCambioEstadoReporte(
                reportencontrado.getUsuario().getIdUsu(),
                reportencontrado.getIdReporte(),
                reportencontrado.getNumReport(),
                "EP",
                reportencontrado.getPuntosGanados()
        );

       return   reporteRepository.save(reportencontrado);
    }

    public Reporte cambiarEstadoEnResuelto(Integer idReporte) throws IllegalAccessException {
        Reporte reportencontrado = obtenerReportePorId(idReporte);


        if (reportencontrado == null){
            throw new RuntimeException("Reporte no encontrado con ID: " + idReporte);
        }

        if (reportencontrado.getEstado() != EstadoReporte.EP ){
            throw new RuntimeException("El reporte tiene que estar en estado En Proceso para actualizar");
        }

        reportencontrado.setEstado(EstadoReporte.RE);
        reportencontrado.setRepoResuelto(LocalDateTime.now());

        //Obtenemos al usuario con su reporte asignado
        Usuario usuarioEncontrado = reportencontrado.getUsuario();

        //calculamos los puntos que va a ganar
        int puntosGanados = usuarioService.sumarPutosReporteClasificacion(reportencontrado.getTipoClasificacion().getIdTipoClasi());

        //Obtenemos los puntos del usuario en ese momento
        int puntosActuales = usuarioEncontrado.getPuntosUsu();
        int puntosNuevos = puntosActuales + puntosGanados; //Sumamos los puntos

        //actualizamos los puntos con los que tenia mas el sumado
        usuarioEncontrado.setPuntosUsu(puntosNuevos);

        //actualizamos al usuario con sus puntos nuevos
        usuarioService.actualizarUsuario(usuarioEncontrado);

        notificacionService.notificarCambioEstadoReporte(
                reportencontrado.getUsuario().getIdUsu(),
                reportencontrado.getIdReporte(),
                reportencontrado.getNumReport(),
                "RE",
                reportencontrado.getPuntosGanados()
        );

        return reporteRepository.save(reportencontrado);
    }

    public ResultadoResponse cancelarReporte(Integer idReporte){
        Reporte reportencontrado = obtenerReportePorId(idReporte);
        ResultadoResponse resultado = new ResultadoResponse();


        if (reportencontrado == null){
            resultado.setValor(false);
            resultado.setMensaje("Error al cancelar el reporte");
            return resultado;
        }


        reportencontrado.setEstado(EstadoReporte.CA);
        resultado.setValor(true);
        resultado.setMensaje("Se cancelo exitosamente el reporte con ID: " + idReporte);

        notificacionService.notificarCambioEstadoReporte(
                reportencontrado.getUsuario().getIdUsu(),
                reportencontrado.getIdReporte(),
                reportencontrado.getNumReport(),
                "CA",
                reportencontrado.getPuntosGanados()
        );

        return   resultado;
    }
    
    // Helper para la ubicacion de las carpetas
    private String obtenerCarpetaPorClasificacion(Integer idClasificacion) {
        return switch (idClasificacion) {
            case 1 -> "GreenGuard/riesgoBajo";
            case 2 -> "GreenGuard/riesgoMedio";
            case 3 -> "GreenGuard/riesgoAlto";
            case 4 -> "GreenGuard/riesgoMuyAlto";
            default -> "GreenGuard/riesgosGenerales";
        };
    }
}
