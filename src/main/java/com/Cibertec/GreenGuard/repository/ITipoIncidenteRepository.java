package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.TipoIncidentes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITipoIncidenteRepository extends JpaRepository< TipoIncidentes ,Integer> {
}
