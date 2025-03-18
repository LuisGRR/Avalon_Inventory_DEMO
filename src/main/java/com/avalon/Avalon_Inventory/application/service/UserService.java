package com.avalon.Avalon_Inventory.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.avalon.Avalon_Inventory.application.dto.UserDetailsLoginResponseDTO;
import com.avalon.Avalon_Inventory.application.dto.UserRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.UserResponseDTO;
import com.avalon.Avalon_Inventory.domain.mapper.UserMapper;
import com.avalon.Avalon_Inventory.domain.model.User;
import com.avalon.Avalon_Inventory.domain.repository.RoleRepository;
import com.avalon.Avalon_Inventory.domain.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Declaración de la variable que se inyecta automáticamente

    // Crear un nuevo usuario
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user = userMapper.toEntity(userRequestDTO);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        
        String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
        user.setPassword(encodedPassword); // Asigna la contraseña cifrada

        user.setRole(roleRepository.findById(userRequestDTO.getRole_id())
                .orElseThrow(() -> new RuntimeException("Role not found")));
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    // Obtener todos los usuarios
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    // Obtener un usuario por ID
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponseDTO);
    }

    public UserDetailsLoginResponseDTO getUserByIdLogin(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return new UserDetailsLoginResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().getName(),
                user.getRole().getPermissions()
            );
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    // Actualizar un usuario
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        user.setRole(roleRepository.findById(userRequestDTO.getRole_id())
                .orElseThrow(() -> new RuntimeException("Role not found")));
        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    // Eliminar un usuario
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
