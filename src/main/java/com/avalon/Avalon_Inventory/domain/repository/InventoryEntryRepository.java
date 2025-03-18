package com.avalon.Avalon_Inventory.domain.repository;

import com.avalon.Avalon_Inventory.domain.model.InventoryEntry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface InventoryEntryRepository extends JpaRepository<InventoryEntry, Long> {

        @NonNull
        @Override
        Page<InventoryEntry> findAll(@NonNull Pageable pageable);

        @NonNull
        @Override
        List<InventoryEntry> findAll();

        Page<InventoryEntry> findAllByUserId(Long userId, Pageable pageable);

        Optional<InventoryEntry> findByIdAndUserId(Long saleId, Long userId);

        // Reporte: Entradas de Inventario por Período
        @Query("SELECT SUM(iep.quantity) FROM InventoryEntryProduct iep " +
                        "WHERE iep.entry.createdAt BETWEEN :startDate AND :endDate")
        Integer getTotalInventoryEntries(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Reporte: Productos con Mayor Entrada de Inventario
        @Query("SELECT iep.product, SUM(iep.quantity) " +
                        "FROM InventoryEntryProduct iep " +
                        "WHERE iep.entry.createdAt BETWEEN :startDate AND :endDate " +
                        "GROUP BY iep.product ORDER BY SUM(iep.quantity) DESC")
        List<Object[]> findTopInventoryEntries(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
