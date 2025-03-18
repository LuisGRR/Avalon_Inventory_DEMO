package com.avalon.Avalon_Inventory.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inventory_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false,updatable = false)
    private User user; // Asegúrate de tener una entidad User

    @Column(name = "modified_by_user_id",nullable = true) // Nuevo campo para el ID del usuario que modifica
    private Long modifiedByUserId; // ID del usuario que realizó la última modificación

    @OneToMany(mappedBy = "entry", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InventoryEntryProduct> products; // Relación con InventoryEntryProduct

    // Nueva propiedad para el estatus
    @Column(name = "status", nullable = false)
    private InventoryEntryExitStatus status;

    @Column(name = "notes")
    private String notes;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
