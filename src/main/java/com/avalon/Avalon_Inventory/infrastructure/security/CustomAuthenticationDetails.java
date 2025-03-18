package com.avalon.Avalon_Inventory.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomAuthenticationDetails {
    private Long userId;
    private String username;   
}
