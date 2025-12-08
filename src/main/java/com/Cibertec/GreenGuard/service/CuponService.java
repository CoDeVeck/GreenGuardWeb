package com.Cibertec.GreenGuard.service;

import com.Cibertec.GreenGuard.model.Categoria;
import com.Cibertec.GreenGuard.model.Cupon;
import com.Cibertec.GreenGuard.repository.ICuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuponService {

    @Autowired
    ICuponRepository cuponRepo;

    private String generarCodigoCupon(){
        Long
    }

    public Cupon crearNuevoCupon(Cupon cupon){

        Cupon cupoRegistrado = new Cupon();

        cupoRegistrado.setNombreCupon(cupon.getNombreCupon());

        String regexSoloLetras = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$";

        if (!cupon.getDescCupon().contains(regexSoloLetras)){
            throw new RuntimeException("Solo se permite la entrada de letras no numeros!");
        }

        cupoRegistrado.setDescCupon(cupon.getDescCupon());

        Categoria categoria = new Categoria();
        categoria.setIdCate(cupon.getCategoria().getIdCate());

        cupoRegistrado.setCategoria(categoria);

    }



}
