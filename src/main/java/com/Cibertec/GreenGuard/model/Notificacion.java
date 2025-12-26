package com.Cibertec.GreenGuard.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@DynamicInsert
@Table(name = "tb_notificacion")
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

    @Column(name = "leida")
    private Boolean leida;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

}
