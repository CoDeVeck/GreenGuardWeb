package com.Cibertec.GreenGuard.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tb_tipo_notificacion")
public class TipoNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_tipo_notificacion")
    private Integer idTipoNotificacion;

    @Column(name="descripcion")
    private String descripcion;

}
