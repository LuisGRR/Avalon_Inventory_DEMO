package com.avalon.Avalon_Inventory.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avalon.Avalon_Inventory.application.dto.PageDto.PageDTO;
import com.avalon.Avalon_Inventory.application.dto.venta.SaleRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.venta.SaleResponseDTO;
import com.avalon.Avalon_Inventory.application.service.SaleService;
import com.avalon.Avalon_Inventory.domain.model.venta.StatusSale;
import com.avalon.Avalon_Inventory.infrastructure.security.CustomUserDetails;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @GetMapping
    @PreAuthorize("hasAuthority('READ')")
    public PageDTO<SaleResponseDTO> getAllSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filter,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<SaleResponseDTO> salePage;

        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            salePage = saleService.getAllSales(page, size);
        } else {
            Long userId = userDetails.getId();
            salePage = saleService.getAllSalesId(userId, page, size);
        }

        return new PageDTO<>(
                salePage.getContent(),
                salePage.getNumber(),
                salePage.getSize(),
                salePage.getTotalElements(),
                salePage.getTotalPages(),
                salePage.getNumberOfElements());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<SaleResponseDTO> getSalesById(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {

            return saleService.getSaleById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } else {
            Long userId = userDetails.getId();
            return saleService.getSaleByIdAndUser(userId, id).map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<SaleResponseDTO> createSale(@Valid @RequestBody SaleRequestDTO saleRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        SaleResponseDTO sale = saleService.registerSale(userId, saleRequest);
        return ResponseEntity.ok(sale);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE')")
    public SaleResponseDTO modificarVenta(@PathVariable Long id, @Valid @RequestBody SaleRequestDTO saleRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        return saleService.modificarVenta(userId, isAdmin, id, saleRequestDTO);
    }

    @PutMapping("/{saleId}/estado")
    @PreAuthorize("hasAuthority('UPDATE')")
    public ResponseEntity<String> actualizarEstadoVenta(@PathVariable Long saleId,
            @RequestBody StatusSale nuevoEstado, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getId();
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            saleService.actualizarEstadoVenta(userId, isAdmin, saleId, nuevoEstado);

            return ResponseEntity.ok("Estado de la venta actualizado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el estado de la venta");
        }
    }

}
