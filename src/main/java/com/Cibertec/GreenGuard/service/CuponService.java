package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.config.ConfigCupon;
import com.Cibertec.GreenGuard.model.Categoria;
import com.Cibertec.GreenGuard.model.Cupon;
import com.Cibertec.GreenGuard.model.Tienda;
import com.Cibertec.GreenGuard.repository.ICategoriaRepository;
import com.Cibertec.GreenGuard.repository.ICuponRepository;
import com.Cibertec.GreenGuard.repository.ITiendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class CuponService {

    @Autowired
    ICuponRepository cuponRepo;

    @Autowired
    ICategoriaRepository categoriaRepository;

    @Autowired
    ITiendaRepository tiendaRepository;

    //Se necesita tener el cupon y el valor del descuento es decir cuando se aplicara
    //5/10/15...100 soles de descuento para poder hacer el calculo de puntos ncesarios
    public Cupon crearNuevoCupon(Cupon cupon, double valorDescuento){

        Cupon cupoRegistrado = new Cupon();

        cupoRegistrado.setNombreCupon(cupon.getNombreCupon());

        int puntosNecesarios = ConfigCupon.calcularPuntosNecesarios(valorDescuento);

        //Solo tiene que tener letras mas no numeros para evitar q el valor del descuento sea uno S/15
        //y en la descripcion ponngan S/20 abusando del sistema y estafando a la gente
        /*String regexSoloLetras = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$";
        if (!cupon.getDescCupon().matches(regexSoloLetras)){
            throw new RuntimeException("Solo se permite la entrada de letras no numeros!");
        }*/

        Categoria categoria = categoriaRepository.findById(cupon.getCategoria().getIdCate())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        Tienda tienda = tiendaRepository.findById(cupon.getTienda().getIdTienda())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        String categoriaName = categoria.getDescCate().toLowerCase();

        cupoRegistrado.setDescCupon("S/" + valorDescuento +" " + "de descuento en " + categoriaName);
        cupoRegistrado.setCategoria(categoria);
        cupoRegistrado.setTienda(tienda);
        cupoRegistrado.setPuntosRequeridos(puntosNecesarios);
        cupoRegistrado.setStockDisponible(cupon.getStockDisponible());
        cupoRegistrado.setFechaCreacion(LocalDateTime.now());
        cupoRegistrado.setFechaVencimiento(cupon.getFechaVencimiento());
        cupoRegistrado.setActivo(true);
       return cuponRepo.save(cupoRegistrado);
    }



}
