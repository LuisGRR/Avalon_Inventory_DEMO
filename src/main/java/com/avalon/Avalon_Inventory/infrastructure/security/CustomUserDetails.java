package com.avalon.Avalon_Inventory.infrastructure.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;

@Builder
public class CustomUserDetails implements UserDetails {
    private Long id; // Almacena el userId
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Puedes cambiarlo según la lógica de tu sistema
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Puedes cambiarlo según la lógica de tu sistema
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Puedes cambiarlo según la lógica de tu sistema
    }

    @Override
    public boolean isEnabled() {
        return true; // Puedes cambiarlo según la lógica de tu sistema
    }
}
