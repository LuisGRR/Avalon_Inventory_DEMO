package com.avalon.Avalon_Inventory.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.avalon.Avalon_Inventory.domain.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
 @NonNull
        @Override
        Page<Category> findAll(@NonNull Pageable pageable);

        @NonNull
        @Override
        List<Category> findAll(); 
}
