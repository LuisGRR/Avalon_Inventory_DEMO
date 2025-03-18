package com.avalon.Avalon_Inventory.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import com.avalon.Avalon_Inventory.domain.model.InventoryEntryExitStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class InventoryEntryRequestDTO {
    // private Long userId;
    @NotNull(message = "La lista de productos no puede ser nula")
    @Size(min = 1, message = "Debe haber al menos un producto en la entrada")
    private List<ProductEntryDTO> products;

    @NotNull(message = "El estado de la entrada no puede ser nulo")
    private InventoryEntryExitStatus status;

    private String notes;

    @Data
    public static class ProductEntryDTO {
        @NotNull(message = "El ID del producto no puede ser nulo")
        private Long productId;

        @NotNull(message = "La cantidad no puede ser nula")
        @Min(value = 1, message = "La cantidad mínima permitida es 1")
        private Integer quantity;

        @NotNull(message = "El precio unitario no puede ser nulo")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor que 0")
        @Digits(integer = 10, fraction = 2, message = "El precio unitario debe tener como máximo 10 dígitos enteros y 2 decimales")
        private BigDecimal unit_price;
    }
}
