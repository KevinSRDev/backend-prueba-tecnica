package com.intexus.inventario.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de la subclase Auto (TODO el cálculo del impuesto anual
 * según la regla del ejercicio: 5% del precio base + 2% adicional si el
 * combustible es GASOLINA o DIESEL; el ELECTRICO paga solo el 5%).
 *
 * El resultado debe venir con 2 decimales (setScale(2, HALF_UP) en el getter
 * getImpuestoAnual de la clase base Vehiculo).
 */
@DisplayName("Auto - cálculo de impuesto anual (JUnit 5)")
class AutoTest {

  /** Construye un Auto válido con el precio base indicado y combustible indicado. */
  private Auto autoCon(BigDecimal precioBase, TipoCombustible combustible) {
    Auto auto = new Auto();
    auto.setMarca("Toyota");
    auto.setModelo("Corolla");
    auto.setAnio(2022);
    auto.setPrecioBase(precioBase);
    auto.setNumeroPuertas(4);
    auto.setTipoCombustible(combustible);
    return auto;
  }

  @Test
  @DisplayName("Gasolina: precioBase 100000 -> impuestoAnual 7000.00 (5% + 2%)")
  void gasolina_impuestoSietePorCiento() {
    Auto auto = autoCon(new BigDecimal("100000"), TipoCombustible.GASOLINA);
    // 100000 * 0.05 = 5000.00 + 100000 * 0.02 = 2000.00 -> 7000.00
    assertEquals(0, new BigDecimal("7000.00").compareTo(auto.getImpuestoAnual()));
  }

  @Test
  @DisplayName("Diésel: precioBase 100000 -> impuestoAnual 7000.00 (5% + 2%)")
  void diesel_impuestoSietePorCiento() {
    Auto auto = autoCon(new BigDecimal("100000"), TipoCombustible.DIESEL);
    // 100000 * 0.05 = 5000.00 + 100000 * 0.02 = 2000.00 -> 7000.00
    assertEquals(0, new BigDecimal("7000.00").compareTo(auto.getImpuestoAnual()));
  }

  @Test
  @DisplayName("Eléctrico: precioBase 100000 -> impuestoAnual 5000.00 (5%)")
  void electrico_impuestoCincoPorCiento() {
    Auto auto = autoCon(new BigDecimal("100000"), TipoCombustible.ELECTRICO);
    // 100000 * 0.05 = 5000.00 (el eléctrico NO paga el 2% adicional)
    assertEquals(0, new BigDecimal("5000.00").compareTo(auto.getImpuestoAnual()));
  }

  @Test
  @DisplayName("Precio con decimales 12345.67 Gasolina -> redondeo HALF_UP a 864.20")
  void precioConDecimales_redondeoHalfUp() {
    Auto auto = autoCon(new BigDecimal("12345.67"), TipoCombustible.GASOLINA);
    // 12345.67 * 0.07 (5% + 2%) = 864.1969 -> setScale(2, HALF_UP) -> 864.20
    assertEquals(0, new BigDecimal("864.20").compareTo(auto.getImpuestoAnual()));

    // El mismo resultado se obtiene replicando el cálculo con MathContext:
    BigDecimal esperado = new BigDecimal("12345.67")
      .multiply(new BigDecimal("0.07"))
      .setScale(2, RoundingMode.HALF_UP);
    assertEquals(0, esperado.compareTo(auto.getImpuestoAnual()));
  }
}
