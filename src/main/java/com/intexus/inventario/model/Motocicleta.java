package com.intexus.inventario.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Subclase concreta MOTOCICLETA de la jerarquía Vehiculo.
 *
 * ▸ HERENCIA: "Motocicleta es un Vehiculo". En la BD se refleja como la
 *   tabla hija `motocicletas`, unida a `vehiculos` por `vehiculo_id`
 *   (@PrimaryKeyJoinColumn, relación 1:1 con la tabla base).
 *   @DiscriminatorValue("Moto") = columna tipo_vehiculo con 'Moto'
 *   (en el JSON: "tipoVehiculo": "Moto").
 *
 * ▸ ENCAPSULAMIENTO: cilindrada y tipoFreno privados, validados en setters
 *   (cilindrada > 0; tipo de freno obligatorio).
 *
 * ▸ POLIMORFISMO: reescribe calcularImpuestoAnual(): 3% si la cilindrada
 *   es <= 150cc y 6% si es mayor a 150cc (regla del ejercicio).
 */
@Entity
@Table(name = "motocicletas")
@PrimaryKeyJoinColumn(name = "vehiculo_id")
@DiscriminatorValue("Moto")
@JsonSubTypes.Type(value = Motocicleta.class, name = "Moto")
public class Motocicleta extends Vehiculo {

  private Integer cilindrada;

  // STRING: guarda el nombre del enum (DISCO/TAMBOR/ABS), igual que la
  // columna ENUM de la tabla `motocicletas`.
  @Enumerated(EnumType.STRING)
  private TipoFreno tipoFreno;

  /**
   * Regla del ejercicio (Motocicleta): 3% del precio base hasta 150cc
   * (inclusive) y 6% por encima de 150cc.
   */
  @Override
  public BigDecimal calcularImpuestoAnual() {
    BigDecimal porcentaje = cilindrada <= 150 ? new BigDecimal("0.03") : new BigDecimal("0.06");
    return getPrecioBase().multiply(porcentaje);
  }

  public Integer getCilindrada() {
    return cilindrada;
  }

  public void setCilindrada(Integer cilindrada) {
    if (cilindrada == null || cilindrada <= 0) {
      throw new IllegalArgumentException("La cilindrada debe ser mayor que 0");
    }
    this.cilindrada = cilindrada;
  }

  public TipoFreno getTipoFreno() {
    return tipoFreno;
  }

  public void setTipoFreno(TipoFreno tipoFreno) {
    if (tipoFreno == null) {
      throw new IllegalArgumentException("El tipo de freno es obligatorio");
    }
    this.tipoFreno = tipoFreno;
  }
}
