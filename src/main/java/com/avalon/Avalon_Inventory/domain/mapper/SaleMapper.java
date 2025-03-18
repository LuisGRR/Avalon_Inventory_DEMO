package com.avalon.Avalon_Inventory.domain.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.avalon.Avalon_Inventory.application.dto.venta.SaleResponseDTO;
import com.avalon.Avalon_Inventory.domain.model.venta.Sale;
import com.avalon.Avalon_Inventory.domain.model.venta.SalesProducts;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    SaleMapper INSTANCE = Mappers.getMapper(SaleMapper.class);

    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "customer.id", target = "customerId"),
            @Mapping(target = "products", expression = "java(mapProducts(sale.getSalesProducts()))")
    })
    SaleResponseDTO toResponseDTO(Sale sale);

    List<SaleResponseDTO> toResponseDTOList(List<Sale> sales);

    default List<SaleResponseDTO.SalesProductsDTO> mapProducts(List<SalesProducts> salesProducts) {
        return salesProducts.stream()
                .map(product -> {
                    SaleResponseDTO.SalesProductsDTO dto = new SaleResponseDTO.SalesProductsDTO();
                    dto.setProductId(product.getProduct().getId());
                    dto.setName(product.getProduct().getName());
                    dto.setDescription(product.getProduct().getDescription());
                    dto.setQuantity(product.getQuantity());
                    dto.setSubtotal(product.getSubtotal());
                    dto.setUnit_price(product.getUnitPrice());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
