package com.avalon.Avalon_Inventory.domain.repository.venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import com.avalon.Avalon_Inventory.domain.model.venta.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @NonNull
    @Override
    Page<Sale> findAll(@NonNull Pageable pageable);

    @NonNull
    @Override
    List<Sale> findAll();

    Page<Sale> findByUserId(Long userId, Pageable pageable);

    Optional<Sale> findByIdAndUserId(Long saleId, Long userId);

    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE s.statusSale = StatusSale.COMPLETADA and s.user.id = :id AND s.dateOfSale BETWEEN :startDate AND :endDate")
    BigDecimal totalSalesDayUserId(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate, @Param("id") Long idUser);

    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE s.statusSale = StatusSale.COMPLETADA AND s.dateOfSale BETWEEN :startDate AND :endDate")
    BigDecimal totalSalesDayAll(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Sale> findByDateOfSaleBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT s FROM Sale s JOIN FETCH s.salesProducts sp WHERE s.dateOfSale BETWEEN :startDate AND :endDate")
    List<Sale> findSalesWithProductsBetweenDates(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
