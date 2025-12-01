package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {
}
