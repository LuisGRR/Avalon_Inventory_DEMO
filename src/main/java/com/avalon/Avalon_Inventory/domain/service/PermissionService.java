package com.avalon.Avalon_Inventory.domain.service;

import org.springframework.stereotype.Service;

import com.avalon.Avalon_Inventory.domain.model.PermissionType;
import com.avalon.Avalon_Inventory.domain.model.User;

@Service
public class PermissionService {
     public boolean hasPermission(User user, PermissionType permission) {
        return user.getRole().getPermissions().contains(permission);
    }
}
