package com.avalon.Avalon_Inventory.application.service;

import com.avalon.Avalon_Inventory.application.dto.ProductRequestDTO;
import com.avalon.Avalon_Inventory.application.dto.ProductResponseDTO;
import com.avalon.Avalon_Inventory.domain.model.Product;
import com.avalon.Avalon_Inventory.domain.repository.ProductRepository;
import com.avalon.Avalon_Inventory.domain.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    // Obtener todos los productos
    public Page<ProductResponseDTO> getAllProductsPaginate(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAllOrderedByStock(pageable);

        List<ProductResponseDTO> productDTOs = productPage.getContent().stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }
    

    public Page<ProductResponseDTO> getFilteredProducts(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.searchByName(filter, pageable);

        List<ProductResponseDTO> productDTOs = productPage.getContent().stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
                
        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    // Obtener un producto por ID
    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponseDTO)
                .orElse(null);
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = productMapper.toEntity(productRequestDTO);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // Guardar la entidad en la base de datos
        Product savedProduct = productRepository.save(product);

        // Convertir la entidad guardada a DTO de respuesta
        return productMapper.toResponseDTO(savedProduct);
    }

    // Actualizar un producto
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    productMapper.updateProductFromDto(productRequestDTO, existingProduct);
                    existingProduct.setUpdatedAt(LocalDateTime.now());
                    Product updatedProduct = productRepository.save(existingProduct);
                    return productMapper.toResponseDTO(updatedProduct);
                })
                .orElse(null);
    }

    // Eliminar un producto
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
