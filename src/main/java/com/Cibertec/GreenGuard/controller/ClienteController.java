package com.Cibertec.GreenGuard.controller;

import com.Cibertec.GreenGuard.dto.CuponCatalagoDTO;
import com.Cibertec.GreenGuard.dto.DashboardUsuarioDTO;
import com.Cibertec.GreenGuard.dto.DetalleReporteHistorialCliente;
import com.Cibertec.GreenGuard.dto.PerfilUsuarioDTO;
import com.Cibertec.GreenGuard.dto.ReporteHistorialCliente;
import com.Cibertec.GreenGuard.dto.UsuarioCuponDto;
import com.Cibertec.GreenGuard.dto.response.CanjeCuponResponse;
import com.Cibertec.GreenGuard.dto.response.ResultadoResponse;
import com.Cibertec.GreenGuard.model.UsuarioCupon;
import com.Cibertec.GreenGuard.service.ReporteService;
import com.Cibertec.GreenGuard.service.UsuarioCuponService;
import com.Cibertec.GreenGuard.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    UsuarioCuponService usuarioCuponService;

    @Autowired
    UsuarioService usuarioService;
    
    @Autowired
    ReporteService reporteService;
    
    @GetMapping("/dashboard/{idUsuario}")
    public DashboardUsuarioDTO dashboard(@PathVariable Integer idUsuario) {
        return reporteService.obtenerDashboard(idUsuario);
    }

    @GetMapping("/{idUsuario}/puntos")
    public ResponseEntity<Integer> obtenerPuntos(@PathVariable Integer idUsuario) {
        Integer puntos = usuarioService.obtenerPuntosUsuario(idUsuario);
        return ResponseEntity.ok(puntos);
    }
    
    @GetMapping("/perfil/{idUsuario}")
    public ResponseEntity<PerfilUsuarioDTO> obtenerPerfil(
            @PathVariable Integer idUsuario
    ) {
        PerfilUsuarioDTO perfil = usuarioService.obtenerPerfil(idUsuario);
        return ResponseEntity.ok(perfil);
    }
    
    @PostMapping("/comprarCupon/{idCupon}")
    public ResponseEntity<?> comprarCupon(@PathVariable("idCupon") Integer idCupon, @AuthenticationPrincipal UserDetails userDetails){

        try {
            String emailUsuario = userDetails.getUsername();
            Integer idUsuario = usuarioService.obtenerIdPorEmail(emailUsuario);

            CanjeCuponResponse resultadoResponse = usuarioCuponService.comprarCupon(idCupon,idUsuario);

            return ResponseEntity.ok(resultadoResponse);

        } catch (Exception e) {
            throw new RuntimeException("Error: ",e);
        }
    }

    @PutMapping("/canjear/{id}")
    public ResponseEntity<ResultadoResponse> canjearCupon(@PathVariable Integer id) {
        
        ResultadoResponse resultado = usuarioCuponService.canjearCupon(id);

        if (resultado.isValor()) {
            return ResponseEntity.ok(resultado);
        } else {
            
            if (resultado.getMensaje().contains("no fue encontrado")) {
                return new ResponseEntity<>(resultado, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(resultado, HttpStatus.BAD_REQUEST);
            }
        }
    }
    
    @GetMapping("/misCupones")
    public ResponseEntity<?> misCupones(@AuthenticationPrincipal UserDetails userDetails){
        String emailUsuario = userDetails.getUsername();
        Integer idUsu = usuarioService.obtenerIdPorEmail(emailUsuario);

        List<UsuarioCuponDto> cupones = usuarioCuponService.obtenerCuponesUsuario(idUsu);

        return ResponseEntity.ok(cupones);
    }

    @PostMapping("/misCupones/{idCuponUsuario}/devolver")
    public ResponseEntity<?>devolverCupones(@PathVariable Integer idCuponUsuario,
                                            @AuthenticationPrincipal UserDetails userDetails){

        String emailUsuario = userDetails.getUsername();
        Integer idUsu = usuarioService.obtenerIdPorEmail(emailUsuario);

        ResultadoResponse resultado = usuarioCuponService.devolverCupon(idCuponUsuario, idUsu);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/catalogo")
    public ResponseEntity<?> catalogoCupones(
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer categoria,
            @RequestParam(required = false) Integer puntosMin,
            @RequestParam(required = false) Integer puntosMax
    ){
        List<CuponCatalagoDTO> lista =
                usuarioCuponService.catalagoCupones(activo, nombre, categoria, puntosMin, puntosMax);

        return ResponseEntity.ok(lista);
    }
    
    //reporte
    @GetMapping("/reporte")
    public ResponseEntity<?> historialDeReportes(
           @RequestParam(required = false) String estado,
           @AuthenticationPrincipal UserDetails userDetails
    ){
        String emailUsuario = userDetails.getUsername();
        Integer idUsu = usuarioService.obtenerIdPorEmail(emailUsuario);

        List<ReporteHistorialCliente> listaReporteCliente =
                usuarioService.reporteHistorialClientes(estado, idUsu);

        return ResponseEntity.ok(listaReporteCliente);
    }

    @GetMapping("/detalleReporte/{idReporte}")
    public ResponseEntity<?> detalleDeReportes(
            @PathVariable("idReporte")Integer idReporte,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        String emailUsuario = userDetails.getUsername();
        Integer idUsu = usuarioService.obtenerIdPorEmail(emailUsuario);

        List<DetalleReporteHistorialCliente> reporteHistorialCliente =
                usuarioService.detalleReporteHistorialClientes(idReporte,idUsu);

        return ResponseEntity.ok(reporteHistorialCliente);
    }

}
