package com.Cibertec.GreenGuard.util;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;

public class GeneradorUtil {

    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMEROS = "0123456789";
    private static final SecureRandom random = new SecureRandom();


    public static String generarCadenaAleatoria(String caracteres, int longitud){
        StringBuilder builder = new StringBuilder(longitud);

        for(int i  = 0; i < longitud; i++){
            builder.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return builder.toString();
    }

    public static String generarCodigoPedido(){
        String parte1  = generarCadenaAleatoria(LETRAS, 3);
        String parte2  = generarCadenaAleatoria(LETRAS, 3);
        String parte3  = generarCadenaAleatoria(LETRAS, 3);
        return String.format("CUP-%s-%s-%s", parte1,parte2,parte3);
    }


    public static String generarQr(String texto){

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);

            int size = 150;

            BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, size,size,hints);

            BufferedImage qrImagen = new BufferedImage(size,size, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i< size; i++){
                for (int j = 0; j < size; j++){
                    qrImagen.setRGB(i,j,bitMatrix.get(i,j) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImagen, "png", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());


        } catch (Exception e) {
            throw new RuntimeException("Error al generar el codigo QR: ", e);
        }
    }



}
