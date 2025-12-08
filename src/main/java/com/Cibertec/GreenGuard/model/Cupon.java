package com.Cibertec.GreenGuard.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tb_cupon")
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_cupon")
    private Integer idCupon;

    @Column(name="nombre_cupon")
    private String nombreCupon;

    @Column(name="desc_cupon")
    private String descCupon;

    @ManyToOne
    @JoinColumn(name = "id_cate")
    private Categoria categoria;

    @Column(name="puntos_requeridos")
    private int puntosRequeridos;

    @ManyToOne
    @JoinColumn(name = "id_tienda")
    private Tienda tienda;

    @Column(name="stock_disponible")
    private int stockDisponible;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name="fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Column(name="activo")
    private Boolean activo;
}
