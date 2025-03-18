package com.avalon.Avalon_Inventory.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.avalon.Avalon_Inventory.application.dto.reportDto.DashBoardDTO;
import com.avalon.Avalon_Inventory.application.dto.reportDto.ProductDash;
import com.avalon.Avalon_Inventory.application.dto.reportDto.SaleReportDTO;
import com.avalon.Avalon_Inventory.application.dto.reportDto.StockResumenDto;
import com.avalon.Avalon_Inventory.domain.model.Product;
import com.avalon.Avalon_Inventory.domain.model.venta.Sale;
import com.avalon.Avalon_Inventory.domain.model.venta.SalesProducts;
import com.avalon.Avalon_Inventory.domain.repository.InventoryEntryRepository;
import com.avalon.Avalon_Inventory.domain.repository.ProductRepository;
import com.avalon.Avalon_Inventory.domain.repository.venta.SaleRepository;
import com.avalon.Avalon_Inventory.domain.repository.venta.SalesProductsRepository;

@Service
public class ReportService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SaleRepository saleRepository;

    @Autowired
    InventoryEntryRepository inventoryEntryRepository;

    @Autowired
    private SalesProductsRepository salesProductsRepository;

    public DashBoardDTO getDashBoardData(Long userId, boolean isAdmin) {
        LocalDate fechaActual = LocalDate.now();
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Crear startDate (inicio del día actual)
        LocalDateTime startDate = currentDateTime.toLocalDate().atStartOfDay();

        // Crear endDate (final del día actual)
        LocalDateTime endDate = currentDateTime.toLocalDate().atTime(LocalTime.MAX);

        DashBoardDTO dashBoardDTO = new DashBoardDTO();

        List<ProductDash> prodDashBajoStock = obtenerProductosBajoStock().stream().map(
                prod -> new ProductDash(prod.getId(), prod.getName(), prod.getQuantity(),
                        prod.getUmbralMinimo(), prod.getStockOptimo()))
                .collect(Collectors.toList());

        List<ProductDash> prodDashMasVendido = obtenerProductosMasVendidos(fechaActual, fechaActual)
                .stream().map(prod -> new ProductDash(prod.getId(), prod.getName(), prod.getQuantity(),
                        prod.getUmbralMinimo(), prod.getStockOptimo()))
                .collect(Collectors.toList());

        if (isAdmin) {
            dashBoardDTO.setTotal_sale(saleRepository.totalSalesDayAll(startDate, endDate));
        } else {
            dashBoardDTO.setTotal_sale(saleRepository.totalSalesDayUserId(startDate, endDate, userId));
        }

        dashBoardDTO.setSales_dashs(obtenerVentasPorFecha(fechaActual, fechaActual));
        dashBoardDTO.setTotal_inventory(productRepository.totalQuantity());
        dashBoardDTO.setNum_prod_stock_bajo(prodDashBajoStock.size());
        dashBoardDTO.setProducto_bajo_stock(prodDashBajoStock);
        dashBoardDTO.setProducto_mas_vendido(prodDashMasVendido);

        return dashBoardDTO;
    }

    // Reporte de productos con stock bajo
    public List<Product> obtenerProductosBajoStock() {
        return productRepository.findAll().stream()
                .filter(product -> product.getQuantity() <= product.getUmbralMinimo())
                .collect(Collectors.toList());
    }

    // Reporte de productos con stock optimo
    public List<Product> obtenerProductosOptimoStock() {
        return productRepository.findAll().stream()
                .filter(product -> product.getQuantity() >= product.getStockOptimo())
                .collect(Collectors.toList());
    }

    // Reporte de ventas por fecha
    public List<SaleReportDTO> obtenerVentasPorFecha(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<Sale> sales = saleRepository.findByDateOfSaleBetween(startDateTime, endDateTime);

        return sales.stream()
                .map(sale -> new SaleReportDTO(
                        sale.getId(),
                        sale.getUser().getUsername(), // Obteniendo el nombre del usuario
                        sale.getCustomer() != null ? sale.getCustomer().getName() : "No asignado", // Cliente opcional
                        sale.getTotal(),
                        sale.getStatusSale(),
                        sale.getDateOfSale(),
                        sale.getSalesProducts() != null ? sale.getSalesProducts().size() : 0 // Contar productos
                ))
                .collect(Collectors.toList());
    }

    public List<Product> getProductsWithoutMovement(LocalDateTime startDate) {
        return productRepository.findProductsWithoutMovement(startDate);
    }

    // Reporte de productos más vendidos
    public List<Product> obtenerProductosMasVendidos(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return saleRepository.findSalesWithProductsBetweenDates(startDateTime, endDateTime)
                .stream()
                .flatMap(sale -> sale.getSalesProducts().stream())
                .collect(Collectors.groupingBy(
                        salesProduct -> salesProduct.getProduct().getId(),
                        Collectors.summingInt(SalesProducts::getQuantity)))
                .entrySet().stream()
                .map(entry -> {
                    Product product = productRepository.findById(entry.getKey())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                    product.setQuantity(entry.getValue());
                    return product;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getLeastSoldProducts(int limit) {
        List<Object[]> results = salesProductsRepository.findLeastSoldProducts(PageRequest.of(0, limit));
        return results.stream().map(result -> {
            Product product = (Product) result[0];
            Long totalSold = (Long) result[1];

            Map<String, Object> map = new HashMap<>();
            map.put("product", product.getName());
            map.put("totalSold", totalSold);
            return map;
        }).collect(Collectors.toList());
    }

    // Reporte: Productos con Mayor Variación de Stock
    public List<StockResumenDto> getStockVariation(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = productRepository.findStockDetails(startDate, endDate);
        return results.stream().map(result -> {
            Long id = (Long) result[0];
            String name = (String) result[1];
            Long stockInicial = (Long) result[2];
            Long totalEntradas = (Long) result[3]; 
            Long ventas = (Long) result[4]; 
            Long totalSalidas = (Long) result[5]; 
            Long variacionStock = (Long) result[6];

            return new StockResumenDto(id, name, stockInicial, totalEntradas, ventas, totalSalidas, variacionStock);
        }).collect(Collectors.toList());
    }

    // Reporte: Productos Más Rentables
    public List<Map<String, Object>> getMostProfitableProducts() {
        List<Object[]> results = productRepository.findMostProfitableProducts();
        return results.stream().map(obj -> Map.of(
                "product", ((Product) obj[0]).getName(),
                "profit", obj[1])).collect(Collectors.toList());
    }

    // Reporte: Entradas de Inventario por Período
    public Integer getTotalInventoryEntries(LocalDateTime startDate, LocalDateTime endDate) {
        return inventoryEntryRepository.getTotalInventoryEntries(startDate, endDate);
    }

    // Reporte: Productos con Mayor Entrada de Inventario
    public List<Map<String, Object>> getTopInventoryEntries(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = inventoryEntryRepository.findTopInventoryEntries(startDate, endDate);
        return results.stream().map(obj -> Map.of(
                "product", ((Product) obj[0]).getName(),
                "totalEntries", obj[1])).collect(Collectors.toList());
    }
}
