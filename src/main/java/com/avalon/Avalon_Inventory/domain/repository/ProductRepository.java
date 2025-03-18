package com.avalon.Avalon_Inventory.domain.repository;

import com.avalon.Avalon_Inventory.domain.model.Product;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @NonNull
    @Override
    Page<Product> findAll(@NonNull Pageable pageable);

    @NonNull
    @Override
    List<Product> findAll(); // Método existente para obtener todos los productos

    @Query("SELECT p FROM Product p ORDER BY " +
            "CASE WHEN p.quantity < p.umbralMinimo THEN 1 " + // Bajo stock
            "     WHEN p.quantity <= p.stockOptimo THEN 2 " + // Stock óptimo
            "     ELSE 3 END, " +
            "p.quantity ASC")
    Page<Product> findAllOrderedByStock(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    Page<Product> searchByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.id NOT IN (SELECT DISTINCT sp.product.id FROM SalesProducts sp WHERE sp.sale.dateOfSale >= :startDate) "
            +
            "AND p.id NOT IN (SELECT DISTINCT iep.product.id FROM InventoryEntryProduct iep WHERE iep.entry.createdAt >= :startDate)")
    List<Product> findProductsWithoutMovement(@Param("startDate") LocalDateTime startDate);

    // Reporte: Productos con Mayor Variación de Stock

    @Query("SELECT p, " +
            "(SELECT COALESCE(SUM(iep.quantity), 0) FROM InventoryEntryProduct iep WHERE iep.product = p) AS entradas,"
            + "(SELECT COALESCE(SUM(sp.quantity), 0) FROM SalesProducts sp WHERE sp.product=p)AS ventas FROM Product p")
    List<Object[]> findStockVariation();

@Query(value = "SELECT \n" + //
        "    p.id,\n" + //
        "    p.name,\n" + //
        "    COALESCE(p.quantity, 0) \n" + //
        "    + COALESCE(s.total_sales, 0) \n" + //
        "    + COALESCE(ex.total_exits, 0) \n" + //
        "    - COALESCE(en.total_entries, 0) AS stock_inicial,\n" + //
        "    COALESCE(en.total_entries, 0) AS total_entradas,\n" + //
        "    COALESCE(s.total_sales, 0) AS ventas,\n" + //
        "    COALESCE(ex.total_exits, 0) AS total_salidas,\n" + //
        "    COALESCE(ex.total_exits, 0) + COALESCE(s.total_sales, 0) - COALESCE(en.total_entries, 0) as variacion_stock\n" + //
        "FROM products p\n" + //
        "LEFT JOIN (\n" + //
        "    SELECT sp.product_id, SUM(sp.quantity) AS total_sales\n" + //
        "    FROM sales_products sp\n" + //
        "    JOIN sales s ON sp.sale_id = s.id\n" + //
        "    WHERE s.status = 1\n" + //
        "      AND s.date_of_sale BETWEEN :startDate AND :endDate\n" + //
        "    GROUP BY sp.product_id\n" + //
        ") s ON p.id = s.product_id\n" + //
        "LEFT JOIN (\n" + //
        "    SELECT iep.product_id, SUM(iep.quantity) AS total_entries\n" + //
        "    FROM inventory_entries_products iep\n" + //
        "    JOIN inventory_entries ie ON iep.entry_id = ie.id\n" + //
        "    WHERE ie.status = 1\n" + //
        "      AND ie.created_at BETWEEN :startDate AND :endDate\n" + //
        "    GROUP BY iep.product_id\n" + //
        ") en ON p.id = en.product_id\n" + //
        "LEFT JOIN (\n" + //
        "    SELECT iepx.product_id, SUM(iepx.quantity) AS total_exits\n" + //
        "    FROM inventory_exits_products iepx\n" + //
        "    JOIN inventory_exits iex ON iepx.exit_id = iex.id\n" + //
        "    WHERE iex.status = 1\n" + //
        "      AND iex.created_at BETWEEN :startDate AND :endDate\n" + //
        "    GROUP BY iepx.product_id\n" + //
        ") ex ON p.id = ex.product_id\n" + //
        " ORDER BY  COALESCE(ex.total_exits, 0) + COALESCE(s.total_sales, 0) - COALESCE(en.total_entries, 0) DESC;", nativeQuery = true)
    List<Object[]> findStockDetails(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Reporte: Productos Más Rentables
    @Query("SELECT sp.product, SUM(sp.subtotal) - (SUM(sp.quantity) * p.price) AS profit " +
            "FROM SalesProducts sp JOIN sp.product p " +
            "GROUP BY sp.product ORDER BY profit DESC")
    List<Object[]> findMostProfitableProducts();

    @Query("SELECT COALESCE(SUM(p.quantity),0) FROM Product p")
    Integer totalQuantity();
}
