package com.avalon.Avalon_Inventory.application.dto.reportDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.avalon.Avalon_Inventory.domain.model.venta.StatusSale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleReportDTO {
    private Long id;
    private String username;
    private String customerName;
    private BigDecimal total;
    private StatusSale status;
    private LocalDateTime dateOfSale;
    private int productCount;
}
