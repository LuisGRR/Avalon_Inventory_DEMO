package com.avalon.Avalon_Inventory.application.dto.venta;

import java.util.List;

import com.avalon.Avalon_Inventory.domain.model.venta.StatusSale;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SaleRequestDTO {
    // private Long userId;

    private Long customerId;

    @NotNull(message = "El estado de la venta no puede ser nulo")
    private StatusSale statusSale;

    @NotNull(message = "La lista de productos no puede ser nula")
    @Size(min = 1, message = "Debe haber al menos un producto en la venta")
    private List<SalesProductsDTO> salesProducts;

    @Data
    public static class SalesProductsDTO {
        @NotNull(message = "El ID del producto no puede ser nulo")
        private Long productId;

        @NotNull(message = "La cantidad no puede ser nula")
        @Min(value = 1, message = "La cantidad mínima permitida es 1")
        private Integer quantity;
    }
}
