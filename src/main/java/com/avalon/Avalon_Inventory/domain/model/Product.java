package com.avalon.Avalon_Inventory.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer umbralMinimo;

    @Column(nullable = false)
    private Integer stockOptimo;

    @Column(name = "fecha_caducidad")
    private LocalDate fechaCaducidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "bar_code", nullable = true, unique = true)
    private String barCode;

    @ManyToOne(optional = true) // Categoría opcional
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @ManyToOne(optional = true)
    @JoinColumn(name = "provider_id", nullable = true)
    private Provider provider;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}