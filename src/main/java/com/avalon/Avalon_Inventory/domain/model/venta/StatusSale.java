package com.avalon.Avalon_Inventory.domain.model.venta;

public enum StatusSale {
    PENDIENTE,      // La venta ha sido creada pero no se ha completado
    COMPLETADA,     // La venta ha sido completada con éxito
    CANCELADA,      // La venta ha sido cancelada
    EN_PROCESO,     // La venta está en proceso de ser completada
    REEMBOLSADA,    // La venta ha sido reembolsada
    FALLIDA;        // La venta no se pudo completar debido a un error
}
