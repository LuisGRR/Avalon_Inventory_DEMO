package com.avalon.Avalon_Inventory.application.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProviderResponseDTO {
    private Long id;

    private String name;

    private String contactEmail;

    private String phoneNumber;

    private Boolean isActive;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
