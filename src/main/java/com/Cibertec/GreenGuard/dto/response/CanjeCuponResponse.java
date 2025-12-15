package com.Cibertec.GreenGuard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CanjeCuponResponse {

    private boolean valor;
    private String mensaje;
    private String codigoCupon;
    private String fechaCanje;
    private String qrBase64;
}