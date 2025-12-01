package com.Cibertec.GreenGuard.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@DynamicInsert
@Table(name = "tb_tipo_notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer idNotificacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_tipo_notificacion")
    private TipoNotificacion tipoNotificacion;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "mensaje")
    private String mensaje;

    @ManyToOne
    @JoinColumn(name = "id_reporte")
    private Reporte reporte;

    @ManyToOne
    @JoinColumn(name = "id_usuario_cupon")
    private UsuarioCupon usuarioCupon;

    @ManyToOne
    @JoinColumn(name = "id_cupon")
    private Cupon cupon;

    @Column(name = "leida")
    private Boolean leida;

    @Column(name = "descartada")
    private Boolean descartada;

}
