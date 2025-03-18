package com.avalon.Avalon_Inventory.domain.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.avalon.Avalon_Inventory.application.dto.InventoryEntryResponseDTO;
import com.avalon.Avalon_Inventory.domain.model.InventoryEntry;
import com.avalon.Avalon_Inventory.domain.model.InventoryEntryProduct;

@Mapper(componentModel = "spring")
public interface InventoryEntryMapper {

  InventoryEntryMapper INSTANCE = Mappers.getMapper(InventoryEntryMapper.class);

  @Mappings({
      @Mapping(source = "user.id", target = "userId"), // Mapea el ID del usuario
      @Mapping(target = "products", expression = "java(mapProducts(entry.getProducts()))")
  })
  InventoryEntryResponseDTO toResponseDTO(InventoryEntry entry);

  List<InventoryEntryResponseDTO> toResponseDTOList(List<InventoryEntry> entries);

  default List<InventoryEntryResponseDTO.ProductEntryDTO> mapProducts(List<InventoryEntryProduct> products) {
    return products.stream()
        .map(product -> {
          InventoryEntryResponseDTO.ProductEntryDTO dto = new InventoryEntryResponseDTO.ProductEntryDTO();
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
