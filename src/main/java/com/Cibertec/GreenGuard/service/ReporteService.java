package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.dto.ReporteFiltroEstadoIncidenteClasificacion;
import com.Cibertec.GreenGuard.dto.response.ResultadoResponse;
import com.Cibertec.GreenGuard.enums.EstadoReporte;
import com.Cibertec.GreenGuard.model.*;
import com.Cibertec.GreenGuard.repository.IReporteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    IReporteRepository reporRepo;

    @Autowired
    UsuarioService usuarioService;

    public Reporte registrarReporte(Reporte reporte, Integer idUsuario){


        Usuario usuario = usuarioService.ObtenerDatosUsuario(idUsuario);

        if (usuario == null){
            throw new EntityNotFoundException("Usuario no encontrado con el ID: "+ idUsuario);
        }

        Reporte reporteRegistrtado = new Reporte();

        reporteRegistrtado.setUsuario(usuario);
        reporteRegistrtado.setDetalleRepo(reporte.getDetalleRepo());


        reporteRegistrtado.setEstado(EstadoReporte.PE);
        reporteRegistrtado.setLatitud(reporte.getLatitud());
        reporteRegistrtado.setLongitud(reporte.getLongitud());
        reporteRegistrtado.setImagenRepo(reporte.getImagenRepo());

        TipoIncidentes incidenteReporte = new TipoIncidentes();
        incidenteReporte.setIdTipoInci(reporte.getTipoIncidente().getIdTipoInci());

        TipoClasificacion tipoClasificacion = new TipoClasificacion();
        tipoClasificacion.setIdTipoClasi(reporte.getTipoClasificacion().getIdTipoClasi());

        Distrito distritoUsuario = new Distrito();
        distritoUsuario.setIdDistrito(usuario.getDistrito().getIdDistrito());

        reporteRegistrtado.setNumReport(generarNumeroReporte());
        reporteRegistrtado.setDistrito(distritoUsuario);
        reporteRegistrtado.setTipoIncidente(incidenteReporte);
        reporteRegistrtado.setTipoClasificacion(tipoClasificacion);
        reporteRegistrtado.setRepoRegistado(LocalDateTime.now());

        return reporRepo.save(reporteRegistrtado);
    }

    private String generarNumeroReporte(){
        Long count = reporRepo.count() + 1;
        return String.format("rep-%d-%d-%05d",LocalDateTime.now().getYear(), LocalDateTime.now().getDayOfMonth(),count);
    }


    //region Listas y Filtros en reportes

    public List<Reporte>listadoGenerallistadoGeneral(){
        return reporRepo.findAll();
    }


    //Filtrado triple de reportes
    public List<ReporteFiltroEstadoIncidenteClasificacion> listadoDeReportesPorFiltro(String estado, Integer incidente, Integer clasificacion){
        return reporRepo.filtrarReportes(estado,incidente,clasificacion);
    }

    public Reporte obtenerReportePorId(Integer idReporte){
        return reporRepo.findById(idReporte).orElseThrow();
    }

    //endregion


    public Reporte cambiarEstadoEnProceso(Integer idReporte){
        Reporte reportencontrado = obtenerReportePorId(idReporte);


       if (reportencontrado == null){
           throw new RuntimeException("Reporte no encontrado con ID: " + idReporte);
       }

       reportencontrado.setEstado(EstadoReporte.EP);

       return   reporRepo.save(reportencontrado);
    }

    public Reporte cambiarEstadoEnResuelto(Integer idReporte){
        Reporte reportencontrado = obtenerReportePorId(idReporte);


        if (reportencontrado == null){
            throw new RuntimeException("Reporte no encontrado con ID: " + idReporte);
        }

        if (reportencontrado.getEstado() != EstadoReporte.EP ){
            throw new RuntimeException("El reporte tiene que estar en estado En Proceso para actualizar");
        }

        reportencontrado.setEstado(EstadoReporte.RE);
        reportencontrado.setRepoResuelto(LocalDateTime.now());

        return   reporRepo.save(reportencontrado);
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

        return   resultado;
    }




}
