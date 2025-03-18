package com.avalon.Avalon_Inventory.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inventory_exits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryExit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false,updatable = false)
    private User user; // Asegúrate de tener una entidad 
    
    @Column(name = "modified_by_user_id",nullable = true) // Nuevo campo para el ID del usuario que modifica
    private Long modifiedByUserId; // ID del usuario que realizó la última modificación

    @OneToMany(mappedBy = "exit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InventoryExitProduct> products; // Relación con InventoryEntryProduct
   
    @Column(name = "category", nullable = false)
    private InventoryExitCategory category; // Categoría de la salida
    
    @Column(name = "status", nullable = false)
    private InventoryEntryExitStatus status; // Puedes usar un Enum si prefieres

    @Column(name = "total",nullable = false)
    private BigDecimal total;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
