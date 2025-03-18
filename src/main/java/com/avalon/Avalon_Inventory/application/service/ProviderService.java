package com.avalon.Avalon_Inventory.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.avalon.Avalon_Inventory.application.dto.ProviderRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.ProviderResponseDTO;
import com.avalon.Avalon_Inventory.domain.mapper.ProviderMapper;
import com.avalon.Avalon_Inventory.domain.model.Provider;
import com.avalon.Avalon_Inventory.domain.repository.ProviderRepository;
import com.avalon.Avalon_Inventory.infrastructure.projections.ProviderProjection;

@Service
public class ProviderService {
    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ProviderMapper providerMapper;

    public Page<ProviderResponseDTO> getAllProviders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Provider> providersPage = providerRepository.findAll(pageable);

        List<ProviderResponseDTO> providerResponseDTO = providersPage.stream().map(providerMapper::toResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(providerResponseDTO,pageable,providersPage.getTotalElements());
    }

    public ProviderResponseDTO getProviderById(Long id) {
        return providerRepository.findById(id).map(providerMapper::toResponseDTO)
                .orElse(null);
    }

    public List<ProviderProjection> gerMinimalProvider(){
        return providerRepository.findAllProjectedBy();
    }

    public ProviderResponseDTO createProvider(ProviderRequestDTO providerRequestDTO) {
        Provider provider = providerMapper.toEntity(providerRequestDTO);
        provider.setCreatedAt(LocalDateTime.now());
        provider.setUpdatedAt(LocalDateTime.now());

        Provider saveProvider = providerRepository.save(provider);
        return providerMapper.toResponseDTO(saveProvider);
    }

    public ProviderResponseDTO updateProvider(Long id, ProviderRequestDTO providerRequestDTO) {
        return providerRepository.findById(id).map(existingProvider -> {
            providerMapper.updateProductFromDto(providerRequestDTO, existingProvider);
            existingProvider.setUpdatedAt(LocalDateTime.now());
            Provider updateProvider = providerRepository.save(existingProvider);
            return providerMapper.toResponseDTO(updateProvider);
        }).orElse(null);
    }

    public boolean deletePovider(Long id) {
        if (providerRepository.existsById(id)) {
            providerRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
