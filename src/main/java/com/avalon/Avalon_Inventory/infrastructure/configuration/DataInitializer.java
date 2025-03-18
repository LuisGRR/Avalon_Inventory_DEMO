package com.avalon.Avalon_Inventory.infrastructure.configuration;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.avalon.Avalon_Inventory.domain.model.PermissionType;
import com.avalon.Avalon_Inventory.domain.model.Role;
import com.avalon.Avalon_Inventory.domain.model.User;
import com.avalon.Avalon_Inventory.domain.model.utils.PasswordGenerator;
import com.avalon.Avalon_Inventory.domain.repository.RoleRepository;
import com.avalon.Avalon_Inventory.domain.repository.UserRepository;

import jakarta.transaction.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            Role role = createRoleIfNotExists("ADMIN");
            createUserIfNotExists("ADMIN_USER", "testuser2@example.com", role);
        } catch (Exception e) {
            logger.error("Error al crear el usuario predeterminado. Revise si la base de datos está en línea.", e);
            throw e;
        }
    }

    private Role createRoleIfNotExists(String roleName) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            role.setCreatedAt(LocalDateTime.now());
            role.setUpdatedAt(LocalDateTime.now());
            role.setPermissions(Arrays.asList(PermissionType.values()));
            return roleRepository.save(role);
        });
    }

    private void createUserIfNotExists(String username, String email, Role role) {
        try {

            User user = userRepository.findByUsername(username);
            if (user == null) {
                createUser(username, email, role);
            }
        } catch (EmptyResultDataAccessException e) {
            createUser(username, email, role);
        }
    }

    private void createUser(String username, String email, Role role) {
        User user = new User();
        user.setUsername(username);
        String randomPassword = PasswordGenerator.generateRandomPassword(12);
        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setEmail(email);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        logger.info("Generated secure password for ADMIN_USER: " + randomPassword);
    }

}
