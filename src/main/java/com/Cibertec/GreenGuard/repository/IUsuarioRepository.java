package com.Cibertec.GreenGuard.repository;

import com.Cibertec.GreenGuard.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {




    Optional<Usuario> findByCorreoUsu(String correoUsu);
    Optional<Usuario> findByTelefonoUsu(String telefonoUsu);
    Optional<Usuario> findByDocumentoUsu(String documentoUsu);
    Optional<Usuario> findByApePatUsu(String apePatUsu);
    Optional<Usuario> findByApeMatUsu(String apeMatUsu);


}
