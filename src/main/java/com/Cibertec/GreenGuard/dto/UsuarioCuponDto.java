package com.Cibertec.GreenGuard.dto;

import com.Cibertec.GreenGuard.enums.EstadoUsuarioCupon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Getter 
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioCuponDto {
	private Integer idUsuarioCupon;
    private String nombreCupon;
    private Integer puntosRequeridos;
    private LocalDateTime fechaCanje;
    private EstadoUsuarioCupon estado;
    private String codigoCupon;
    private String qrVerificationCode;
    private String nombreTienda;
    private String distritoTienda;
    private String qrBase64;
}
