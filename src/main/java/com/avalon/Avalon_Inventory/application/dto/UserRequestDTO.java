package com.avalon.Avalon_Inventory.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotNull(message = "El nombre no puede ser nulo")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String username;

    @NotNull(message = "El correo no puede ser nulo")
    @Email(message = "Debe proporcionar un correo electrónico válido")
    private String email;

    @NotNull(message = "La contraseña no puede ser nulo")
    @Size(min = 8, max = 20, message = "La contraseña debe tener entre 8 y 20 caracteres")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "La contraseña debe contener al menos un dígito, una letra minúscula, una letra mayúscula y un carácter especial (@#$%^&+=)")
    private String password;

    @NotNull(message = "El id del rol no puede ser nulo")
    private Long role_id;
}