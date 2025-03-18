package com.avalon.Avalon_Inventory.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.avalon.Avalon_Inventory.application.dto.InventoryExitRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.InventoryExitResponseDTO;
import com.avalon.Avalon_Inventory.domain.mapper.InventoryExitMapper;
import com.avalon.Avalon_Inventory.domain.model.InventoryEntryExitStatus;
import com.avalon.Avalon_Inventory.domain.model.InventoryExit;
import com.avalon.Avalon_Inventory.domain.model.InventoryExitProduct;
import com.avalon.Avalon_Inventory.domain.model.Product;
import com.avalon.Avalon_Inventory.domain.model.ProductUtils;
import com.avalon.Avalon_Inventory.domain.model.User;
import com.avalon.Avalon_Inventory.domain.repository.InventoryExitRepository;
import com.avalon.Avalon_Inventory.domain.repository.ProductRepository;
import com.avalon.Avalon_Inventory.domain.repository.UserRepository;

@Service
public class InventoryExitService {
    @Autowired
    private InventoryExitRepository inventoryExitRepository;

    //@Autowired
    //private InventoryExitProductRepository inventoryExitProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryExitMapper inventoryExitMapper;

    @Autowired
    private InventoryService inventoryService;

    public Page<InventoryExitResponseDTO> getAllExits(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<InventoryExit> exits = inventoryExitRepository.findAll(pageable);

        List<InventoryExitResponseDTO> exitResponseDTO = exits.getContent().stream()
                .map(inventoryExitMapper::toResponseDTO).collect(Collectors.toList());

        return new PageImpl<>(exitResponseDTO, pageable, exits.getTotalElements());
    }

    public Page<InventoryExitResponseDTO> getAllExitsId(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<InventoryExit> exits = inventoryExitRepository.findAllByUserId(id, pageable);

        List<InventoryExitResponseDTO> exitResponseDTO = exits.getContent().stream()
                .map(inventoryExitMapper::toResponseDTO).collect(Collectors.toList());

        return new PageImpl<>(exitResponseDTO, pageable, exits.getTotalElements());
    }

    public Optional<InventoryExitResponseDTO> getExitById(Long id) {
        return inventoryExitRepository.findById(id).map(inventoryExitMapper::toResponseDTO);
    }

    public Optional<InventoryExitResponseDTO> getExitByIdAndUser(Long userId, Long id) {
        return inventoryExitRepository.findByIdAndUserId(id, userId).map(inventoryExitMapper::toResponseDTO);
    }

