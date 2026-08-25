package com.intexus.inventario.exception;

/**
 * Excepción de dominio: se lanza cuando se consulta o modifica un vehículo
 * que no existe en la base de datos. La traduce el GlobalExceptionHandler a
 * un HTTP 404 con { "error": "mensaje" }.
 */
public class VehiculoNotFoundException extends RuntimeException {

  public VehiculoNotFoundException(String mensaje) {
    super(mensaje);
  }
}
