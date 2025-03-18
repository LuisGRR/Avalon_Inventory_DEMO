package com.avalon.Avalon_Inventory.presentation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avalon.Avalon_Inventory.application.dto.ProviderRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.ProviderResponseDTO;
import com.avalon.Avalon_Inventory.application.dto.PageDto.PageDTO;
import com.avalon.Avalon_Inventory.application.service.ProviderService;
import com.avalon.Avalon_Inventory.infrastructure.projections.ProviderProjection;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/provider")
public class ProviderController {
    @Autowired
    private ProviderService providerService;

    @GetMapping
    @PreAuthorize("hasAuthority('READ')")
    public PageDTO<ProviderResponseDTO> getAllProviders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filter) {

        Page<ProviderResponseDTO> providePage;

        providePage = providerService.getAllProviders(page, size);

        return new PageDTO<>(
                providePage.getContent(),
                providePage.getNumber(),
                providePage.getSize(),
                providePage.getTotalElements(),
                providePage.getTotalPages(),
                providePage.getNumberOfElements());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<ProviderResponseDTO> getProviderById(@PathVariable Long id) {
        ProviderResponseDTO provider = providerService.getProviderById(id);
        return provider != null ? ResponseEntity.ok(provider) : ResponseEntity.notFound().build();
    }

    @GetMapping("/minimal")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<List<ProviderProjection>> getMinimalProvider() {
        List<ProviderProjection> provider = providerService.gerMinimalProvider();
        return provider != null ? ResponseEntity.ok(provider) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<ProviderResponseDTO> createProvider(
            @Valid @RequestBody ProviderRequestDTO providerRequestDTO) {
        ProviderResponseDTO providerResponse = providerService.createProvider(providerRequestDTO);
        return ResponseEntity.ok(providerResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE')")
    public ResponseEntity<ProviderResponseDTO> updateProvider(@PathVariable Long id,
            @Valid @RequestBody ProviderRequestDTO providerRequestDTO) {
        ProviderResponseDTO updateProvider = providerService.updateProvider(id, providerRequestDTO);
        return updateProvider != null ? ResponseEntity.ok(updateProvider) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        boolean isDeleted = providerService.deletePovider(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
