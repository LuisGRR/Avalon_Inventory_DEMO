package com.avalon.Avalon_Inventory.application.dto.reportDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockResumenDto {
    private Long id;
    private String name;
    private Long stockInicial;
    private Long totalEntradas;
    private Long ventas;
    private Long totalSalidas;
    private Long variacionStock;
}
