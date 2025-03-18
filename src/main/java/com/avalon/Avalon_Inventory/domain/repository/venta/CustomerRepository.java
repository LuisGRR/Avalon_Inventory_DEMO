package com.avalon.Avalon_Inventory.domain.repository.venta;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avalon.Avalon_Inventory.domain.model.venta.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Long>{
    
}
