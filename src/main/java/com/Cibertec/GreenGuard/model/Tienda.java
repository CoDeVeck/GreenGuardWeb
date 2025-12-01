package com.Cibertec.GreenGuard.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tb_tienda")
public class Tienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_tienda")
    private Integer idTienda;

    @Column(name="nom_tienda")
    private String nomTienda;

    @ManyToOne
    @JoinColumn(name = "id_usu")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_distrito")
    private Distrito distrito;
}
