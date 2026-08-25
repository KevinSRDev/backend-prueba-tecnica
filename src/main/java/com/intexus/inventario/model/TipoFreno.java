package com.intexus.inventario.model;

/**
 * Valores permitidos para el sistema de frenos de una Motocicleta.
 *
 * Coinciden EXACTAMENTE con la columna ENUM 'tipo_freno' de la tabla
 * `motocicletas` (ASCII, sin tildes); la UI muestra "Disco", "Tambor", "ABS".
 */
public enum TipoFreno {
  DISCO,
  TAMBOR,
  ABS,
}
