package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.dto.CuponCatalagoDTO;
import com.Cibertec.GreenGuard.model.Cupon;
import com.Cibertec.GreenGuard.model.Usuario;
import com.Cibertec.GreenGuard.model.UsuarioCupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IUsuarioCuponRepository extends JpaRepository<UsuarioCupon, Integer> {

    boolean existsByUsuarioAndCupon(Usuario usuario, Cupon cupon);

    List<UsuarioCupon> findByUsuarioIdUsu(Integer idUsu);



    //region Todas las Listas de cupones


    @Query(value="""
		           SELECT 
		            c.id_cupon,
		            c.nombre_cupon,
		            c.desc_cupon,
		            c.puntos_requeridos,
		            cat.desc_cate,
		            c.fecha_vencimiento,
		            c.stock_disponible
		        FROM tb_cupon c
		        INNER JOIN tb_categoria cat ON cat.id_cate = c.id_cate
		        WHERE (:activo IS NULL OR c.activo = :activo)
		        AND (:nombre IS NULL OR LOWER(c.nombre_cupon) LIKE LOWER(CONCAT('%' || :nombre || '%')))
		        AND (:categoria IS NULL OR c.id_cate = :categoria)
		        AND (:puntosMin IS NULL OR c.puntos_requeridos >= :puntosMin)
		        AND (:puntosMax IS NULL OR c.puntos_requeridos <= :puntosMax)
		        ORDER BY c.puntos_requeridos ASC
            """, nativeQuery = true)
    List<Object[]> listaCatalagoCupon(
            @Param("activo") Boolean activo,
            @Param("nombre") String nombre,
            @Param("categoria") Integer categoria,
            @Param("puntosMin") Integer puntosmMin,
            @Param("puntosMax") Integer puntosMax
    );
    
    //endregion
}
