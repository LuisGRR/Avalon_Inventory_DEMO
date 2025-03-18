package com.avalon.Avalon_Inventory.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProviderRequestDTO {

    @NotNull(message = "El nombre del proveedor no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre del proveedor debe tener entre 3 y 100 caracteres")
    private String name;

    @NotNull(message = "El correo no puede ser nulo")
    @Email(message = "Debe proporcionar un correo electrónico válido")
    private String contactEmail;

    @NotBlank(message = "El número de teléfono no puede estar vacío")
    @Pattern(regexp = "^\\d{10}$", message = "El número de teléfono debe tener exactamente 10 dígitos")
    private String phoneNumber;

    @NotNull(message = "El valor no puede ser nulo")
    private Boolean isActive;

    private String notes;
}
