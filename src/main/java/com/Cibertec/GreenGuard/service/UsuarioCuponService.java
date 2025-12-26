package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.dto.CuponCatalagoDTO;
import com.Cibertec.GreenGuard.dto.UsuarioCuponDto;
import com.Cibertec.GreenGuard.dto.response.CanjeCuponResponse;
import com.Cibertec.GreenGuard.dto.response.ResultadoResponse;
import com.Cibertec.GreenGuard.enums.EstadoUsuarioCupon;
import com.Cibertec.GreenGuard.model.Cupon;
import com.Cibertec.GreenGuard.model.Usuario;
import com.Cibertec.GreenGuard.model.UsuarioCupon;
import com.Cibertec.GreenGuard.repository.ICuponRepository;
import com.Cibertec.GreenGuard.repository.IUsuarioCuponRepository;
import com.Cibertec.GreenGuard.util.GeneradorUtil;
import com.google.zxing.WriterException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsuarioCuponService {

	@Autowired
	IUsuarioCuponRepository usuCupoRepo;

	@Autowired
	ICuponRepository cuponRepository;

	@Autowired
	UsuarioService usuarioService;

	@Autowired
	IUsuarioCuponRepository usuarioCuponRepository;

    @Autowired
    NotificacionService notificacionService;

	@Transactional
	public CanjeCuponResponse comprarCupon(Integer idCupon, Integer idUsuario) {

        CanjeCuponResponse resultado = new CanjeCuponResponse();

        UsuarioCupon cuponAdquirido = null;
        try {
            Cupon cuponComprar = cuponRepository.findById(idCupon)
                    .orElseThrow(() -> new RuntimeException("Error al encontrar el cupon"));

            if (cuponComprar.getFechaVencimiento().isBefore(LocalDateTime.now())) {
                resultado.setValor(false);
                resultado.setMensaje("Este cupon ya vencio por lo cual no puede ser adquirido");
                return resultado;
            }

            if (cuponComprar.getStockDisponible() <= 0) {
                resultado.setValor(false);
                resultado.setMensaje("Cupon agotado");
                return resultado;
            }

            Usuario usuario = usuarioService.ObtenerDatosUsuario(idUsuario);

            if (usuario.getPuntosUsu() < cuponComprar.getPuntosRequeridos()) {
                resultado.setValor(false);
                resultado.setMensaje("No tienes suficientes puntos");
                return resultado;
            }

            Optional<UsuarioCupon> ultimoCuponOpt =
                    usuCupoRepo.findTopByUsuarioAndCuponOrderByFechaCanjeDesc(usuario, cuponComprar);

            if (ultimoCuponOpt.isPresent()) {
                UsuarioCupon ultimoCupon = ultimoCuponOpt.get();

                // ❌ Si el último NO está en CA, no puede volver a adquirir
                if (ultimoCupon.getEstado() != EstadoUsuarioCupon.CA) {
                    resultado.setValor(false);
                    resultado.setMensaje(
                            "No puedes adquirir este cupón porque ya tienes uno activo o usado"
                    );
                    return resultado;
                }
            }

            // Descontar puntos y stock
            usuario.setPuntosUsu(usuario.getPuntosUsu() - cuponComprar.getPuntosRequeridos());
            cuponComprar.setStockDisponible(cuponComprar.getStockDisponible() - 1);

            // Crear cupón adquirido
            cuponAdquirido = new UsuarioCupon();
            cuponAdquirido.setCupon(cuponComprar);
            cuponAdquirido.setUsuario(usuario);
            cuponAdquirido.setCodigoCupon(GeneradorUtil.generarCodigoCupon());
            cuponAdquirido.setQrVerificationCode(GeneradorUtil.generarCodigoCupon());
            cuponAdquirido.setFechaCanje(LocalDateTime.now());
            cuponAdquirido.setCanjeado(false);
            cuponAdquirido.setEstado(EstadoUsuarioCupon.AC);

            // GENERAR QR Y GUARDARLO COMO BYTE[]
            try {
                String qrContent = String.format("CUPON:%s|CODIGO:%s|USUARIO:%d|FECHA:%s",
                        cuponAdquirido.getCodigoCupon(), cuponAdquirido.getQrVerificationCode(), idUsuario,
                        cuponAdquirido.getFechaCanje());

                byte[] qrBytes = GeneradorUtil.generateQRCodeBytes(qrContent);
                cuponAdquirido.setQrImage(qrBytes);

                log.info("QR generado exitosamente para cupón {}", cuponAdquirido.getCodigoCupon());

            } catch (Exception e) {
                log.error("Error generando QR para cupón: {}", e.getMessage(), e);
                // Continúa sin el QR - no es crítico
            }

            // Guardar
            usuarioService.actualizarUsuario(usuario);
            usuCupoRepo.save(cuponAdquirido);

            CanjeCuponResponse response = new CanjeCuponResponse();

            response.setValor(true);
            response.setMensaje("¡Cupón canjeado con éxito!");
            response.setCodigoCupon(cuponAdquirido.getCodigoCupon());
            response.setFechaCanje(cuponAdquirido.getFechaCanje().toString());

            if (cuponAdquirido.getQrImage() != null) {
                response.setQrBase64(Base64.getEncoder().encodeToString(cuponAdquirido.getQrImage()));
            }

            notificacionService.notificarCuponCanjeado(
                    idUsuario,
                    cuponAdquirido.getUsuario().getIdUsu(),
                    cuponComprar.getNombreCupon(),
                    cuponComprar.getPuntosRequeridos()
            );

            return response;
        } catch (Exception e) {
            log.error("Error al comprar cupón: {}", e.getMessage(), e);
            resultado.setValor(false);
            resultado.setMensaje("Error al procesar la compra: " + e.getMessage());
        }

        return resultado;
    }

	public ResultadoResponse canjearCupon(Integer id) {

		UsuarioCupon usuarioCupon = usuCupoRepo.findById(id).orElse(null);

		if (usuarioCupon == null) {
			return new ResultadoResponse(false, "ERROR: El cupón de usuario con ID " + id + " no fue encontrado.");
		}

		if (usuarioCupon.getEstado() == EstadoUsuarioCupon.CA) {
			return new ResultadoResponse(false, "ERROR: Este cupón ya ha sido canjeado anteriormente.");
		}

		if (usuarioCupon.getEstado() == EstadoUsuarioCupon.VE) {
			return new ResultadoResponse(false, "ERROR: Este cupón ya fue vencido.");
		}


		try {
			usuarioCupon.setEstado(EstadoUsuarioCupon.CA);

			usuarioCupon.setFechaUso(LocalDateTime.now());

			usuarioCupon.setCanjeado(true);

			usuCupoRepo.save(usuarioCupon);


            notificacionService.notificarCuponUsado(
                    usuarioCupon.getIdUsuarioCupon(),
                    usuarioCupon.getIdUsuarioCupon(),
                    usuarioCupon.getCupon().getNombreCupon(),
                    usuarioCupon.getCupon().getTienda().getNomTienda()
            );

			return new ResultadoResponse(true,
					"Cupón de usuario ID " + id + " canjeado exitosamente. Nuevo estado: CA.");

		} catch (Exception e) {
			System.err.println("Error al guardar el estado del cupón: " + e.getMessage());
			return new ResultadoResponse(false, "ERROR interno al procesar el canje del cupón.");
		}
	}

	public List<UsuarioCuponDto> obtenerCuponesUsuario(Integer idUsu) {
		List<UsuarioCupon> listaObtenida = usuCupoRepo.findByUsuarioIdUsu(idUsu);

		return listaObtenida.stream().map(c -> {
			UsuarioCuponDto dto = new UsuarioCuponDto(c.getIdUsuarioCupon(), c.getCupon().getNombreCupon(),
					c.getCupon().getPuntosRequeridos(), c.getFechaCanje(), c.getEstado(), c.getCodigoCupon(),
					c.getQrVerificationCode(), c.getCupon().getTienda().getNomTienda(),
					c.getCupon().getTienda().getDistrito().getDescDistrito(), null);

			// CONVERTIR BYTE[] A BASE64 PARA ENVIAR AL FRONTEND
			if (c.getQrImage() != null && c.getQrImage().length > 0) {
				String qrBase64 = GeneradorUtil.bytesToBase64(c.getQrImage());
				dto.setQrBase64(qrBase64);
				log.debug("QR convertido a Base64 para cupón {}", c.getIdUsuarioCupon());
			} else {
				log.warn("No hay QR almacenado para cupón {}", c.getIdUsuarioCupon());
				dto.setQrBase64(null);
			}

			return dto;
		}).collect(Collectors.toList());
	}

	@Transactional
	public ResultadoResponse devolverCupon(Integer idCuponUsuario, Integer idUsuario) {

		ResultadoResponse resultado = new ResultadoResponse();

		UsuarioCupon cuponDevolver = usuarioCuponRepository.findById(idCuponUsuario)
				.orElseThrow(() -> new RuntimeException("Error al encontrar el cupon"));

		if (cuponDevolver.getEstado().equals(EstadoUsuarioCupon.CA)) {
			resultado.setValor(false);
			resultado.setMensaje("El cupon fue canjeado y no se puede devolver");
			return resultado;
		}
		;

		if (cuponDevolver.getEstado().equals(EstadoUsuarioCupon.VE)) {
			resultado.setValor(false);
			resultado.setMensaje("El cupon fue VENCIDO y no se puede devolver");
			return resultado;
		}
		;

		Cupon cupon = cuponDevolver.getCupon();

		Usuario usuario = usuarioService.ObtenerDatosUsuario(idUsuario);

		usuario.setPuntosUsu(usuario.getPuntosUsu() + cupon.getPuntosRequeridos());

		cupon.setStockDisponible(cupon.getStockDisponible() + 1);
		cupon.setIdCupon(cuponDevolver.getCupon().getIdCupon());

		usuCupoRepo.deleteById(idCuponUsuario);

		usuarioService.actualizarUsuario(usuario);
		cuponRepository.save(cupon);

		resultado.setValor(true);
		resultado
				.setMensaje("Cupon devuelto correctamente. Se devolvieron  " + cupon.getPuntosRequeridos() + "puntos.");
		return resultado;
	}

	public List<CuponCatalagoDTO> catalagoCupones(Boolean activo, String nombre, Integer categoria, Integer puntosMin,
			Integer puntosMax) {

		List<Object[]> resultados = usuCupoRepo.listaCatalagoCupon(activo, nombre, categoria, puntosMin, puntosMax);

		return resultados.stream()
				.map(obj -> new CuponCatalagoDTO((Integer) obj[0], (String) obj[1], (String) obj[2], (Integer) obj[3],
						(String) obj[4], ((java.sql.Timestamp) obj[5]).toLocalDateTime(), (Integer) obj[6],
						(String) obj[7]))
				.collect(Collectors.toList());
	}

}
