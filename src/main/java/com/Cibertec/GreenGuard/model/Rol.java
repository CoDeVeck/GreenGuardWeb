package com.Cibertec.GreenGuard.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tb_rol")
public class Rol {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_rol")
	private Integer idRol;
	
	@Column(name="descripcion")
	private String descripcion;
}
