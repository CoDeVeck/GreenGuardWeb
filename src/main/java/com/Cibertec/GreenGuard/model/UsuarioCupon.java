package com.Cibertec.GreenGuard.model;

import com.Cibertec.GreenGuard.enums.EstadoUsuarioCupon;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tb_usuario_cupon")
public class UsuarioCupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario_cupon")
    private Integer idUsuarioCupon;

    @ManyToOne
    @JoinColumn(name = "id_cupon")
    private Cupon cupon;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name="codigo_cupon")
    private String CodigoCupon;

    @Column(name="qr_verification_code")
    private String qrVerificationCode;

    @Column(name="fecha_canje")
    private LocalDateTime fechaCanje; //fecha q obtuvo el cupon

    @Column(name="canjeado")
    private Boolean canjeado; //false por default

    @Column(name="fecha_uso")
    private LocalDateTime fechaUso;

    @Column(name="estado")
    private EstadoUsuarioCupon estado;
}
