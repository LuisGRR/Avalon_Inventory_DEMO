package com.avalon.Avalon_Inventory.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avalon.Avalon_Inventory.domain.model.Product;
import com.avalon.Avalon_Inventory.domain.repository.ProductRepository;

@Service
public class InventoryService {

    @Autowired
    private ProductRepository productRepository;

    public void actualizarInventario(Long productId, int cantidad, boolean devolver) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        if (devolver) {
            product.setQuantity(product.getQuantity() + cantidad); // Devolver al inventario
        } else {
            if (product.getQuantity() < cantidad) {
                throw new IllegalStateException("Stock insuficiente para el producto con ID: " + productId);
            }
            product.setQuantity(product.getQuantity() - cantidad); // Restar del inventario
        }

        productRepository.save(product);
    }

    // Verifica si el stock está por debajo del umbral mínimo
    public boolean stockBajo(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return product.getQuantity() <= product.getUmbralMinimo();
    }

    // Genera un reporte de productos con stock bajo
    public List<Product> obtenerProductosBajoStock() {
        return productRepository.findAll().stream()
                .filter(product -> product.getQuantity() <= product.getUmbralMinimo())
                .collect(Collectors.toList());
    }
}