    @Transactional
    public InventoryExitResponseDTO registerInventoryEntry(Long userId, InventoryExitRequestDTO exDTO) {
        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal totalExit = BigDecimal.ZERO;

        // Crear una nueva entrada de inventario
        InventoryExit inventoryExit = new InventoryExit();
        inventoryExit.setUser(user);
        inventoryExit.setCreatedAt(LocalDateTime.now());
        inventoryExit.setUpdatedAt(LocalDateTime.now());
        inventoryExit.setStatus(exDTO.getStatus());
        inventoryExit.setCategory(exDTO.getCategory());
        inventoryExit.setProducts(new ArrayList<>());

        // Procesar los productos en la entrada
        for (InventoryExitRequestDTO.ProductExitDTO productExit : exDTO.getProducts()) {
            Product product = productRepository.findById(productExit.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Crear una relación entre la entrada y el producto
            InventoryExitProduct exitProduct = new InventoryExitProduct();
            exitProduct.setExit(inventoryExit);
            exitProduct.setProduct(product);
            exitProduct.setQuantity(productExit.getQuantity());
            exitProduct.setUnit_price(product.getPrice());
            exitProduct.setSubtotal(ProductUtils.priceProductsSubtotal(productExit.getQuantity(), product.getPrice()));

            if (inventoryExit.getStatus() == InventoryEntryExitStatus.COMPLETED) {

                inventoryService.actualizarInventario(product.getId(), exitProduct.getQuantity(), false);

            }
            totalExit = totalExit
                    .add(exitProduct.getSubtotal());


            inventoryExit.getProducts().add(exitProduct);
        }

        inventoryExit.setTotal(totalExit);
        // Guardar la entrada de inventario
        inventoryExitRepository.save(inventoryExit);
        // Convertir la entrada de inventario a DTO y devolverla
        return inventoryExitMapper.toResponseDTO(inventoryExit);
    }

    // Actualizar una entrada de inventario
    public InventoryExitResponseDTO updateExit(Long userId, boolean isAdmin, Long id,
            InventoryExitRequestDTO requestDTO) {
        InventoryExit exit = inventoryExitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada"));

        // Verificar el estado de la entrada
        if (exit.getStatus() == InventoryEntryExitStatus.COMPLETED
                || exit.getStatus() == InventoryEntryExitStatus.CANCELLED) {
            throw new RuntimeException("No se puede modificar una salida que está completa o cancelada.");
        }

        if (exit.getUser().getId() != userId && !isAdmin) {
            throw new RuntimeException("El usuario no puede modificar la salida");
        }

        exit.setUpdatedAt(LocalDateTime.now());
        exit.setNotes(requestDTO.getNotes());
        exit.setStatus(requestDTO.getStatus());
        exit.setCategory(requestDTO.getCategory());
        exit.setModifiedByUserId(userId);
        // Actualizar productos
        updateProducts(exit, requestDTO.getProducts(), exit.getStatus());

        BigDecimal totalEntry = BigDecimal.ZERO;

        for (InventoryExitRequestDTO.ProductExitDTO productDTO : requestDTO.getProducts()) {
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            totalEntry = totalEntry
                    .add(ProductUtils.priceProductsSubtotal(productDTO.getQuantity(), product.getPrice()));
        }

        exit.setTotal(totalEntry);
        inventoryExitRepository.save(exit);
        return inventoryExitMapper.toResponseDTO(exit);
    }

    private void updateProducts(InventoryExit exit, List<InventoryExitRequestDTO.ProductExitDTO> productDTOs,
            InventoryEntryExitStatus status) {

        for (InventoryExitRequestDTO.ProductExitDTO productDTO : productDTOs) {
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Buscar si el producto ya existe en la salida
            Optional<InventoryExitProduct> existingExitProductOpt = exit.getProducts().stream()
                    .filter(ep -> ep.getProduct().getId().equals(product.getId()))
                    .findFirst();

            if (existingExitProductOpt.isPresent()) {
                // Actualizar el producto existente
                InventoryExitProduct existingExitProduct = existingExitProductOpt.get();
                existingExitProduct.setQuantity(productDTO.getQuantity());
                existingExitProduct.setSubtotal(
                        ProductUtils.priceProductsSubtotal(existingExitProduct.getQuantity(), product.getPrice()));
                if (status == InventoryEntryExitStatus.COMPLETED) {

                    inventoryService.actualizarInventario(product.getId(), existingExitProduct.getQuantity(), false);

                }

            } else {
                // Agregar un nuevo producto si no existe
                InventoryExitProduct inventoryExitProduct = new InventoryExitProduct();
                inventoryExitProduct.setProduct(product);
                inventoryExitProduct.setQuantity(productDTO.getQuantity());
                inventoryExitProduct.setExit(exit);
                inventoryExitProduct.setSubtotal(
                        ProductUtils.priceProductsSubtotal(inventoryExitProduct.getQuantity(), product.getPrice()));
                if (status == InventoryEntryExitStatus.COMPLETED) {
                    inventoryService.actualizarInventario(product.getId(), inventoryExitProduct.getQuantity(), false);
                }
                exit.getProducts().add(inventoryExitProduct);
            }
        }
    }

    // Eliminar una entrada de inventario
    public void deleteExit(Long id) {
        inventoryExitRepository.deleteById(id);
    }

     public void actualizarEstadoExit(Long userId, boolean isAdmin, Long entryId, InventoryEntryExitStatus newStatus) {

        InventoryExit exit = inventoryExitRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada"));

        if (exit.getUser().getId() != userId && !isAdmin) {
            throw new RuntimeException("El usuario no puede modificar la venta");
        }

        if (exit.getStatus() == InventoryEntryExitStatus.CANCELLED
                || exit.getStatus() == InventoryEntryExitStatus.COMPLETED) {
            throw new IllegalStateException("No se puede cambiar el estado de una venta cancelada o completa");
        }
        
        ajustarInventarioEntry(exit, exit.getStatus(), newStatus);
        
        exit.setModifiedByUserId(userId);
        
        exit.setStatus(newStatus);

        inventoryExitRepository.save(exit);
    }

    private void ajustarInventarioEntry(InventoryExit entry, InventoryEntryExitStatus statusAntes,
            InventoryEntryExitStatus statusNuevo) {
        List<InventoryExitProduct> exitProducts = entry.getProducts();

        for (InventoryExitProduct iEP : exitProducts) {
            Product product = iEP.getProduct();

            switch (statusNuevo) {
                case COMPLETED:
                    if (statusAntes == InventoryEntryExitStatus.PENDING) {
                        inventoryService.actualizarInventario(product.getId(), iEP.getQuantity(), false);
                    }
                    break;

                case CANCELLED:
                    if (statusAntes == InventoryEntryExitStatus.PENDING) {
                        inventoryService.actualizarInventario(product.getId(), iEP.getQuantity(), true);
                    }
                    break;
                default:
                    break;
            }

        }
    }
}
