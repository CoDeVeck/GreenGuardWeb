package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.Cupon;
import com.Cibertec.GreenGuard.model.Usuario;
import com.Cibertec.GreenGuard.model.UsuarioCupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUsuarioCuponRepository extends JpaRepository<UsuarioCupon, Integer> {

    boolean existsByUsuarioAndCupon(Usuario usuario, Cupon cupon);

    List<UsuarioCupon> findByUsuarioIdUsu(Integer idUsu);
}
