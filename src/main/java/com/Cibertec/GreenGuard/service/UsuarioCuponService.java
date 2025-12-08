package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.dto.response.ResultadoResponse;
import com.Cibertec.GreenGuard.enums.EstadoUsuarioCupon;
import com.Cibertec.GreenGuard.model.Cupon;
import com.Cibertec.GreenGuard.model.Usuario;
import com.Cibertec.GreenGuard.model.UsuarioCupon;
import com.Cibertec.GreenGuard.repository.ICuponRepository;
import com.Cibertec.GreenGuard.repository.IUsuarioCuponRepository;
import com.Cibertec.GreenGuard.repository.IUsuarioRepository;
import com.Cibertec.GreenGuard.util.GeneradorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UsuarioCuponService {

    @Autowired
    IUsuarioCuponRepository usuCupoRepo;

    @Autowired
    ICuponRepository cuponRepository;

    @Autowired
    UsuarioService usuarioService;


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

}
