package com.intexus.inventario.exception;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traductor de excepciones -> códigos HTTP con un único formato de error:
 * { "error": "mensaje claro en español" }. Así el frontend SIEMPRE recibe
 * el mismo shape, sin importar la causa.
 *
 * POR QUÉ: sin este advice, las excepciones no mapeadas producirían un
 * error HTML de Spring con status 500, difícil de interpretar para la UI.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** 404: el recurso no existe (ej. GET /999999). */
  @ExceptionHandler(VehiculoNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> vehiculoNoEncontrado(VehiculoNotFoundException ex) {
    return Map.of("error", ex.getMessage());
  }

  /**
   * 400: peticiones "malas" del cliente.
   * - IllegalArgumentException: validación de los setters (precioBase <= 0,
   *   año fuera de rango, puertas inválidas, marca vacía, etc.).
   * - IllegalStateException: intento de cambiar el tipo Auto <-> Moto.
   * - HttpMessageNotReadableException: JSON malformado o tipoVehiculo
   *   desconocido (Jackson no sabe qué clase instanciar).
   * - DataIntegrityViolationException: viola una restricción de la BD
   *   (ENUM, CHECK, FK), por si algún dato escapa a la validación Java.
   */
  @ExceptionHandler({
    IllegalArgumentException.class,
    IllegalStateException.class,
    HttpMessageNotReadableException.class,
    DataIntegrityViolationException.class,
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> peticionInvalida(RuntimeException ex) {
    return Map.of("error", ex.getMessage());
  }

  /** 500 genérico: error inesperado del servidor (no culpable del cliente). */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, String> errorInterno(Exception ex) {
    return Map.of("error", "Error interno del servidor");
  }
}
