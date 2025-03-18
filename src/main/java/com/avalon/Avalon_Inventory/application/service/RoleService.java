package com.avalon.Avalon_Inventory.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avalon.Avalon_Inventory.domain.model.Role;
import com.avalon.Avalon_Inventory.domain.repository.RoleRepository;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    // Crear un nuevo rol
    public Role createRole(Role role) {
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        return roleRepository.save(role);
    }

    // Obtener todos los roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Obtener un rol por ID
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    // Actualizar un rol
    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(roleDetails.getName());
        role.setPermissions(roleDetails.getPermissions());
        role.setUpdatedAt(LocalDateTime.now());

        return roleRepository.save(role);
    }

    // Eliminar un rol
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
