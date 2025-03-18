package com.avalon.Avalon_Inventory.domain.mapper;

import com.avalon.Avalon_Inventory.application.dto.ProductRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.ProductResponseDTO;
import com.avalon.Avalon_Inventory.domain.model.Category;
import com.avalon.Avalon_Inventory.domain.model.Product;
import com.avalon.Avalon_Inventory.domain.model.Provider;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mappings({
            @Mapping(source = "category_id", target = "category", qualifiedByName = "mapCategoryId"),
            @Mapping(source = "provider_id", target = "provider", qualifiedByName = "mapProviderId")
    })
    Product toEntity(ProductRequestDTO productRequestDTO);

    @Mappings({
            @Mapping(source = "category.id", target = "category_id"),
            @Mapping(source = "provider.id", target = "provider_id"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "updatedAt", target = "updatedAt"),
    })
    ProductResponseDTO toResponseDTO(Product product);

    // Método para actualizar la entidad existente
    @Mapping(target = "id", ignore = true) // Ignorar el ID para evitar sobrescribirlo
    void updateProductFromDto(ProductRequestDTO productRequestDTO, @MappingTarget Product product);

    // Métodos auxiliares para manejar valores nulos en la conversión
    @Named("mapCategoryId")
    default Category mapCategoryId(Long categoryId) {
        if (categoryId == null) {
            return null; // No asignar nada si es nulo
        }
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

    @Named("mapProviderId")
    default Provider mapProviderId(Long providerId) {
        if (providerId == null) {
            return null; // No asignar nada si es nulo
        }
        Provider provider = new Provider();
        provider.setId(providerId);
        return provider;
    }
}
