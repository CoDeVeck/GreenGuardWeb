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
@Table(name = "tb_tipos_incidentes")
public class TipoIncidentes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_tipo_inci")
	private Integer idTipoInci;
	
		@Column(name="desc_tipo_inci")
	private String descTipoInci;
	
}
