package com.avalon.Avalon_Inventory.domain.repository.venta;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avalon.Avalon_Inventory.domain.model.venta.SalesProducts;

public interface SalesProductsRepository extends JpaRepository<SalesProducts,Long>{
     @Query("SELECT sp.product, SUM(sp.quantity) as totalVendido " +
           "FROM SalesProducts sp " +
           "GROUP BY sp.product " +
           "ORDER BY totalVendido ASC")
    List<Object[]> findLeastSoldProducts(Pageable pageable);
}
