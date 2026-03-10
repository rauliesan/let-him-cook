package com.daw.security;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Servicio encargado de la generación, validación y extracción de datos de
 * tokens JWT.
 * 
 * El secreto y la expiración se configuran desde
 * {@code application.properties}:
 * jwt.secret – clave HMAC-SHA256 codificada en Base64.
 * jwt.expiration – tiempo de vida del token en milisegundos.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    // ========================
    // Generación de tokens
    // ========================

    /**
     * Genera un token JWT para el usuario proporcionado, sin claims adicionales.
     *
     * @param userDetails datos del usuario autenticado
     * @return token JWT firmado
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    /**
     * Genera un token JWT con claims extra (por ejemplo, rol, id, etc.).
     *
     * @param extraClaims claims adicionales a incluir en el payload
     * @param userDetails datos del usuario autenticado
     * @return token JWT firmado
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ========================
    // Validación de tokens
    // ========================

    /**
     * Comprueba que el token pertenece al usuario y no ha expirado.
     *
     * @param token       token JWT a validar
     * @param userDetails datos del usuario contra el que se valida
     * @return {@code true} si el token es válido
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // ========================
    // Extracción de claims
    // ========================

    /**
     * Extrae el username (subject) del token.
     *
     * @param token token JWT
     * @return username contenido en el subject
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token.
     *
     * @param token token JWT
     * @return fecha de expiración
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim genérico del token usando un resolver.
     *
     * @param token          token JWT
     * @param claimsResolver función que extrae el claim deseado
     * @param <T>            tipo del claim
     * @return valor del claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ========================
    // Métodos privados
    // ========================

    /**
     * Obtiene todos los claims del token tras verificar la firma.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Comprueba si el token ha expirado.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Construye la clave de firma HMAC-SHA a partir del secreto configurado.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
