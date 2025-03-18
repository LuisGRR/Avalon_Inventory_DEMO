package com.avalon.Avalon_Inventory.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.avalon.Avalon_Inventory.application.dto.UserRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.UserResponseDTO;
import com.avalon.Avalon_Inventory.domain.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    /*@Mapping(source = "role_id", target = "role.id")
    User toEntity(UserRequestDTO productRequestDTO);

    @Mapping(source = "role.id", target = "id_role")
    UserResponseDTO toResponseDTO(User user);*/

    User toEntity(UserRequestDTO userRequestDTO);

    @Mapping(source = "role.permissions", target = "permissions")
    @Mapping(source = "role.name", target = "roleName")
    UserResponseDTO toResponseDTO(User user);

    // UserResponseDTO toResponseDTO(User user);
}
