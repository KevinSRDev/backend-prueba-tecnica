package com.intexus.inventario.model;

/**
 * Valores permitidos para el combustible de un Auto.
 *
 * Los nombres coinciden EXACTAMENTE con la columna ENUM 'tipo_combustible'
 * de la tabla `autos` (ASCII, sin tildes). La interfaz de usuario los
 * presenta traducidos ("Gasolina", "Diésel", "Eléctrico"), pero entre la
 * BD y la API viajan sin tilde, según el contrato de datos compartido.
 */
public enum TipoCombustible {
  GASOLINA,
  DIESEL,
  ELECTRICO,
}
