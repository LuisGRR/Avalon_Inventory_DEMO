package com.avalon.Avalon_Inventory.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.avalon.Avalon_Inventory.application.dto.CategoryRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.CategoryResponseDTO;
import com.avalon.Avalon_Inventory.domain.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    // Mapea de Category (Entidad) a CategoryResponseDTO (DTO de respuesta)
    CategoryResponseDTO categoryToCategoryResponseDTO(Category category);

    // Mapea de CategoryRequestDTO (DTO de solicitud) a Category (Entidad)
    @Mapping(target = "id", ignore = true)
    Category categoryRequestDTOToCategory(CategoryRequestDTO categoryRequestDTO);

    @Mapping(target = "id", ignore = true) // Ignorar el ID para evitar sobrescribirlo
    void updateCategorytFromDto(CategoryRequestDTO categoryRequestDTO, @MappingTarget Category category);
}
