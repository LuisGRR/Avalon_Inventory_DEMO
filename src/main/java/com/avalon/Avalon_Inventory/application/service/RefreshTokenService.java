package com.avalon.Avalon_Inventory.application.service;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.avalon.Avalon_Inventory.domain.exception.TokenException;
import com.avalon.Avalon_Inventory.domain.model.RefreshToken;
import com.avalon.Avalon_Inventory.domain.model.User;
import com.avalon.Avalon_Inventory.domain.repository.RefreshTokenRepository;
import com.avalon.Avalon_Inventory.domain.repository.UserRepository;
import com.avalon.Avalon_Inventory.infrastructure.configuration.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh.token.expiration}")
    private String expirationRefreshTocken;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


    // Generar un nuevo Refresh Token
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Eliminar Refresh Token anterior si existe
        refreshTokenRepository.deleteByUser(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().getPermissions().stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.name()))
                        .collect(Collectors.toList()))
                .build();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(jwtUtil.generateRefreshToken(userDetails));
        refreshToken.setExpiryDate(Instant.now().plusMillis(Long.parseLong(expirationRefreshTocken)));

        return refreshTokenRepository.save(refreshToken);
    }

    // Validar Refresh Token
    public RefreshToken validateRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token inválido"));
    }

    // Eliminar Refresh Token
    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public RefreshToken verifyRefreshTokenAndExpiration(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenException("El refresh token no es válido"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenException("El refresh token ha expirado. Inicia sesión nuevamente.");
        }

        return refreshToken;
    }
}
