package com.avalon.Avalon_Inventory.domain.model.venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.avalon.Avalon_Inventory.domain.model.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único de la venta

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false,updatable = false)
    private User user; // Usuario que registró la venta

    @Column(name = "modified_by_user_id",nullable = true) // Nuevo campo para el ID del usuario que modifica
    private Long modifiedByUserId; // ID del usuario que realizó la última modificación

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = true)
    private Customer customer; // Cliente que realiza la compra (opcional)

    @OneToMany(mappedBy = "sale", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SalesProducts> salesProducts; // Lista de detalles de la venta

    @Column(name = "date_of_sale", updatable = false)
    private LocalDateTime dateOfSale; // Fecha de la venta

    @Column(name = "total",nullable = false)
    private BigDecimal total;

    @Column(name = "status", nullable = false)
    private StatusSale statusSale; // Estado de la venta

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
