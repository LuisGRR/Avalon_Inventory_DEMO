package com.avalon.Avalon_Inventory.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.avalon.Avalon_Inventory.application.dto.ProviderRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.ProviderResponseDTO;
import com.avalon.Avalon_Inventory.domain.model.Provider;

@Mapper(componentModel = "spring")
public interface ProviderMapper {

    ProviderMapper INSTANCE = Mappers.getMapper(ProviderMapper.class);

    Provider toEntity(ProviderRequestDTO providerRequestDTO);

    ProviderResponseDTO toResponseDTO(Provider provider);

    // @Mapping(target = "id", ignore = true) // Ignorar el ID para evitar
    // sobrescribirlo
    void updateProductFromDto(ProviderRequestDTO providerRequestDTO, @MappingTarget Provider provider);
}
