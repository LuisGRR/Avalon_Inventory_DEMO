package com.avalon.Avalon_Inventory.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory_exits_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryExitProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exit_id", nullable = false)
    private InventoryExit exit;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unit_price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal; // Subtotal (cantidad * precioUnitario)
}
