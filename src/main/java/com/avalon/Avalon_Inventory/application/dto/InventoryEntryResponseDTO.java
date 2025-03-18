package com.avalon.Avalon_Inventory.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.avalon.Avalon_Inventory.domain.model.InventoryEntryExitStatus;

import lombok.Data;

@Data
public class InventoryEntryResponseDTO {
    private Long id;
    private Long userId;
    private Long modifiedByUserId;
    private List<ProductEntryDTO> products;
    private InventoryEntryExitStatus status;
    private String notes;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class ProductEntryDTO {
        private Long productId;
        private String name;
        private String description;
        private Integer quantity;
        private BigDecimal unit_price;
        private BigDecimal subtotal; // Subtotal (cantidad * precioUnitario)
    }
}