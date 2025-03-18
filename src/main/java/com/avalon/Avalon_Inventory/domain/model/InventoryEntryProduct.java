package com.avalon.Avalon_Inventory.domain.model;


import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory_entries_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEntryProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entry_id", nullable = false)
    private InventoryEntry entry;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false )
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal unit_price;

    @Column(name = "subtotal", nullable = false )
    private BigDecimal subtotal; // Subtotal (cantidad * precioUnitario)

}
