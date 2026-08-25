package com.intexus.inventario.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de la subclase Motocicleta: el impuesto es el 3% del
 * precio base si la cilindrada es <= 150cc y el 6% si es MAYOR que 150cc
 * (regla del ejercicio, "es policíndrica" en el límite 150->3%, 151->6%).
 */
@DisplayName("Motocicleta - cálculo de impuesto anual (JUnit 5)")
class MotocicletaTest {

  /** Construye una Motocicleta válida con el precio base y la cilindrada indicados. */
  private Motocicleta motoCon(BigDecimal precioBase, Integer cilindrada) {
    Motocicleta moto = new Motocicleta();
    moto.setMarca("Honda");
    moto.setModelo("CG 150");
    moto.setAnio(2021);
    moto.setPrecioBase(precioBase);
    moto.setCilindrada(cilindrada);
    moto.setTipoFreno(TipoFreno.DISCO);
    return moto;
  }

  @Test
  @DisplayName("Cilindrada 150 (límite inclusive): precioBase 5000 -> impuestoAnual 150.00 (3%)")
  void cilindrada150_impuestoTresPorCiento() {
    Motocicleta moto = motoCon(new BigDecimal("5000"), 150);
    // 5000 * 0.03 = 150.00
    assertEquals(0, new BigDecimal("150.00").compareTo(moto.getImpuestoAnual()));
  }

  @Test
  @DisplayName("Cilindrada 151: precioBase 5000 -> impuestoAnual 300.00 (6%)")
  void cilindrada151_impuestoSeisPorCiento() {
    Motocicleta moto = motoCon(new BigDecimal("5000"), 151);
    // 5000 * 0.06 = 300.00
    assertEquals(0, new BigDecimal("300.00").compareTo(moto.getImpuestoAnual()));
  }

  @Test
  @DisplayName("Cilindrada 1000: precioBase 5000 -> impuestoAnual 300.00 (6%)")
  void cilindrada1000_impuestoSeisPorCiento() {
    Motocicleta moto = motoCon(new BigDecimal("5000"), 1000);
    // 5000 * 0.06 = 300.00
    assertEquals(0, new BigDecimal("300.00").compareTo(moto.getImpuestoAnual()));
  }
}
