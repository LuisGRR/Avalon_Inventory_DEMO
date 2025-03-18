package com.avalon.Avalon_Inventory.domain.model;

import java.math.BigDecimal;

public class ProductUtils {

    /**
     * Calcula el total de un producto en base al precio por unidad y la cantidad.
     *
     * @param price El precio de una unidad del producto.
     * @param quantity        La cantidad de unidades del producto.
     * @return El total calculado como BigDecimal.
     */
    public static BigDecimal priceProductsSubtotal(Integer quantity, BigDecimal price) {
        BigDecimal quantityBigD = BigDecimal.valueOf(quantity);
        BigDecimal subTotal = price.multiply(quantityBigD);
        return subTotal;
    }
}
