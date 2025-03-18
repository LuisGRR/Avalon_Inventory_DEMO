package com.avalon.Avalon_Inventory.domain.model.venta;

import java.math.BigDecimal;

import com.avalon.Avalon_Inventory.domain.model.Product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único del detalle

    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale; // Venta asociada

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Producto vendido

    @Column(name = "quantity", nullable = false )
    private Integer quantity; // Cantidad del producto vendido

    @Column(name = "unit_price", nullable = false )
    private BigDecimal unitPrice; // Precio por unidad del producto

    @Column(name = "subtotal", nullable = false )
    private BigDecimal subtotal; // Subtotal (cantidad * precioUnitario)
}
