package com.Cibertec.GreenGuard.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_usu")
	private Integer idUsu;
	
	@Column(name="nom_usu")
	private String nomUsu;
	
	@Column(name="ape_pat_usu")
	private String apePatUsu;
	
	@Column(name="ape_mat_usu")
	private String apeMatUsu;
	
	@Column(name="documento_usu")
	private String documentoUsu;
	
	@Column(name="correo_usu")
	private String correoUsu;
	
	@Column(name="password_usu")
	private String passwordUsu;
	
	@Column(name="telefono_usu")
	private String telefonoUsu;
	
	@Column(name="genero_usu")
	private String generoUsu;

    @Column(name="imagen_usu")
    private String imagenUsu;

	@Column(name="registro_usu")
	private LocalDateTime registroUsu;
	
	@Column(name="puntos_usu")
	private int puntosUsu;
	
	@ManyToOne
	@JoinColumn(name="id_rol")
	private Rol rol;
	
	@Column(name="activo")
	private Boolean activo;
	
	@Column(name="FCM_TOKEN")
	private String fmcToken;
	
	@Column(name="FCM_TOKEN_FECHA")
	private LocalDateTime fmcTokenFecha;

    @JsonIgnore
    @Transient
    private MultipartFile imagenUrl; // para la subida de imagens
}
