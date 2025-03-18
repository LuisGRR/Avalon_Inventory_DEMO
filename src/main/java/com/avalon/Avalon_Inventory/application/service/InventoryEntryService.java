package com.avalon.Avalon_Inventory.application.service;

import com.avalon.Avalon_Inventory.application.dto.InventoryEntryRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.InventoryEntryResponseDTO;
import com.avalon.Avalon_Inventory.domain.mapper.InventoryEntryMapper;
import com.avalon.Avalon_Inventory.domain.model.InventoryEntry;
import com.avalon.Avalon_Inventory.domain.model.InventoryEntryExitStatus;
import com.avalon.Avalon_Inventory.domain.model.InventoryEntryProduct;
import com.avalon.Avalon_Inventory.domain.model.Product;
import com.avalon.Avalon_Inventory.domain.model.ProductUtils;
import com.avalon.Avalon_Inventory.domain.model.User;
import com.avalon.Avalon_Inventory.domain.repository.InventoryEntryRepository;
import com.avalon.Avalon_Inventory.domain.repository.ProductRepository;
import com.avalon.Avalon_Inventory.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryEntryService {
    @Autowired
    private InventoryEntryRepository inventoryEntryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryEntryMapper inventoryEntryMapper;

    public Page<InventoryEntryResponseDTO> getAllEntries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<InventoryEntry> entries = inventoryEntryRepository.findAll(pageable);

        List<InventoryEntryResponseDTO> entryResponseDTO = entries.getContent().stream()
                .map(inventoryEntryMapper::toResponseDTO).collect(Collectors.toList());

        return new PageImpl<>(entryResponseDTO, pageable, entries.getTotalElements());
    }

    public Page<InventoryEntryResponseDTO> getAllEntriesId(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<InventoryEntry> entries = inventoryEntryRepository.findAllByUserId(id, pageable);

        List<InventoryEntryResponseDTO> entryResponseDTO = entries.getContent().stream()
                .map(inventoryEntryMapper::toResponseDTO).collect(Collectors.toList());

        return new PageImpl<>(entryResponseDTO, pageable, entries.getTotalElements());
    }

    // Obtener una entrada de inventario por ID
    public Optional<InventoryEntryResponseDTO> getEntryById(Long id) {
        return inventoryEntryRepository.findById(id)
                .map(inventoryEntryMapper::toResponseDTO);
    }

    public Optional<InventoryEntryResponseDTO> getEntryByIdAndUser(Long userId, Long id) {
        return inventoryEntryRepository.findByIdAndUserId(id, userId)
                .map(inventoryEntryMapper::toResponseDTO);
    }

    @Transactional
    public InventoryEntryResponseDTO registerInventoryEntry(Long userId, InventoryEntryRequestDTO entryDTO) {
        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal totalEntry = BigDecimal.ZERO;

        // Crear una nueva entrada de inventario
        InventoryEntry inventoryEntry = new InventoryEntry();
        inventoryEntry.setUser(user);
        inventoryEntry.setStatus(entryDTO.getStatus());
        inventoryEntry.setNotes(entryDTO.getNotes());
        inventoryEntry.setCreatedAt(LocalDateTime.now());
        inventoryEntry.setUpdatedAt(LocalDateTime.now());
        inventoryEntry.setProducts(new ArrayList<>());

        // Procesar los productos en la entrada
        for (InventoryEntryRequestDTO.ProductEntryDTO productEntry : entryDTO.getProducts()) {
            Product product = productRepository.findById(productEntry.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Crear una relación entre la entrada y el producto
            InventoryEntryProduct entryProduct = new InventoryEntryProduct();
            entryProduct.setEntry(inventoryEntry);
            entryProduct.setProduct(product);
            entryProduct.setQuantity(productEntry.getQuantity());
            entryProduct.setUnit_price(productEntry.getUnit_price());
            entryProduct.setSubtotal(
                    ProductUtils.priceProductsSubtotal(entryProduct.getQuantity(), entryProduct.getUnit_price()));

            if (entryDTO.getStatus() == InventoryEntryExitStatus.COMPLETED) {

                inventoryService.actualizarInventario(product.getId(), productEntry.getQuantity(), true);
            }

            totalEntry = totalEntry
                    .add(entryProduct.getSubtotal());

            inventoryEntry.getProducts().add(entryProduct); 
        }

        inventoryEntry.setTotal(totalEntry);

        // Guardar la entrada de inventario
        inventoryEntryRepository.save(inventoryEntry);
        // Convertir la entrada de inventario a DTO y devolverla
        return inventoryEntryMapper.toResponseDTO(inventoryEntry);
    }

    // Actualizar una entrada de inventario
    @Transactional
    public InventoryEntryResponseDTO updateEntry(Long userId, boolean isAdmin, Long id,
            InventoryEntryRequestDTO requestDTO) {
        InventoryEntry entry = inventoryEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada"));

        // Verificar el estado de la entrada
        if (entry.getStatus() == InventoryEntryExitStatus.COMPLETED
                || entry.getStatus() == InventoryEntryExitStatus.CANCELLED) {
            throw new RuntimeException("No se puede modificar una entrada que está completa o cancelada.");
        }

        if (entry.getUser().getId() != userId && !isAdmin) {
            throw new RuntimeException("El usuario no puede modificar la venta");
        }

        entry.setModifiedByUserId(userId);
        entry.setUpdatedAt(LocalDateTime.now());
        entry.setStatus(requestDTO.getStatus());
        entry.setNotes(requestDTO.getNotes());
        // Actualizar productos
        updateProducts(entry, requestDTO.getProducts(), entry.getStatus());

        BigDecimal totalEntry = BigDecimal.ZERO;

        for (InventoryEntryRequestDTO.ProductEntryDTO productDTO : requestDTO.getProducts()) {
            totalEntry = totalEntry
                    .add(ProductUtils.priceProductsSubtotal(productDTO.getQuantity(), productDTO.getUnit_price()));
        }

        entry.setTotal(totalEntry);
        inventoryEntryRepository.save(entry);
        return inventoryEntryMapper.toResponseDTO(entry);
    }

    // Método para actualizar productos
    private void updateProducts(InventoryEntry entry, List<InventoryEntryRequestDTO.ProductEntryDTO> productDTOs,
            InventoryEntryExitStatus status) {

        for (InventoryEntryRequestDTO.ProductEntryDTO productDTO : productDTOs) {
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Buscar si el producto ya existe en la entrada
            Optional<InventoryEntryProduct> existingEntryProductOpt = entry.getProducts().stream()
                    .filter(ep -> ep.getProduct().getId().equals(product.getId()))
                    .findFirst();

            if (existingEntryProductOpt.isPresent()) {
                // Actualizar el producto existente
                InventoryEntryProduct existingEntryProduct = existingEntryProductOpt.get();
                existingEntryProduct.setQuantity(productDTO.getQuantity());
                existingEntryProduct.setUnit_price(productDTO.getUnit_price());
                existingEntryProduct.setSubtotal(ProductUtils.priceProductsSubtotal(existingEntryProduct.getQuantity(),
                        existingEntryProduct.getUnit_price()));
                if (status == InventoryEntryExitStatus.COMPLETED) {

                    inventoryService.actualizarInventario(product.getId(), existingEntryProduct.getQuantity(), true);

                }
            } else {
                // Agregar un nuevo producto si no existe
                InventoryEntryProduct inventoryEntryProduct = new InventoryEntryProduct();
                inventoryEntryProduct.setProduct(product);
                inventoryEntryProduct.setQuantity(productDTO.getQuantity());
                inventoryEntryProduct.setEntry(entry);
                inventoryEntryProduct.setUnit_price(productDTO.getUnit_price());
                inventoryEntryProduct
                        .setSubtotal(ProductUtils.priceProductsSubtotal(inventoryEntryProduct.getQuantity(),
                                inventoryEntryProduct.getUnit_price()));
                if (status == InventoryEntryExitStatus.COMPLETED) {
 
                    inventoryService.actualizarInventario(product.getId(), productDTO.getQuantity(), true);
                }
                entry.getProducts().add(inventoryEntryProduct);
            }
        }
    }

    // Eliminar una entrada de inventario
    public void deleteEntry(Long id) {
        inventoryEntryRepository.deleteById(id);
    }

    public void actualizarEstadoEntry(Long userId, boolean isAdmin, Long entryId, InventoryEntryExitStatus newStatus) {

        InventoryEntry entry = inventoryEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada"));

        if (entry.getUser().getId() != userId && !isAdmin) {
            throw new RuntimeException("El usuario no puede modificar la venta");
        }

        if (entry.getStatus() == InventoryEntryExitStatus.CANCELLED
                || entry.getStatus() == InventoryEntryExitStatus.COMPLETED) {
            throw new IllegalStateException("No se puede cambiar el estado de una venta cancelada o completa");
        }
        
        ajustarInventarioEntry(entry, entry.getStatus(), newStatus);
        
        entry.setModifiedByUserId(userId);
        
        entry.setStatus(newStatus);

        inventoryEntryRepository.save(entry);
    }

    private void ajustarInventarioEntry(InventoryEntry entry, InventoryEntryExitStatus statusAntes,
            InventoryEntryExitStatus statusNuevo) {
        List<InventoryEntryProduct> entryProducts = entry.getProducts();

        for (InventoryEntryProduct iEP : entryProducts) {
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
