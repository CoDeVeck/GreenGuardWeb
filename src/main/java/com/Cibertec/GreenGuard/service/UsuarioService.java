package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.dto.DetalleReporteHistorialCliente;
import com.Cibertec.GreenGuard.dto.PerfilUsuarioDTO;
import com.Cibertec.GreenGuard.dto.ReporteHistorialCliente;
import com.Cibertec.GreenGuard.dto.response.ResultadoResponse;
import com.Cibertec.GreenGuard.enums.EstadoReporte;
import com.Cibertec.GreenGuard.model.Rol;
import com.Cibertec.GreenGuard.model.Usuario;
import com.Cibertec.GreenGuard.repository.IReporteRepository;
import com.Cibertec.GreenGuard.repository.IUsuarioCuponRepository;
import com.Cibertec.GreenGuard.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    IUsuarioRepository usuarioRepo;

    @Autowired
    IReporteRepository reporteRepository;
    
    @Autowired
    IUsuarioCuponRepository usuarioCuponRepository;
    
    //region puntos sin cambios
    private static final int PUNTOS_BAJO = 10;
    private static final int PUNTOS_MEDIO = 20;
    private static final int PUNTOS_ALTO = 35;
    private static final int PUNTOS_MUY_ALTO = 50;

    //endregion

    public PerfilUsuarioDTO obtenerPerfil(Integer idUsuario) {

        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PerfilUsuarioDTO dto = new PerfilUsuarioDTO();

        dto.setTotalReportes(
        		reporteRepository.obtenerReportes(idUsuario)
        );

        dto.setTotalReportesResueltos(
        		reporteRepository.obtenerReportesResueltos(idUsuario)
        );

        dto.setCuponesCanjeado(
        		usuarioCuponRepository.countCuponesCanjeados(idUsuario)
        );

        dto.setTotalPuntos(usuario.getPuntosUsu());
        LocalDateTime registro = usuario.getRegistroUsu();
        LocalDateTime ahora = LocalDateTime.now();
        dto.setImagenUrl(usuario.getImagenUsu());
        Period periodo = Period.between(
                registro.toLocalDate(),
                ahora.toLocalDate()
        );

        int años = periodo.getYears();
        int meses = periodo.getMonths();

        String tiempoActivo = "";

        if (años > 0) {
            tiempoActivo += años + (años == 1 ? " año" : " años");
        }

        if (meses > 0) {
            if (!tiempoActivo.isEmpty()) tiempoActivo += ", ";
            tiempoActivo += meses + (meses == 1 ? " mes" : " meses");
        }

        if (tiempoActivo.isEmpty()) {
            tiempoActivo = "Menos de un mes";
        }

        dto.setTiempoActivo(tiempoActivo);

        return dto;
    }
    
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


    public int obtenerPuntosUsuario(Integer id) {
    	Usuario user = usuarioRepo.findById(id).orElseThrow();
    	return user.getPuntosUsu();
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


    public List<ReporteHistorialCliente> reporteHistorialClientes(String estado, Integer idsuario){


        List<Object[]> resultado = usuarioRepo.listaDeReportesDelUsuario(estado, idsuario);

        return resultado.stream().map( obj -> {
            ReporteHistorialCliente dto = new ReporteHistorialCliente();

            dto.setIdReporte((Integer) obj[0]);
            dto.setImagenRepo((String) obj[1]);
            dto.setIncidente((String) obj[2]);

            //Obtenemos el ID de la clasificacion que viene en el native query
            Integer idTipoClasificacion = (Integer) obj[3];
            dto.setIdTipoClasi(idTipoClasificacion);

            //Obtenemos los puntos obtenidos segun el tipo de clasificacion que fue para poder enviar
            //en el JSON
            Integer puntosObtenidos = switch (idTipoClasificacion){
                case 1 -> PUNTOS_BAJO;
                case 2 -> PUNTOS_MEDIO;
                case 3 -> PUNTOS_ALTO;
                case 4 -> PUNTOS_MUY_ALTO;
                default ->  0;
            };

            dto.setPuntosGanados(puntosObtenidos);


            //Convertimos STRING a ENUM
            String estadoStr = (String) obj[4];
            dto.setEstado(EstadoReporte.valueOf(estadoStr));

            dto.setRepoRegistado(obj[5] != null ?
                    ((java.sql.Timestamp) obj[5]).toLocalDateTime() : null);
            dto.setRepoProceso(obj[6] != null ?
                    ((java.sql.Timestamp) obj[6]).toLocalDateTime() : null);
            dto.setRepoResuelto(obj[7] != null ?
                    ((java.sql.Timestamp) obj[7]).toLocalDateTime() : null);

            return dto;
        }).collect(Collectors.toList());
    }


    public List<DetalleReporteHistorialCliente> detalleReporteHistorialClientes(Integer idReporte, Integer idUsuario){

        List<Object[]> resultado = usuarioRepo.detalleDeReportesCliente(idReporte,idUsuario);

        return resultado.stream().map( obj -> {
            DetalleReporteHistorialCliente dto = new DetalleReporteHistorialCliente();

            dto.setIdReporte((Integer) obj[0]);
            dto.setNumeroReporte((String) obj[1]);
            dto.setImagenRepo((String) obj[2]);

            Integer idTipoClasificacion = (Integer) obj[3];
            dto.setIdTipoClasi(idTipoClasificacion);
            dto.setIncidente((String) obj[4]);

            String estadoStr = (String) obj[5];

            dto.setEstado(EstadoReporte.valueOf(estadoStr));
            dto.setLatitud((BigDecimal) obj[6]);
            dto.setLongitud((BigDecimal) obj[7]);
            dto.setDescripcion((String) obj[8]);

            dto.setRepoRegistado(obj[9] != null ?
                    ((java.sql.Timestamp) obj[9]).toLocalDateTime() : null);
            dto.setRepoProceso(obj[10] != null ?
                    ((java.sql.Timestamp) obj[10]).toLocalDateTime() : null);
            dto.setRepoResuelto(obj[11] != null ?
                    ((java.sql.Timestamp) obj[11]).toLocalDateTime() : null);
            dto.setPuntosGanados((Integer) obj[12]);

            return dto;
        }).collect(Collectors.toList());
    }


}
