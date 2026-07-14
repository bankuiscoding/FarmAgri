package com.openmarket.farmersapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// Add this line to tell Spring to look in both places!
@ComponentScan(basePackages = { "com.openmarket.farmersapi", "com.openmarket.productmanagement", })
public class FarmersapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmersapiApplication.class, args);
	}

}