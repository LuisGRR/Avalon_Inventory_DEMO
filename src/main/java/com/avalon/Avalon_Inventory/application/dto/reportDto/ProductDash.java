package com.avalon.Avalon_Inventory.application.dto.reportDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDash {
    private Long id;
    private String name;
    private Integer quantity;
    private Integer umbralMinimo;
    private Integer stockOptimo;
}