package com.avalon.Avalon_Inventory.infrastructure.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.avalon.Avalon_Inventory.domain.model.User;

import com.avalon.Avalon_Inventory.domain.repository.UserRepository;
import com.avalon.Avalon_Inventory.infrastructure.security.CustomUserDetails;

import jakarta.transaction.Transactional;

@Service
public class JwtAuthenticationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username); // Buscar usuario en la base de datos
        if (user != null) {
            // Obtenemos los permisos del rol del usuario
            List<GrantedAuthority> authorities = new ArrayList<>();

            // Extraemos los permisos del rol y los convertimos a GrantedAuthority
            List<String> permissions = user.getRole().getPermissions().stream()
                    .map(permission -> permission.name()) // Convertimos PermissionType a String
                    .collect(Collectors.toList());

            // Convertimos los permisos a GrantedAuthority
            authorities.addAll(permissions.stream()
                    .map(permission -> new SimpleGrantedAuthority(permission))
                    .collect(Collectors.toList()));

            // Agregamos el rol como autoridad
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

            // Devolvemos el UserDetails con los roles y permisos
            /**
             * return org.springframework.security.core.userdetails.User
             * .withUsername(user.getUsername())
             * .password(user.getPassword())
             * .authorities(authorities) // Establecemos las autoridades (roles + permisos)
             * .build();
             */
            return CustomUserDetails.builder().id(user.getId()) // Agregar el ID del usuario
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(authorities) // Establecer roles y permisos
                    .build();
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public String authenticateUser(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
       // User user = userRepository.findByUsername(username); // Obtener al usuario desde la base de datos
       CustomUserDetails userDetails = loadUserByUsername(username);

        return jwtUtil.generateToken(userDetails, userDetails.getId());

    }
}
