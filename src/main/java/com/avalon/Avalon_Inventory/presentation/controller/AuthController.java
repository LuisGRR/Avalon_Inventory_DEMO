package com.avalon.Avalon_Inventory.presentation.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avalon.Avalon_Inventory.application.dto.LoginRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.RefreshTokenRequest;
import com.avalon.Avalon_Inventory.application.dto.UserDetailsLoginResponseDTO;
import com.avalon.Avalon_Inventory.application.dto.UserRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.UserResponseDTO;
import com.avalon.Avalon_Inventory.application.service.RefreshTokenService;
import com.avalon.Avalon_Inventory.application.service.UserService;
import com.avalon.Avalon_Inventory.domain.exception.UserRegistrationException;
import com.avalon.Avalon_Inventory.domain.model.RefreshToken;
import com.avalon.Avalon_Inventory.domain.repository.UserRepository;
import com.avalon.Avalon_Inventory.infrastructure.configuration.JwtAuthenticationService;
import com.avalon.Avalon_Inventory.infrastructure.configuration.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtAuthenticationService jwtAuthenticationService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO user) {
        try {
            String accessToken = jwtAuthenticationService.authenticateUser(user.getUser(), user.getPass());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUser());

            UserDetailsLoginResponseDTO userData = userService.getUserByIdLogin(refreshToken.getUser().getId());

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken.getToken(),
                    "userData", userData));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales incorrectas"));
        }
    }

    // Endpoint para registrar usuario
    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') and hasAuthority('CREATE')")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDTO user) {
        try {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new UserRegistrationException("El usuario ya existe.");
            }

            UserResponseDTO newUser = userService.createUser(user);

            return ResponseEntity.ok(Map.of("message", "Usuario registrado exitosamente", "userId", newUser));

        } catch (UserRegistrationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al registrar el usuario"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            // RefreshToken validRefreshToken =
            // refreshTokenService.validateRefreshToken(request.getRefreshToken());
            RefreshToken validRefreshToken = refreshTokenService
                    .verifyRefreshTokenAndExpiration(request.getRefreshToken());
            UserDetails userDetails = userDetailsService.loadUserByUsername(validRefreshToken.getUser().getUsername());

            // Generamos un nuevo Access Token
            String newAccessToken = jwtUtil.generateToken(userDetails, validRefreshToken.getUser().getId());

            // return ResponseEntity.ok(new RefreshTokenRequest(newAccessToken));

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
