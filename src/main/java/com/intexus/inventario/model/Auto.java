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
 * Subclase concreta AUTO de la jerarquía Vehiculo.
 *
 * ▸ HERENCIA: "Auto es un Vehiculo" (extends Vehiculo). En la BD se refleja
 *   como la tabla hija `autos`, unida a `vehiculos` por `vehiculo_id`
 *   (@PrimaryKeyJoinColumn = FK a la PK de la tabla base, relación 1:1).
 *   @DiscriminatorValue("Auto") indica que la columna `tipo_vehiculo`
 *   guardará 'Auto' para estas filas (y el JSON dirá "tipoVehiculo": "Auto").
 *
 * ▸ ENCAPSULAMIENTO: numeroPuertas y tipoCombustible son private y se
 *   validan en los setters (puertas 2..6; combustible obligatorio).
 *
 * ▸ POLIMORFISMO: reescribe calcularImpuestoAnual() con la regla del
 *   ejercicio: 5% del precio base + 2% adicional si el combustible es
 *   Gasolina o Diésel.
 */
@Entity
@Table(name = "autos")
@PrimaryKeyJoinColumn(name = "vehiculo_id")
@DiscriminatorValue("Auto")
@JsonSubTypes.Type(value = Auto.class, name = "Auto")
public class Auto extends Vehiculo {

  private Integer numeroPuertas;

  // STRING: guarda el nombre del enum tal cual (GASOLINA/DIESEL/ELECTRICO),
  // igual que la columna ENUM de la tabla `autos`.
  @Enumerated(EnumType.STRING)
  private TipoCombustible tipoCombustible;

  /**
   * Regla del ejercicio (Auto): 5% del precio base, más 2% adicional si el
   * combustible es GASOLINA o DIESEL (el eléctrico paga solo el 5%).
   */
  @Override
  public BigDecimal calcularImpuestoAnual() {
    BigDecimal impuesto = getPrecioBase().multiply(new BigDecimal("0.05"));
    if (tipoCombustible == TipoCombustible.GASOLINA || tipoCombustible == TipoCombustible.DIESEL) {
      impuesto = impuesto.add(getPrecioBase().multiply(new BigDecimal("0.02")));
    }
    return impuesto;
  }

  public Integer getNumeroPuertas() {
    return numeroPuertas;
  }

  public void setNumeroPuertas(Integer numeroPuertas) {
    if (numeroPuertas == null || numeroPuertas < 2 || numeroPuertas > 6) {
      throw new IllegalArgumentException("El número de puertas debe estar entre 2 y 6");
    }
    this.numeroPuertas = numeroPuertas;
  }

  public TipoCombustible getTipoCombustible() {
    return tipoCombustible;
  }

  public void setTipoCombustible(TipoCombustible tipoCombustible) {
    if (tipoCombustible == null) {
      throw new IllegalArgumentException("El tipo de combustible es obligatorio");
    }
    this.tipoCombustible = tipoCombustible;
  }
}
