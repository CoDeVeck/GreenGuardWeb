package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.Notificacion;
import com.Cibertec.GreenGuard.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface INotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    // Listar solo las NO LEÍDAS de un usuario (ordenadas por más reciente)
    List<Notificacion> findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(Usuario usuario);

    // Bonus: Contar no leídas (para mostrar badge con número)
    long countByUsuarioAndLeidaFalse(Usuario usuario);

}
