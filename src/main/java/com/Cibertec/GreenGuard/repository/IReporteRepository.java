package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.dto.ReporteFiltroEstadoIncidenteClasificacion;
import com.Cibertec.GreenGuard.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IReporteRepository extends JpaRepository<Reporte, Integer> {


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



    //endregion
}
