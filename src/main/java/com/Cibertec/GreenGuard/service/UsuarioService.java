package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.dto.ReporteHistorialCliente;
import com.Cibertec.GreenGuard.dto.response.ResultadoResponse;
import com.Cibertec.GreenGuard.enums.EstadoReporte;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    IUsuarioRepository usuarioRepo;

    //region puntos sin cambios
    private static final int PUNTOS_BAJO = 10;
    private static final int PUNTOS_MEDIO = 20;
    private static final int PUNTOS_ALTO = 35;
    private static final int PUNTOS_MUY_ALTO = 50;

    //endregion

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

       Usuario u = usuarioRepo.findByCorreoUsu(username)
               .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.withUsername(u.getCorreoUsu())
                .password("{noop}" + u.getPasswordUsu())
                .roles(u.getRol().getDescripcion().replace("ROLE_",""))
                .build();
    }


    public Integer obtenerIdPorEmail(String email){
        return usuarioRepo.findByCorreoUsu(email)
                .map(Usuario::getIdUsu)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado"));
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


    public Usuario ObtenerDatosUsuario(Integer idUsuario){
        return usuarioRepo.findById(idUsuario).orElseThrow();
    }




    //Obtener los puntos segun el tipo de clasificacion del reporte

    public int sumarPutosReporteClasificacion(Integer idClasificacion) throws IllegalAccessException {

        int totalDePuntosSumar = 0;

        switch (idClasificacion){
            case 1 -> totalDePuntosSumar += PUNTOS_BAJO;
            case 2 -> totalDePuntosSumar += PUNTOS_MEDIO;
            case 3 -> totalDePuntosSumar += PUNTOS_ALTO;
            case 4 -> totalDePuntosSumar += PUNTOS_MUY_ALTO;
            default -> throw  new IllegalAccessException(
                    "Tipo de clasificacion invalido: " + idClasificacion);
        }
        return totalDePuntosSumar;
    }

    public Usuario actualizarUsuario(Usuario usuario){
        return usuarioRepo.save(usuario);
    }


    public List<ReporteHistorialCliente> reporteHistorialClientes(String estado){

        List<Object[]> resultado = usuarioRepo.listaDeReportesDelUsuario(estado);

        return resultado.stream().map( obj -> {
            ReporteHistorialCliente dto = new ReporteHistorialCliente();

            dto.setIdReporte((Integer) obj[0]);
            dto.setImagenRepo((String) obj[1]);
            dto.setIdTipoClasi((Integer) obj[2]);

            //Convertimos STRING a ENUM
            String estadoStr = (String) obj[3];
            dto.setEstado(EstadoReporte.valueOf(estadoStr));

            dto.setRepoRegistado(obj[4] != null ?
                    ((java.sql.Timestamp) obj[4]).toLocalDateTime() : null);
            dto.setRepoProceso(obj[5] != null ?
                    ((java.sql.Timestamp) obj[5]).toLocalDateTime() : null);
            dto.setRepoResuelto(obj[6] != null ?
                    ((java.sql.Timestamp) obj[6]).toLocalDateTime() : null);

            return dto;
        }).collect(Collectors.toList());
    }


}
