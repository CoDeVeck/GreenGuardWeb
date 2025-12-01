package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITipoNotificacionRepository extends JpaRepository<TipoNotificacion, Integer> {
}
