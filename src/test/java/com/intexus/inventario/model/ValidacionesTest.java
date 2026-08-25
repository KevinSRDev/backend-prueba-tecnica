package com.intexus.inventario.model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de las VALIDACIONES encapsuladas en el dominio: los setters/
 * constructores de Vehiculo/Auto/Motocicleta deben rechazar los estados
 * inválidos con IllegalArgumentException y un mensaje de error NO vacío.
 */
@DisplayName("Validaciones de dominio (encapsulamiento)")
class ValidacionesTest {

  private Auto autoBase() {
    Auto auto = new Auto();
    auto.setMarca("Toyota");
    auto.setModelo("Corolla");
    auto.setAnio(2022);
    auto.setPrecioBase(new BigDecimal("100000"));
    auto.setNumeroPuertas(4);
    auto.setTipoCombustible(TipoCombustible.GASOLINA);
    return auto;
  }

  private Motocicleta motoBase() {
    Motocicleta moto = new Motocicleta();
    moto.setMarca("Honda");
    moto.setModelo("CG 150");
    moto.setAnio(2021);
    moto.setPrecioBase(new BigDecimal("5000"));
    moto.setCilindrada(150);
    moto.setTipoFreno(TipoFreno.DISCO);
    return moto;
  }

  @Test
  @DisplayName("precioBase 0 -> IllegalArgumentException con mensaje no vacío")
  void precioBaseCero_rechazado() {
    IllegalArgumentException ex =
      assertThrows(IllegalArgumentException.class, () -> autoBase().setPrecioBase(BigDecimal.ZERO));
    assertTrue(ex.getMessage() != null && !ex.getMessage().isBlank());
  }

  @Test
  @DisplayName("precioBase -1 -> IllegalArgumentException con mensaje no vacío")
  void precioBaseNegativo_rechazado() {
    IllegalArgumentException ex =
      assertThrows(
        IllegalArgumentException.class,
        () -> autoBase().setPrecioBase(new BigDecimal("-1"))
      );
    assertTrue(ex.getMessage() != null && !ex.getMessage().isBlank());
  }

  @Test
  @DisplayName("marca vacía -> IllegalArgumentException")
  void marcaVacia_rechazada() {
    assertThrows(IllegalArgumentException.class, () -> autoBase().setMarca("   "));
  }

  @Test
  @DisplayName("marca null -> IllegalArgumentException")
  void marcaNula_rechazada() {
    IllegalArgumentException ex =
      assertThrows(IllegalArgumentException.class, () -> autoBase().setMarca(null));
    assertTrue(!ex.getMessage().isBlank());
  }

  @Test
  @DisplayName("anio 1900 (fuera de 1950-2030) -> IllegalArgumentException")
  void anioFueraDeRango_rechazado() {
    IllegalArgumentException ex =
      assertThrows(IllegalArgumentException.class, () -> autoBase().setAnio(1900));
    assertTrue(!ex.getMessage().isBlank());
  }

  @Test
  @DisplayName("Auto numeroPuertas 1 (fuera de 2-6) -> IllegalArgumentException")
  void numeroPuertasInvalido_rechazado() {
    IllegalArgumentException ex =
      assertThrows(IllegalArgumentException.class, () -> autoBase().setNumeroPuertas(1));
    assertTrue(!ex.getMessage().isBlank());
  }

  @Test
  @DisplayName("Moto cilindrada 0 -> IllegalArgumentException")
  void cilindradaCero_rechazada() {
    IllegalArgumentException ex =
      assertThrows(IllegalArgumentException.class, () -> motoBase().setCilindrada(0));
    assertTrue(!ex.getMessage().isBlank());
  }

  @Test
  @DisplayName("Moto cilindrada negativa -> IllegalArgumentException")
  void cilindradaNegativa_rechazada() {
    assertThrows(IllegalArgumentException.class, () -> motoBase().setCilindrada(-20));
  }

  @Test
  @DisplayName("Auto tipoCombustible null -> IllegalArgumentException")
  void combustibleNulo_rechazado() {
    assertThrows(IllegalArgumentException.class, () -> autoBase().setTipoCombustible(null));
  }

  @Test
  @DisplayName("Moto tipoFreno null -> IllegalArgumentException")
  void frenoNulo_rechazado() {
    assertThrows(IllegalArgumentException.class, () -> motoBase().setTipoFreno(null));
  }
}
