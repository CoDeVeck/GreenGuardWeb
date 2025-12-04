package com.Cibertec.GreenGuard.model;

import com.Cibertec.GreenGuard.enums.EstadoReporte;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_reporte")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_reporte")
    private Integer idReporte;

    @Column(name="num_report")
    private String numReport;

    @ManyToOne
    @JoinColumn(name = "id_usu")
    private Usuario usuario;

    @Column(name="detalle_repo")
    private String detalleRepo;

    @Column(name="imagen_repo")
    private String imagenRepo;

    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private EstadoReporte estado;

    @Column(name="latitud")
    private BigDecimal latitud;

    @Column(name="longitud")
    private BigDecimal longitud;

    @ManyToOne
    @JoinColumn(name = "id_tipo_inci")
    private TipoIncidentes tipoIncidente;

    @ManyToOne
    @JoinColumn(name = "id_tipo_clasi")
    private TipoClasificacion tipoClasificacion;

    @ManyToOne
    @JoinColumn(name = "id_distrito")
    private Distrito distrito;

    @Column(name="repo_registrado")
    private LocalDateTime repoRegistado;

    @Column(name="repo_resuelto")
    private LocalDateTime repoResuelto;

    @JsonIgnore
    @Transient
    private MultipartFile imagenUrl; // para la subida de imagens
}
