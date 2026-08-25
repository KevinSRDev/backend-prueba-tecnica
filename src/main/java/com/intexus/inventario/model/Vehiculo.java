package com.intexus.inventario.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Clase BASE del dominio: el vehículo como concepto genérico.
 *
 * ▸ ABSTRACCIÓN: expone solo la esencia de "todo vehículo" (marca, modelo,
 *   anio, precioBase) sin saber si es un Auto o una Motocicleta. Por eso es
 *   abstracta: en el dominio real no existe un "vehículo genérico", solo
 *   vehículos concretos. No se puede instanciar Vehiculo a secas.
 *
 * ▸ HERENCIA: Auto y Motocicleta extienden esta clase. En la base de datos
 *   esa herencia se materializa con la estrategia Table-per-Type:
 *   - tabla BASE `vehiculos`  <-> esta clase (@Entity + @Inheritance(JOINED))
 *   - tabla HIJA `autos`      <-> clase Auto (vehiculo_id = vehiculos.id)
 *   - tabla HIJA `motocicletas` <-> clase Motocicleta (vehiculo_id = id)
 *   La columna `tipo_vehiculo` actúa como DISCRIMINADOR: guarda "Auto" o
 *   "Moto" (y en el JSON se llama tipoVehiculo, campo visible para Jackson).
 *
 * ▸ ENCAPSULAMIENTO: todos los atributos son private y solo se modifican a
 *   través de los setters, que VALIDAN antes de aceptar el valor. El estado
 *   interno nunca queda inconsistente (precio base > 0, año 1950-2030...).
 *
 * ▸ POLIMORFISMO: calcularImpuestoAnual() es abstracto por diseño: cada
 *   subclase sabe calcular SU impuesto (Auto: 5%+2% según combustible;
 *   Motocicleta: 3% o 6% según cilindrada). El código cliente envía el mismo
 *   mensaje y cada objeto responde según su tipo real.
 */
@Entity
@Table(name = "vehiculos")
@Inheritance(strategy = InheritanceType.JOINED) // = Table-per-Type (fase 01)
@DiscriminatorColumn(name = "tipo_vehiculo", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "tipoVehiculo",
  visible = true
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = Auto.class, name = "Auto"),
  @JsonSubTypes.Type(value = Motocicleta.class, name = "Moto"),
})
public abstract class Vehiculo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String marca;

  private String modelo;

  private Integer anio;

  private BigDecimal precioBase;

  /**
   * IMPUESTO POLIMÓRFICO: el mensaje no se implementa aquí porque el
   * cálculo depende del tipo concreto del receptor (ver Auto/Motocicleta).
   */
  public abstract BigDecimal calcularImpuestoAnual();

  /**
   * Expone el impuesto con el formato 2 decimales (0.00) que exige el
   * contrato de datos; aparece en el JSON de respuesta como "impuestoAnual".
   * Aunque no es un atributo persistido, Jackson lo serializa como getter.
   */
  public BigDecimal getImpuestoAnual() {
    return calcularImpuestoAnual().setScale(2, RoundingMode.HALF_UP);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMarca() {
    return marca;
  }

  public void setMarca(String marca) {
    if (marca == null || marca.isBlank()) {
      throw new IllegalArgumentException("La marca no puede estar vacía");
    }
    if (marca.length() > 60) {
      throw new IllegalArgumentException("La marca no puede superar los 60 caracteres");
    }
    this.marca = marca;
  }

  public String getModelo() {
    return modelo;
  }

  public void setModelo(String modelo) {
    if (modelo == null || modelo.isBlank()) {
      throw new IllegalArgumentException("El modelo no puede estar vacío");
    }
    if (modelo.length() > 60) {
      throw new IllegalArgumentException("El modelo no puede superar los 60 caracteres");
    }
    this.modelo = modelo;
  }

  public Integer getAnio() {
    return anio;
  }

  public void setAnio(Integer anio) {
    if (anio == null || anio < 1950 || anio > 2030) {
      throw new IllegalArgumentException("El año debe estar entre 1950 y 2030");
    }
    this.anio = anio;
  }

  public BigDecimal getPrecioBase() {
    return precioBase;
  }

  public void setPrecioBase(BigDecimal precioBase) {
    if (precioBase == null || precioBase.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El precio base debe ser mayor que 0");
    }
    this.precioBase = precioBase;
  }
}
