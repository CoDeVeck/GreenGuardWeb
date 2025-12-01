package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INotificacionRepository extends JpaRepository<Notificacion, Integer> {
}
