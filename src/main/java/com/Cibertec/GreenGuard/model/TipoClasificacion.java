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
@Table(name = "tb_tipo_clasificacion")
public class TipoClasificacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_tipo_clasi")
	private Integer idTipoClasi;
	
	@Column(name="desc_tipo_clasi")
	private String descTipoClasi;
	
}
