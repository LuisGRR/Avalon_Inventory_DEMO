package com.avalon.Avalon_Inventory.infrastructure.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private String expirationTocken;

    @Value("${jwt.refresh.token.expiration}")
    private String expirationRefreshTocken;

    // private static final long EXPIRATION_TIME = 86400000; // 24 horas

    // Generar un JWT para el usuario
    public String generateToken(UserDetails userDetails, Long userId) {
        Map<String, Object> claims = new HashMap<>();

        // Extraer roles del usuario
        List<String> roles = userDetails.getAuthorities().stream()
                .filter(authority -> authority.getAuthority().startsWith("ROLE_")) // Filtramos los roles
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Extraer permisos del usuario
        List<String> permissions = userDetails.getAuthorities().stream()
                .filter(authority -> !authority.getAuthority().startsWith("ROLE_")) // Filtramos los permisos
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Agregamos roles y permisos al token en diferentes claims
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        claims.put("userId", userId); // Agregar el userId

        return Jwts.builder()
                .setClaims(claims) // Agregamos los claims personalizados
                .setSubject(userDetails.getUsername()) // Usuario
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión
                .setExpiration(new Date(new Date().getTime() + Long.parseLong(expirationTocken)))
                .signWith(SignatureAlgorithm.HS256, secretKey) // Firma con clave secreta
                .compact();
    }

    // Generar Refresh Token
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + Long.parseLong(expirationRefreshTocken)))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Extraer el userId del token
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    // Extraer el nombre de usuario del JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraer una afirmación del token
    private <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    // Extraer todos los claims del JWT
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    // Validar si el token es válido
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        final Long userId = extractUserId(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token) && userId != null);
    }

    // Verificar si el token ha expirado
    private boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration()
                .before(new Date());
    }

    // Extraer la fecha de expiración del JWT
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Interfaz para resolver los claims
    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}
