package com.Cibertec.GreenGuard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tb_distrito")
public class Distrito {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_distrito")
	private Integer idDistrito;
	
	@Column(name="desc_distrito")
	private String descDistrito;
}
