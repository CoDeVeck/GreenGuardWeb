package com.Cibertec.GreenGuard.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {

    private final Key key = Keys.hmacShaKeyFor("SK_443ff638f8a74ba659cdb8336bfb5f91".getBytes());

    public String generateToken(String username, List<String> roles){
        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", roles);
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) //Maximo 10 horas
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
    }

    public String obtenerUsuarioAndToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validarToken(String token){
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }


}
