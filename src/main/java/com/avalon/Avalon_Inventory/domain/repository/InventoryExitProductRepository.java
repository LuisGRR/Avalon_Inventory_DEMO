package com.avalon.Avalon_Inventory.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.avalon.Avalon_Inventory.domain.model.InventoryExitProduct;

public interface InventoryExitProductRepository extends JpaRepository<InventoryExitProduct, Long>{
    
}
