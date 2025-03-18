package com.avalon.Avalon_Inventory.domain.repository;

import com.avalon.Avalon_Inventory.domain.model.InventoryEntryProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryEntryProductRepository extends JpaRepository<InventoryEntryProduct, Long> {
}
