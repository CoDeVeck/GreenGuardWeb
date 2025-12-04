package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoriaRepository extends JpaRepository<Categoria, Integer> {
}
