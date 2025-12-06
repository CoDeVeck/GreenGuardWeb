package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IReporteRepository extends JpaRepository<Reporte, Integer> {
	boolean existsByNumReport(String numReport);
}
