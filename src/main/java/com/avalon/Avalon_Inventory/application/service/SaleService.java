package com.avalon.Avalon_Inventory.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.avalon.Avalon_Inventory.application.dto.venta.SaleRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.venta.SaleResponseDTO;
import com.avalon.Avalon_Inventory.domain.mapper.SaleMapper;
import com.avalon.Avalon_Inventory.domain.model.Product;
import com.avalon.Avalon_Inventory.domain.model.ProductUtils;
import com.avalon.Avalon_Inventory.domain.model.User;
import com.avalon.Avalon_Inventory.domain.model.venta.Customer;
import com.avalon.Avalon_Inventory.domain.model.venta.Sale;
import com.avalon.Avalon_Inventory.domain.model.venta.SalesProducts;
import com.avalon.Avalon_Inventory.domain.model.venta.StatusSale;
import com.avalon.Avalon_Inventory.domain.repository.ProductRepository;
import com.avalon.Avalon_Inventory.domain.repository.UserRepository;
import com.avalon.Avalon_Inventory.domain.repository.venta.CustomerRepository;
import com.avalon.Avalon_Inventory.domain.repository.venta.SaleRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleService {
    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleMapper saleMapper;

    public Page<SaleResponseDTO> getAllSales(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Sale> salePage = saleRepository.findAll(pageable);

        List<SaleResponseDTO> saleResponseDTO = salePage.getContent().stream()
                .map(saleMapper::toResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(saleResponseDTO, pageable, salePage.getTotalElements());
    }

    public Page<SaleResponseDTO> getAllSalesId(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Sale> salePage = saleRepository.findByUserId(id, pageable);

        List<SaleResponseDTO> saleResponseDTO = salePage.getContent().stream()
                .map(saleMapper::toResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(saleResponseDTO, pageable, salePage.getTotalElements());
    }

    public Optional<SaleResponseDTO> getSaleById(Long id) {
        return saleRepository.findById(id).map(saleMapper::toResponseDTO);
    }

    public Optional<SaleResponseDTO> getSaleByIdAndUser(Long userId, Long id) {
        return saleRepository.findByIdAndUserId(id, userId).map(saleMapper::toResponseDTO);
    }

    @Transactional
    public SaleResponseDTO registerSale(Long userId, SaleRequestDTO saleRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not fund"));

        Customer customer = null;
        if (saleRequestDTO.getCustomerId() != null) {
            customer = customerRepository.findById(saleRequestDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException(
                            "Cliente no encontrado con ID: " + saleRequestDTO.getCustomerId()));
        }

        AtomicReference<BigDecimal> totalSale = new AtomicReference<>(BigDecimal.ZERO);

        Sale sale = new Sale();
        sale.setUser(user);
        sale.setCustomer(customer);
        sale.setStatusSale(saleRequestDTO.getStatusSale());
        sale.setDateOfSale(LocalDateTime.now());
        sale.setUpdatedAt(LocalDateTime.now());

        // Crear los detalles de la venta (salesProducts)
        List<SalesProducts> salesProductsList = saleRequestDTO.getSalesProducts().stream()
                .map(salesProductsDTO -> {
                    Product product = productRepository.findById(salesProductsDTO.getProductId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Producto no encontrado con ID: " + salesProductsDTO.getProductId()));

                    if (salesProductsDTO.getQuantity() <= 0) {
                        throw new IllegalArgumentException("La cantidad del producto debe ser mayor a cero.");
                    }

                    if (sale.getStatusSale() == StatusSale.COMPLETADA) {
                        // Validar cantidad de productos en la venta
                        if (product.getQuantity() <= 0) {
                            throw new IllegalStateException("La no se cuenta con suficiente stock.");
                        }
                        product.setQuantity(product.getQuantity() - salesProductsDTO.getQuantity());
                        productRepository.save(product);
                    }

                    SalesProducts salesProducts = new SalesProducts();
                    salesProducts.setSale(sale);
                    salesProducts.setProduct(product);
                    salesProducts.setQuantity(salesProductsDTO.getQuantity());
                    salesProducts.setUnitPrice(product.getPrice());
                    salesProducts
                            .setSubtotal(ProductUtils.priceProductsSubtotal(salesProductsDTO.getQuantity(),
                                    product.getPrice()));

                    totalSale.getAndUpdate(currentTotal -> currentTotal.add(salesProducts.getSubtotal()));
                    return salesProducts;
                }).collect(Collectors.toList());

        sale.setSalesProducts(salesProductsList);
        sale.setTotal(totalSale.get()); // Obtener el valor final de `totalSale`

        saleRepository.save(sale);

        return saleMapper.toResponseDTO(sale);

    }

    @Transactional
    public SaleResponseDTO modificarVenta(Long userId, boolean isAdmin, Long saleId, SaleRequestDTO saleRequestDTO) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + saleId));

        if (sale.getStatusSale() != StatusSale.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden modificar ventas en estado PENDIENTE");
        }

        // Actualizar cliente si está presente
        if (saleRequestDTO.getCustomerId() != null) {
            Customer customer = customerRepository.findById(saleRequestDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException(
                            "Cliente no encontrado con ID: " + saleRequestDTO.getCustomerId()));
            sale.setCustomer(customer);
        }

        BigDecimal totalSale = BigDecimal.ZERO;



        if (sale.getUser().getId() != userId && !isAdmin) {
            throw new RuntimeException("El usuario no puede modificar la venta");
        }

        // Actualizar total y estado
        sale.setModifiedByUserId(userId);
        sale.setStatusSale(saleRequestDTO.getStatusSale());
        sale.setUpdatedAt(LocalDateTime.now());

        // Crear nuevos detalles de la venta y ajustar inventario
        for (SaleRequestDTO.SalesProductsDTO salesProductsDTO : saleRequestDTO.getSalesProducts()) {
            Product product = productRepository.findById(salesProductsDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            Optional<SalesProducts> existingSalesProductOpt = sale.getSalesProducts().stream()
                    .filter(sp -> sp.getProduct().getId().equals(salesProductsDTO.getProductId()))
                    .findFirst();

            if (existingSalesProductOpt.isPresent()) {

                // Actualizar producto existente
                SalesProducts existingSalesProduct = existingSalesProductOpt.get();

                existingSalesProduct.setQuantity(salesProductsDTO.getQuantity());
                existingSalesProduct.setUnitPrice(product.getPrice());
                existingSalesProduct
                        .setSubtotal(
                                ProductUtils.priceProductsSubtotal(salesProductsDTO.getQuantity(), product.getPrice()));

                // Ajustar inventario
                if (sale.getStatusSale() == StatusSale.COMPLETADA) {

                    inventoryService.actualizarInventario(product.getId(), existingSalesProduct.getQuantity(), false);

                }
                totalSale = totalSale.add(existingSalesProduct.getSubtotal());
            } else {
                // Crear nuevo detalle de producto
                SalesProducts newSalesProduct = new SalesProducts();
                newSalesProduct.setUnitPrice(product.getPrice());
                newSalesProduct.setQuantity(salesProductsDTO.getQuantity());
                newSalesProduct.setSubtotal(
                        ProductUtils.priceProductsSubtotal(salesProductsDTO.getQuantity(), product.getPrice()));

                // Ajustar inventario
                if (sale.getStatusSale() == StatusSale.COMPLETADA) {

                    inventoryService.actualizarInventario(product.getId(), newSalesProduct.getQuantity(), false);

                }
                totalSale = totalSale.add(newSalesProduct.getSubtotal());
                sale.getSalesProducts().add(newSalesProduct);
            }
        }

        sale.setTotal(totalSale);
        saleRepository.save(sale);
        return saleMapper.toResponseDTO(sale);
    }

    @Transactional
    public void actualizarEstadoVenta(Long userId, boolean isAdmin, Long saleId, StatusSale nuevoEstado) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + saleId));

        if (sale.getUser().getId() != userId && !isAdmin) {
            throw new RuntimeException("El usuario no puede modificar la venta");
        }

        if (sale.getStatusSale() == StatusSale.CANCELADA || sale.getStatusSale() == StatusSale.REEMBOLSADA) {
            throw new IllegalStateException("No se puede cambiar el estado de una venta cancelada o reembolsada");
        }
        sale.setModifiedByUserId(userId);

        // Ajustar las cantidades de productos según el nuevo estado
        ajustarInventario(sale, sale.getStatusSale(), nuevoEstado);

        sale.setStatusSale(nuevoEstado);
        saleRepository.save(sale);
    }

    private void ajustarInventario(Sale sale, StatusSale estadoAnterior, StatusSale nuevoEstado) {
        List<SalesProducts> salesProducts = sale.getSalesProducts();

        for (SalesProducts sp : salesProducts) {
            Product product = sp.getProduct();

            switch (nuevoEstado) {
                case COMPLETADA:
                    if (estadoAnterior == StatusSale.PENDIENTE || estadoAnterior == StatusSale.EN_PROCESO) {

                        inventoryService.actualizarInventario(product.getId(), sp.getQuantity(), false);
                    }
                    break;

                case CANCELADA:
                case REEMBOLSADA:
                    if (estadoAnterior == StatusSale.COMPLETADA || estadoAnterior == StatusSale.EN_PROCESO) {

                        inventoryService.actualizarInventario(product.getId(), sp.getQuantity(), true);
                    }
                    break;
                default:
                    break;

                // Otros casos según sea necesario
            }
            // productRepository.save(product);
        }
    }

}
