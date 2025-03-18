package com.avalon.Avalon_Inventory.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.avalon.Avalon_Inventory.domain.model.InventoryExit;

public interface InventoryExitRepository extends JpaRepository<InventoryExit, Long> {
    @NonNull
    @Override
    Page<InventoryExit> findAll(@NonNull Pageable pageable);

    @NonNull
    @Override
    List<InventoryExit> findAll();

    Page<InventoryExit> findAllByUserId(Long userId, Pageable pageable);

    Optional<InventoryExit> findByIdAndUserId(Long saleId, Long userId);

}
