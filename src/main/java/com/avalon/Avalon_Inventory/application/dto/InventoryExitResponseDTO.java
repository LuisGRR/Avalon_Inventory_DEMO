package com.avalon.Avalon_Inventory.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.avalon.Avalon_Inventory.domain.model.InventoryEntryExitStatus;
import com.avalon.Avalon_Inventory.domain.model.InventoryExitCategory;

import lombok.Data;

@Data
public class InventoryExitResponseDTO {
    private Long id;
    private Long userId;
    private Long modifiedByUserId;
    private List<ProductEntryDTO> products;
    private InventoryEntryExitStatus status;
    private InventoryExitCategory category;
    private BigDecimal total;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class ProductEntryDTO {
        private Long productId;
        private String name;
        private String description;
        private Integer quantity;
        private BigDecimal unit_price;
        private BigDecimal subtotal;
    }
}
