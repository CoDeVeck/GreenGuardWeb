package com.Cibertec.GreenGuard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class CuponCatalagoDTO {

    private Integer idCupon;
    private String nombreCupon;
    private String descCupon;
    private Integer puntosRequeridos;
    private String categoria;
    private LocalDateTime fechaVencimiento;
    private Integer stockDisponible;



}
