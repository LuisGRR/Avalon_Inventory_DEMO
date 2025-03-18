package com.avalon.Avalon_Inventory.presentation.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avalon.Avalon_Inventory.application.dto.reportDto.DashBoardDTO;
import com.avalon.Avalon_Inventory.application.dto.reportDto.SaleReportDTO;
import com.avalon.Avalon_Inventory.application.dto.reportDto.StockResumenDto;
import com.avalon.Avalon_Inventory.application.service.ReportService;
import com.avalon.Avalon_Inventory.domain.model.Product;
import com.avalon.Avalon_Inventory.infrastructure.security.CustomUserDetails;

@RestController
@RequestMapping("/api/reportes")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/dash-board")
    public ResponseEntity<DashBoardDTO> getDashBoard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(reportService.getDashBoardData(userId,isAdmin));
    }

    @GetMapping("/productos-bajo-stock")
    public List<Product> getProductosBajoStock() {
        return reportService.obtenerProductosBajoStock();
    }

    // Reporte de ventas por fecha
    @GetMapping("/ventas")
    public List<SaleReportDTO> getVentasPorFecha(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return reportService.obtenerVentasPorFecha(startDate, endDate);
    }

    // Reporte de productos más vendidos
    @GetMapping("/productos-mas-vendidos")
    public List<Product> getProductosMasVendidos(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return reportService.obtenerProductosMasVendidos(startDate, endDate);
    }

    @GetMapping("/least-sold")
    public ResponseEntity<List<Map<String, Object>>> getLeastSoldProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getLeastSoldProducts(limit));
    }

    @GetMapping("/products-no-movement")
    public ResponseEntity<List<Product>> getProductsWithoutMovement(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {
        return ResponseEntity.ok(reportService.getProductsWithoutMovement(startDate));
    }

    // 📍 Reporte: Entradas de Inventario por Período
    @GetMapping("/inventory-entries")
    public ResponseEntity<Integer> getTotalInventoryEntries(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return ResponseEntity.ok(reportService.getTotalInventoryEntries(startDate, endDate));
    }

    // 📍 Reporte: Productos con Mayor Entrada de Inventario
    @GetMapping("/top-inventory-entries")
    public ResponseEntity<List<Map<String, Object>>> getTopInventoryEntries(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return ResponseEntity.ok(reportService.getTopInventoryEntries(startDate, endDate));
    }

    // 📍 Reporte: Productos con Mayor Variación de Stock
    @GetMapping("/stock-variation")
    public ResponseEntity<List<StockResumenDto>> getStockVariation(@RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getStockVariation(startDate, endDate));
    }

    // 📍 Reporte: Productos Más Rentables
    @GetMapping("/most-profitable-products")
    public ResponseEntity<List<Map<String, Object>>> getMostProfitableProducts() {
        return ResponseEntity.ok(reportService.getMostProfitableProducts());
    }

}
