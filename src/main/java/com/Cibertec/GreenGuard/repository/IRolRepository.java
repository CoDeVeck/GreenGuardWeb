package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRolRepository extends JpaRepository<Rol, Integer> {

}
