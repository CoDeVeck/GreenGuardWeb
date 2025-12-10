package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.dto.CuponCatalagoDTO;
import com.Cibertec.GreenGuard.dto.UsuarioCuponDto;
import com.Cibertec.GreenGuard.dto.response.ResultadoResponse;
import com.Cibertec.GreenGuard.enums.EstadoUsuarioCupon;
import com.Cibertec.GreenGuard.model.Cupon;
import com.Cibertec.GreenGuard.model.Usuario;
import com.Cibertec.GreenGuard.model.UsuarioCupon;
import com.Cibertec.GreenGuard.repository.ICuponRepository;
import com.Cibertec.GreenGuard.repository.IUsuarioCuponRepository;
import com.Cibertec.GreenGuard.util.GeneradorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioCuponService {

    @Autowired
    IUsuarioCuponRepository usuCupoRepo;

    @Autowired
    ICuponRepository cuponRepository;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    IUsuarioCuponRepository usuarioCuponRepository;

    @Transactional
    public ResultadoResponse comprarCupon(Integer idCupon, Integer idUsuario){

        ResultadoResponse resultado = new ResultadoResponse();

        //Obtener cupon y usuario
        Cupon cuponComprar = cuponRepository.findById(idCupon)
                .orElseThrow(() -> new  RuntimeException("Error al encontrar el cupon"));

        if (cuponComprar.getFechaVencimiento().isBefore(LocalDateTime.now())){
            resultado.setValor(false);
            resultado.setMensaje("Este cupon ya vencio por lo cual no puede ser adquirido");
            return resultado;
        }

        if (cuponComprar.getStockDisponible() <= 0) {
            resultado.setValor(false);
            resultado.setMensaje("Cupon agotado");

        }

        Usuario usuario = usuarioService.ObtenerDatosUsuario(idUsuario);

        if (usuario.getPuntosUsu() < cuponComprar.getPuntosRequeridos()){
            resultado.setValor(false);
            resultado.setMensaje("No tienes suficientes puntos");
            return resultado;
        }

        boolean yaAdquirido = usuCupoRepo.existsByUsuarioAndCupon(usuario, cuponComprar);

        if (yaAdquirido){
            resultado.setValor(false);
            resultado.setMensaje("Este cupon ya fue adquirido previamente");
            return resultado;
        }

        usuario.setPuntosUsu(usuario.getPuntosUsu() - cuponComprar.getPuntosRequeridos());
        cuponComprar.setStockDisponible(cuponComprar.getStockDisponible() - 1);



        UsuarioCupon cuponAdquirido = new UsuarioCupon();

        cuponAdquirido.setCupon(cuponComprar);
        cuponAdquirido.setUsuario(usuario);
        cuponAdquirido.setCodigoCupon(GeneradorUtil.generarCodigoCupon());
        cuponAdquirido.setQrVerificationCode(GeneradorUtil.generarCodigoCupon());
        cuponAdquirido.setFechaCanje(LocalDateTime.now());
        cuponAdquirido.setCanjeado(false);
        cuponAdquirido.setEstado(EstadoUsuarioCupon.AC);

        usuarioService.actualizarUsuario(usuario);
        usuCupoRepo.save(cuponAdquirido);

        resultado.setValor(true);
        resultado.setMensaje("Exito! Adquiriste el cupon: " + cuponComprar.getNombreCupon());

        return resultado;
    }


    public List<UsuarioCuponDto> obtenerCuponesUsuario(Integer idUsu){
       List<UsuarioCupon> listaObtenida = usuCupoRepo.findByUsuarioIdUsu(idUsu);

       return listaObtenida.stream().map( c -> new UsuarioCuponDto(
               c.getIdUsuarioCupon(),
               c.getCupon().getNombreCupon(),
               c.getCupon().getPuntosRequeridos(),
               c.getFechaCanje(),
               c.getEstado(),
               c.getCodigoCupon(),
               c.getQrVerificationCode()
       )).toList();
    }


    @Transactional
    public ResultadoResponse devolverCupon(Integer idCuponUsuario, Integer idUsuario){

        ResultadoResponse resultado = new ResultadoResponse();

        UsuarioCupon cuponDevolver = usuarioCuponRepository.findById(idCuponUsuario)
                .orElseThrow(() -> new  RuntimeException("Error al encontrar el cupon"));

        if ( cuponDevolver.getEstado().equals(EstadoUsuarioCupon.CA)){
            resultado.setValor(false);
            resultado.setMensaje("El cupon fue canjeado y no se puede devolver");
            return  resultado;
        };

        if (cuponDevolver.getEstado().equals(EstadoUsuarioCupon.VE)){
            resultado.setValor(false);
            resultado.setMensaje("El cupon fue VENCIDO y no se puede devolver");
            return  resultado;
        };

        Cupon cupon = cuponDevolver.getCupon();

        Usuario usuario = usuarioService.ObtenerDatosUsuario(idUsuario);

        usuario.setPuntosUsu(usuario.getPuntosUsu() + cupon.getPuntosRequeridos());

        cupon.setStockDisponible(cupon.getStockDisponible() + 1);
        cupon.setIdCupon(cuponDevolver.getCupon().getIdCupon());


        usuCupoRepo.deleteById(idCuponUsuario);

        usuarioService.actualizarUsuario(usuario);
        cuponRepository.save(cupon);

        resultado.setValor(true);
        resultado.setMensaje("Cupon devuelto correctamente. Se devolvieron  " + cupon.getPuntosRequeridos() + "puntos.");
            return resultado;
    }



    public List<CuponCatalagoDTO> catalagoCupones(
            Boolean activo,
            String nombre,
            Integer categoria,
            Integer puntosMin,
            Integer puntosMax){
       
    	List<Object[]> resultados = usuCupoRepo.listaCatalagoCupon(activo, nombre, categoria, puntosMin, puntosMax);
    	
    	return resultados.stream()
                .map(obj -> new CuponCatalagoDTO(
                        (Integer) obj[0],
                        (String) obj[1],
                        (String) obj[2],
                        (Integer) obj[3],
                        (String) obj[4],
                        ((java.sql.Timestamp) obj[5]).toLocalDateTime(),
                        (Integer) obj[6]
                ))
                .collect(Collectors.toList());
    }

}
