package com.Cibertec.GreenGuard.config;

import java.util.Map;

public class ConfigCupon {

    /*
        FORMULA GENERAL PARA EL SISTEMA DE PUNTOS POR CUPON:
        CADA S/1 EQUIVALE A 10 PUNTOS ES DECIR QUE SI UN CUPON ESTA EN
        S/5 SOLES EN ROPA TENDRIAS Q PAGAR 50 PUNTOS

     */

    //s/1 -> 10 puntos
    public static final int CONVERSION_PUNTOS = 10;

    public static final int PUNTOS_MINIMOS = 20;
    public static final int PUNTOS_MAXIMO = 1000;


    public static int calcularPuntosNecesarios(double descuentoSoles){
        int puntosNecesarios = (int) Math.round(descuentoSoles * CONVERSION_PUNTOS);

        puntosNecesarios = Math.max(puntosNecesarios, PUNTOS_MINIMOS);
        puntosNecesarios = Math.min(puntosNecesarios, PUNTOS_MAXIMO);

        return puntosNecesarios;
    }

}
