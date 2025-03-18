package com.avalon.Avalon_Inventory.application.dto.venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.avalon.Avalon_Inventory.domain.model.venta.StatusSale;

import lombok.Data;

@Data
public class SaleResponseDTO {
    private Long id;
    private Long userId;
    private Long modifiedByUserId; 
    private Long customerId;
    private LocalDateTime dateOfSale;
    private BigDecimal total;
    private StatusSale statusSale;
    private LocalDateTime updatedAt;
    private List<SalesProductsDTO> products;

    @Data
    public static class SalesProductsDTO {
        private Long productId;
        private String name;
        private String description;
        private Integer quantity;
        private BigDecimal unit_price;
        private BigDecimal subtotal;
    }
}
