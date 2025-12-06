package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.dto.ClassificationResponseDTO;
import com.Cibertec.GreenGuard.dto.ReporteRequestDTO;
import com.Cibertec.GreenGuard.dto.ReporteResponseDTO;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;

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

    
    @Transactional
    public ReporteResponseDTO crearReporteConClasificacion(ReporteRequestDTO request) throws IOException {
        
        log.info("Creando reporte CON clasificación pre-obtenida para usuario: {}", request.getIdUsu());
        
        // 1. Validar imagen
        if (request.getImagen() == null || request.getImagen().isEmpty()) {
            throw new IllegalArgumentException("La imagen es requerida");
        }
        
        // 2. Validar que tenga datos de clasificación
        if (request.getIdTipoIncidente() == null || request.getIdClasificacion() == null) {
            throw new IllegalArgumentException("Datos de clasificación requeridos");
        }
        
        log.info("Usando clasificación pre-obtenida: idTipo={}, idClasif={}", 
            request.getIdTipoIncidente(), 
            request.getIdClasificacion());
        
        // 3. Subir imagen a Cloudinary (esto sí es necesario)
        log.info("Subiendo imagen a Cloudinary...");
        String imageUrl = cloudinaryService.uploadImage(
            request.getImagen(), 
            "greenguard/reportes"
        );
        log.info("Imagen subida: {}", imageUrl);
        
        // 4. Generar número de reporte
        String numReport = generarNumeroReporte();
        
        // 5. Crear el reporte
        Reporte reporte = new Reporte();
        reporte.setNumReport(numReport);
        reporte.setDetalleRepo(request.getDetalleRepo());
        reporte.setImagenRepo(imageUrl);
        reporte.setLatitud(request.getLatitud());
        reporte.setLongitud(request.getLongitud());
        reporte.setRepoRegistado(LocalDateTime.now());
        reporte.setEstado(EstadoReporte.PE);
        
        // Buscar entidades relacionadas
        Usuario usuario = usuarioRepository.findById(request.getIdUsu())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        reporte.setUsuario(usuario);
        
        Distrito distrito = distritoRepository.findById(request.getIdDistrito())
                .orElseThrow(() -> new RuntimeException("Distrito no encontrado"));
        reporte.setDistrito(distrito);
        
        // ✅ USAR IDs DE CLASIFICACIÓN PRE-OBTENIDOS (sin volver a escanear)
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
        
        // 6. Guardar
        Reporte reporteGuardado = reporteRepository.save(reporte);
        log.info("✅ Reporte creado: {}", reporteGuardado.getNumReport());
        
        // 7. Construir respuesta
        return ReporteResponseDTO.builder()
                .idReporte(reporteGuardado.getIdReporte())
                .numReport(reporteGuardado.getNumReport())
                .imagenUrl(imageUrl)
                .estado(reporteGuardado.getEstado().name())
                .puntosGanados(request.getPuntosEstimados())           // ✅ Del request
                .tipoIncidente(tipoInc.getDescTipoInci())
                .clasificacion(tipoClas.getDescTipoClasi())
                .nivelRiesgo(tipoClas.getDescTipoClasi())
                .descripcionIA(request.getDescripcionIA())             // ✅ Del request
                .repoRegistrado(reporteGuardado.getRepoRegistado())
                .build();
    }
    
    private String generarNumeroReporte() {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString()
            .substring(0, 6).toUpperCase();
        return "REP-" + timestamp + "-" + random;
    }
}
