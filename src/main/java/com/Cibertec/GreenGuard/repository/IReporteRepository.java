package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.dto.ReporteFiltroEstadoIncidenteClasificacion;
import com.Cibertec.GreenGuard.dto.ReporteStatsDTO;
import com.Cibertec.GreenGuard.model.Reporte;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IReporteRepository extends JpaRepository<Reporte, Integer> {
	boolean existsByNumReport(String numReport);

	@Query("""
		    SELECT COUNT(r)
		    FROM Reporte r
		    WHERE r.usuario.idUsu = :idUsuario
		      AND r.estado = com.Cibertec.GreenGuard.enums.EstadoReporte.RE
		""")
	int obtenerReportesResueltos(@Param("idUsuario") Integer idUsuario);

	@Query("""
		    SELECT COUNT(r)
		    FROM Reporte r
		    WHERE r.usuario.idUsu = :idUsuario
		""")
	int obtenerReportes(@Param("idUsuario") Integer idUsuario);
	
	
    //region lista de Reportes personalizados
    @Query("""
            SELECT NEW com.Cibertec.GreenGuard.dto.ReporteFiltroEstadoIncidenteClasificacion
            (   r.idReporte,
                r.imagenRepo,
                r.estado,
                r.tipoIncidente.idTipoInci,
                r.tipoClasificacion.idTipoClasi,
                r.detalleRepo,
                r.repoRegistado) FROM Reporte r
            WHERE (:estado IS NULL OR CAST(r.estado AS String) = :estado)
                AND(:incidente IS NULL OR r.tipoIncidente.idTipoInci = :incidente)
                AND(:clasificacion IS NULL OR r.tipoClasificacion.idTipoClasi = :clasificacion)
            ORDER BY r.repoRegistado DESC
            """)
    List<ReporteFiltroEstadoIncidenteClasificacion> filtrarReportes(@Param("estado")String estado,
                                                                    @Param("incidente")Integer incidente,
                                                                    @Param("clasificacion")Integer clasificacion);
    
    @Query("""
    	    SELECT r
    	    FROM Reporte r
    	    WHERE r.usuario.idUsu = :idUsuario
    	    ORDER BY r.repoRegistado DESC
    	""")
    	List<Reporte> reportesRecientes(
    	    @Param("idUsuario") Integer idUsuario,
    	    Pageable pageable
    	);


        @Query("""
            SELECT new com.Cibertec.GreenGuard.dto.ReporteStatsDTO(
                r.tipoIncidente,
                COUNT(r)
            )
            FROM Reporte r
            GROUP BY r.tipoIncidente
            ORDER BY COUNT(r) DESC
        """)
        List<ReporteStatsDTO> categoriasMasReportes(Pageable pageable);
}
