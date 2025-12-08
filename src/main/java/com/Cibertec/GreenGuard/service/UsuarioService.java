package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.dto.response.ResultadoResponse;
import com.Cibertec.GreenGuard.model.Rol;
import com.Cibertec.GreenGuard.model.Usuario;
import com.Cibertec.GreenGuard.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    IUsuarioRepository usuarioRepo;




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

       Usuario u = usuarioRepo.findByCorreoUsu(username)
               .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.withUsername(u.getCorreoUsu())
                .password("{noop}" + u.getPasswordUsu())
                .roles(u.getRol().getDescripcion().replace("ROLE_",""))
                .build();
    }

    public Optional<Usuario> obtenerDatos(String correo){
        return usuarioRepo.findByCorreoUsu(correo);
    }

    public ResultadoResponse createUser(Usuario user){
        ResultadoResponse resultado = new ResultadoResponse();

        if (usuarioRepo.findByCorreoUsu(user.getCorreoUsu()).isPresent()){
            resultado.setValor(false);
            resultado.setMensaje("El correo ingresado ya existe elige otro");
        }

        if (usuarioRepo.findByTelefonoUsu(user.getTelefonoUsu()).isPresent()){
            resultado.setValor(false);
            resultado.setMensaje("El telefono ingresado ya existe elige otro");
        }

        if (usuarioRepo.findByDocumentoUsu(user.getDocumentoUsu()).isPresent()){
            resultado.setValor(false);
            resultado.setMensaje("El N°: " + user.getDocumentoUsu()+ " ya fue registrado elige otro");
        }

        if (usuarioRepo.findByApePatUsu(user.getApePatUsu()).isPresent() && usuarioRepo.findByApeMatUsu(user.getApeMatUsu()).isPresent()){
            resultado.setValor(false);
            resultado.setMensaje("Los apellidos ingresados ya fueron registrados elige otro");
        }

        Rol rolUsuarioDefault = new Rol();
            rolUsuarioDefault.setIdRol(2);
        user.setRol(rolUsuarioDefault);
        user.setRegistroUsu(LocalDateTime.now());
        user.setActivo(true);

        usuarioRepo.save(user);
        resultado.setValor(true);
        resultado.setMensaje("El usuario fue creado correctamente");

        return resultado;
    }
}
