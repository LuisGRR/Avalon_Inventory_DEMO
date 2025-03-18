package com.avalon.Avalon_Inventory.application.dto.reportDto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardDTO {

    private List<ProductDash> producto_bajo_stock; //
    private List<ProductDash> producto_mas_vendido;//
    private List<SaleReportDTO> sales_dashs;
    private Integer total_inventory;//
    private Integer num_prod_stock_bajo;//
    private BigDecimal total_sale;//

   
}
