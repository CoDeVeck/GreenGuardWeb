package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {
	
    Optional<Usuario> findByCorreoUsu(String correoUsu);
    Optional<Usuario> findByTelefonoUsu(String telefonoUsu);
    Optional<Usuario> findByDocumentoUsu(String documentoUsu);
    Optional<Usuario> findByApePatUsu(String apePatUsu);
    Optional<Usuario> findByApeMatUsu(String apeMatUsu);
    
    @Query("""
    	    SELECT COALESCE(SUM(r.puntosGanados), 0)
    	    FROM Reporte r
    	    WHERE r.usuario.idUsu = :idUsuario
    	    AND r.repoRegistado BETWEEN :inicioMes AND :finMes
    	""")
    	Integer puntosMesActual(
    	    @Param("idUsuario") Integer idUsuario,
    	    @Param("inicioMes") LocalDateTime inicioMes,
    	    @Param("finMes") LocalDateTime finMes
    	);

    
    @Query("""
    		SELECT COUNT(R) FROM Reporte R
    		WHERE R.usuario.idUsu = :idUsuario
    		""")
    	int totalReportes(@Param("idUsuario") Integer idUsuario);
    
    @Query("""
    		SELECT COUNT(U) FROM Usuario U
    		""")
    	int totalPersonasBeneficiadas();
    
    //Detalle de reportes

    @Query(value = """
            SELECT 
            r.id_reporte,
            r.imagen_repo,
            inc.desc_tipo_inci,
            cla.id_tipo_clasi,
            r.estado,
            r.repo_registrado,
            r.repo_proceso,
            r.repo_resuelto
            FROM tb_reporte r
            INNER JOIN tb_tipo_clasificacion cla ON cla.id_tipo_clasi = r.id_tipo_clasi
            INNER JOIN tb_tipos_incidentes inc ON inc.id_tipo_inci = r.id_tipo_inci
            WHERE (:estado IS NULL OR r.estado = :estado) 
            AND r.id_usu = :idUsuario
            ORDER BY r.repo_registrado DESC
            """, nativeQuery = true)
    List<Object[]> listaDeReportesDelUsuario(
            @Param("estado")String estado,
            @Param("idUsuario")Integer idUsuario
    );


    @Query(value = """
            SELECT 
             r.id_reporte,
             r.num_report,
             r.imagen_repo,
             cla.id_tipo_clasi,
             inc.desc_tipo_inci,
             r.estado,
             r.latitud,
             r.longitud,
             r.detalle_repo,
             r.repo_registrado,
            r.repo_proceso,
            r.repo_resuelto,
             r.puntos_ganados 
            FROM tb_reporte r  
            INNER JOIN tb_tipo_clasificacion cla ON cla.id_tipo_clasi = r.id_tipo_clasi
            INNER JOIN tb_tipos_incidentes inc ON inc.id_tipo_inci = r.id_tipo_inci
            WHERE r.id_usu = :idUsuario
            AND r.id_reporte = :idReporte
            """, nativeQuery = true)
    List<Object[]>detalleDeReportesCliente(
            @Param("idReporte") Integer idReporte,
            @Param("idUsuario") Integer idUsuario
    ) ;

}
