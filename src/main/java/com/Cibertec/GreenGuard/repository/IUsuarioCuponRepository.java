package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.Categoria;
import com.Cibertec.GreenGuard.model.UsuarioCupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioCuponRepository extends JpaRepository<UsuarioCupon, Integer> {
}
