package com.avalon.Avalon_Inventory.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequestDTO {
    @NotNull(message = "El nombre no puede ser nulo")
    private String name; 

}
