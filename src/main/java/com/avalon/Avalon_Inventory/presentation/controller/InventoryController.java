package com.avalon.Avalon_Inventory.presentation.controller;

import com.avalon.Avalon_Inventory.application.dto.InventoryEntryRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.InventoryEntryResponseDTO;
import com.avalon.Avalon_Inventory.application.dto.InventoryExitRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.InventoryExitResponseDTO;
import com.avalon.Avalon_Inventory.application.dto.PageDto.PageDTO;
import com.avalon.Avalon_Inventory.application.service.InventoryEntryService;
import com.avalon.Avalon_Inventory.application.service.InventoryExitService;
import com.avalon.Avalon_Inventory.domain.model.InventoryEntryExitStatus;
import com.avalon.Avalon_Inventory.infrastructure.security.CustomUserDetails;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private InventoryEntryService inventoryEntryService;

    @Autowired
    private InventoryExitService inventoryExitService;

    @GetMapping("/entry")
    @PreAuthorize("hasAuthority('READ')")
    public PageDTO<InventoryEntryResponseDTO> getAllEntries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filter,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<InventoryEntryResponseDTO> inventoryEntryPage;
        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {

            inventoryEntryPage = inventoryEntryService.getAllEntries(page, size);
        } else {
            Long userId = userDetails.getId();
            inventoryEntryPage = inventoryEntryService.getAllEntriesId(userId, page, size);
        }
        return new PageDTO<>(
                inventoryEntryPage.getContent(),
                inventoryEntryPage.getNumber(),
                inventoryEntryPage.getSize(),
                inventoryEntryPage.getTotalElements(),
                inventoryEntryPage.getTotalPages(),
                inventoryEntryPage.getNumberOfElements());
    }

    // Obtener una entrada de inventario por ID
    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/entry/{id}")
    public ResponseEntity<InventoryEntryResponseDTO> getEntryById(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {

            return inventoryEntryService.getEntryById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        Long userId = userDetails.getId();

        return inventoryEntryService.getEntryByIdAndUser(userId, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @PostMapping("/entry")
    @PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<InventoryEntryResponseDTO> registerEntry(
            @Valid @RequestBody InventoryEntryRequestDTO entryDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        InventoryEntryResponseDTO inventoryEntryRegister = inventoryEntryService.registerInventoryEntry(userId,
                entryDTO);
        return ResponseEntity.ok(inventoryEntryRegister);
    }

    // Actualizar una entrada de inventario
    @PutMapping("/entry/{id}")
    @PreAuthorize("hasAuthority('UPDATE')")
    public ResponseEntity<InventoryEntryResponseDTO> updateEntry(@PathVariable Long id,
            @Valid @RequestBody InventoryEntryRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        InventoryEntryResponseDTO updatedEntry = inventoryEntryService.updateEntry(userId, isAdmin, id, requestDTO);
        return ResponseEntity.ok(updatedEntry);
    }

    @PutMapping("/entry/{id}/estado")
    @PreAuthorize("hasAuthority('UPDATE')")
    public ResponseEntity<String> actualizarEstadoEntry(@PathVariable Long id,
            @RequestBody InventoryEntryExitStatus nuevoEstado,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getId();
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            inventoryEntryService.actualizarEstadoEntry(userId, isAdmin, id, nuevoEstado);

            return ResponseEntity.ok("Estado de la entrada se actualizado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el estado de la venta");
        }
    }
    /*
     * @PutMapping("/entry/{saleId}/estado")
     * public ResponseEntity<String> actualizarEstadoVenta(@PathVariable Long
     * saleId,
     * 
     * @RequestBody StatusSale nuevoEstado) {
     * try {
     * saleService.actualizarEstadoVenta(saleId, nuevoEstado);
     * return ResponseEntity.ok("Estado de la venta actualizado exitosamente");
     * } catch (RuntimeException e) {
     * return ResponseEntity.status(404).body(e.getMessage());
     * } catch (Exception e) {
     * return
     * ResponseEntity.status(500).body("Error al actualizar el estado de la venta");
     * }
     * }
     */

    // Eliminar una entrada de inventario
    /*
     * @DeleteMapping("/entry/{id}")
     * public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
     * inventoryEntryService.deleteEntry(id);
     * return ResponseEntity.noContent().build();
     * }
     */

    @GetMapping("/exit")
    @PreAuthorize("hasAuthority('READ')")
    public PageDTO<InventoryExitResponseDTO> getAllExits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filter,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<InventoryExitResponseDTO> exitPage;
        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {

            exitPage = inventoryExitService.getAllExits(page, size);
        } else {
            Long userId = userDetails.getId();
            exitPage = inventoryExitService.getAllExitsId(userId, page, size);
        }
        return new PageDTO<>(
                exitPage.getContent(),
                exitPage.getNumber(),
                exitPage.getSize(),
                exitPage.getTotalElements(),
                exitPage.getTotalPages(),
                exitPage.getNumberOfElements());

    }

    // Obtener una entrada de inventario por ID
    @GetMapping("/exit/{id}")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<InventoryExitResponseDTO> getExitById(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {

            return inventoryExitService.getExitById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        Long userId = userDetails.getId();

        return inventoryExitService.getExitByIdAndUser(userId, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/exit")
    @PreAuthorize("hasAuthority('CREATE')")
    public ResponseEntity<InventoryExitResponseDTO> registerExit(@Valid @RequestBody InventoryExitRequestDTO exitDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();

        InventoryExitResponseDTO inventoryExitResponse = inventoryExitService.registerInventoryEntry(userId, exitDTO);
        return ResponseEntity.ok(inventoryExitResponse);
    }

    // Actualizar una entrada de inventario
    @PutMapping("/exit/{id}")
    @PreAuthorize("hasAuthority('UPDATE')")
    public ResponseEntity<InventoryExitResponseDTO> updateExit(@PathVariable Long id,
            @Valid @RequestBody InventoryExitRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        InventoryExitResponseDTO updatedEntry = inventoryExitService.updateExit(userId, isAdmin, id, requestDTO);
        return ResponseEntity.ok(updatedEntry);
    }

    @PutMapping("/exit/{id}/estado")
    @PreAuthorize("hasAuthority('UPDATE')")
    public ResponseEntity<String> actualizarEstadoExit(@PathVariable Long id,
            @RequestBody InventoryEntryExitStatus nuevoEstado,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getId();
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            inventoryExitService.actualizarEstadoExit(userId, isAdmin, id, nuevoEstado);

            return ResponseEntity.ok("La salida se actualizado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el estado de la salida");
        }
    }
    // Eliminar una entrada de inventario
    /*
     * @DeleteMapping("/exit/{id}")
     * public ResponseEntity<Void> deleteExit(@PathVariable Long id) {
     * inventoryExitService.deleteExit(id);
     * return ResponseEntity.noContent().build();
     * }
     */
}
