package com.avalon.Avalon_Inventory.application.dto;

import java.util.List;

import com.avalon.Avalon_Inventory.domain.model.PermissionType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username; 
    private String email;
    private String roleName;
    private List<PermissionType> permissions;
}
