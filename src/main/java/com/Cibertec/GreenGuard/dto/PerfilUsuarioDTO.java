package com.Cibertec.GreenGuard.dto;



import lombok.Data;

@Data
public class PerfilUsuarioDTO {
	private int totalReportes;
	private int totalReportesResueltos;
	private int totalPuntos;
	private int cuponesCanjeado;
	private String tiempoActivo;
	private String imagenUrl;
}
