package com.avalon.Avalon_Inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan(basePackages = "com.avalon.Avalon_Inventory.infrastructure.configuration")
public class AvalonInventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(AvalonInventoryApplication.class, args);
	}

}
