package com.avalon.Avalon_Inventory.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Integer quantity;
    private Integer umbralMinimo;
    private Integer stockOptimo;
    private LocalDate fechaCaducidad;
    private BigDecimal price;
    private String barCode;
    
    private Long category_id;

    private Long provider_id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
