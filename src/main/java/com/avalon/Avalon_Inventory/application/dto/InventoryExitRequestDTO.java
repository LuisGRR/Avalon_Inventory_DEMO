package com.avalon.Avalon_Inventory.application.dto;

import lombok.Data;

import java.util.List;

import com.avalon.Avalon_Inventory.domain.model.InventoryEntryExitStatus;
import com.avalon.Avalon_Inventory.domain.model.InventoryExitCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class InventoryExitRequestDTO {
    // private Long userId;
    @NotNull(message = "La lista de productos no puede ser nula")
    @Size(min = 1, message = "Debe haber al menos un producto en la salida")
    private List<ProductExitDTO> products;

    @NotNull(message = "La categoria de la salida no puede ser nulo")
    private InventoryExitCategory category;

    @NotNull(message = "El estado de la salida no puede ser nulo")
    private InventoryEntryExitStatus status;

    private String notes;

    @Data
    public static class ProductExitDTO {
        @NotNull(message = "El ID del producto no puede ser nulo")
        private Long productId;

        @NotNull(message = "La cantidad no puede ser nula")
        @Min(value = 1, message = "La cantidad mínima permitida es 1")
        private Integer quantity;
    }
}
