package com.avalon.Avalon_Inventory.domain.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.avalon.Avalon_Inventory.application.dto.InventoryExitResponseDTO;
import com.avalon.Avalon_Inventory.domain.model.InventoryExit;
import com.avalon.Avalon_Inventory.domain.model.InventoryExitProduct;

@Mapper(componentModel = "spring")
public interface InventoryExitMapper {

    InventoryExitMapper INSTANCE = Mappers.getMapper(InventoryExitMapper.class);

    @Mappings({
            @Mapping(source = "user.id", target = "userId"), // Mapea el ID del usuario
            @Mapping(target = "products", expression = "java(mapProducts(entry.getProducts()))") // Mapea la lista de
    })
    InventoryExitResponseDTO toResponseDTO(InventoryExit entry);

    List<InventoryExitResponseDTO> toResponseDTOList(List<InventoryExit> entries);

    default List<InventoryExitResponseDTO.ProductEntryDTO> mapProducts(List<InventoryExitProduct> products) {
        return products.stream()
                .map(product -> {
                    InventoryExitResponseDTO.ProductEntryDTO dto = new InventoryExitResponseDTO.ProductEntryDTO();
                    dto.setProductId(product.getProduct().getId());
                    dto.setName(product.getProduct().getName());
                    dto.setDescription(product.getProduct().getDescription());
                    dto.setQuantity(product.getQuantity());
                    dto.setSubtotal(product.getSubtotal());
                    dto.setUnit_price(product.getUnit_price());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
