package com.Cibertec.GreenGuard.util;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;

public class GeneradorUtil {

    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMEROS = "0123456789";
    private static final SecureRandom random = new SecureRandom();
    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    public static String generarCadenaAleatoria(String caracteres, int longitud){
        StringBuilder builder = new StringBuilder(longitud);

        for(int i  = 0; i < longitud; i++){
            builder.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return builder.toString();
    }

    public static String generarCodigoCupon(){
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

    public static byte[] generateQRCodeBytes(String text, int width, int height) 
            throws WriterException, IOException {
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix bitMatrix = qrCodeWriter.encode(
            text, 
            BarcodeFormat.QR_CODE, 
            width, 
            height, 
            hints
        );
        
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        
        return baos.toByteArray();
    }

    /**
     * Genera un código QR con tamaño por defecto (400x400)
     */
    public static byte[] generateQRCodeBytes(String text) 
            throws WriterException, IOException {
        return generateQRCodeBytes(text, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Genera un código QR y lo devuelve como String Base64
     */
    public static String generateQRCodeBase64(String text, int width, int height) 
            throws WriterException, IOException {
        byte[] qrBytes = generateQRCodeBytes(text, width, height);
        return Base64.getEncoder().encodeToString(qrBytes);
    }

    /**
     * Genera un código QR en Base64 con tamaño por defecto
     */
    public static String generateQRCodeBase64(String text) 
            throws WriterException, IOException {
        return generateQRCodeBase64(text, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Convierte byte array a Base64
     */
    public static String bytesToBase64(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Convierte Base64 a byte array
     */
    public static byte[] base64ToBytes(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return null;
        }
        return Base64.getDecoder().decode(base64);
    }

}
