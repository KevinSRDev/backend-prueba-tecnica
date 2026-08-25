package com.intexus.inventario.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración CORS: la única "puerta" para el frontend local (Vite en
 * http://localhost:5173). Sin esto, el navegador bloquearía las peticiones
 * del microfrontend hacia la API por política de mismo origen.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
      .addMapping("/api/**") // cualquier ruta de la API
      .allowedOrigins("http://localhost:5173") // solo el frontend local
      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
      .allowedHeaders("*"); // contentType, etc. sin restricciones
  }
}
