package com.avalon.Avalon_Inventory.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import com.avalon.Avalon_Inventory.domain.model.Provider;
import com.avalon.Avalon_Inventory.infrastructure.projections.ProviderProjection;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
        @NonNull
        @Override
        Page<Provider> findAll(@NonNull Pageable pageable);

        @NonNull
        @Override
        List<Provider> findAll();

        @Query("SELECT r.id AS id, r.name AS name FROM Provider r WHERE r.isActive = true")
        List<ProviderProjection> findAllProjectedBy();
}
