package com.avalon.Avalon_Inventory.domain.model.venta;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único del cliente

    @Column(name = "name", nullable = false)
    private String name; // Nombre del cliente

    @Column(nullable = true, unique = true)
    private String email; // Correo electrónico del cliente (opcional)

    @Column(name = "phone")
    private String phone; // Teléfono del cliente (opcional)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
