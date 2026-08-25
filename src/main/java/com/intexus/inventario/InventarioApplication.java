package com.intexus.inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API REST del Sistema de Gestión de Vehículos.
 *
 * @SpringBootApplication habilita la auto-configuración de Spring Boot
 * (inyección de dependencias, servidor web embebido, persistencia JPA, etc.)
 * y el escaneo de componentes en este paquete y sus subpaquetes.
 */
@SpringBootApplication
public class InventarioApplication {

  public static void main(String[] args) {
    SpringApplication.run(InventarioApplication.class, args);
  }
}
