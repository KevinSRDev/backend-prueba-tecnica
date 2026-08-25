package com.intexus.inventario.service;

import com.intexus.inventario.exception.VehiculoNotFoundException;
import com.intexus.inventario.model.Auto;
import com.intexus.inventario.model.Motocicleta;
import com.intexus.inventario.model.Vehiculo;
import com.intexus.inventario.repository.VehiculoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CAPA DE NEGOCIO: aquí vive la lógica de la aplicación (reglas del
 * ejercicio, validaciones de transición de estado). No conoce HTTP ni
 * protocolos; solo se comunica con el repositorio y con objetos del modelo.
 */
@Service
public class VehiculoService {

  private final VehiculoRepository vehiculoRepository;

  // Inyección por constructor (la dependencia es inmutable y testeable).
  public VehiculoService(VehiculoRepository vehiculoRepository) {
    this.vehiculoRepository = vehiculoRepository;
  }

  @Transactional(readOnly = true)
  public List<Vehiculo> listar() {
    // Polimorfismo: la lista devuelve Vehiculo (la abstracción) pero cada
    // elemento es un Auto o una Motocicleta y responde a su calcularImpuestoAnual().
    return vehiculoRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Vehiculo obtenerPorId(Long id) {
    return vehiculoRepository
      .findById(id)
      .orElseThrow(() -> new VehiculoNotFoundException("No existe un vehículo con id " + id));
  }

  @Transactional
  public Vehiculo crear(Vehiculo vehiculo) {
    // El subtipo ya llegó materializado por Jackson (Auto o Motocicleta
    // gracias a "tipoVehiculo" + @JsonSubTypes); guardamos el objeto tal cual.
    return vehiculoRepository.save(vehiculo);
  }

  @Transactional
  public Vehiculo actualizar(Long id, Vehiculo datos) {
    Vehiculo existente = obtenerPorId(id);

    // Un vehículo NO cambia de clase en una actualización: Auto sigue
    // siendo Auto y Moto sigue siendo Moto (el polimorfismo no "transforma"
    // objetos). Validamos el tipo REAL de ambos antes de copiar campos y
    // evitamos que un PUT con "tipoVehiculo" distinto rompa la herencia.
    if (!datos.getClass().equals(existente.getClass())) {
      throw new IllegalStateException(
        "El tipo de un vehículo no se puede cambiar: el id " +
          id +
          " corresponde a " +
          existente.getClass().getSimpleName() +
          " y se intentó actualizar con " +
          datos.getClass().getSimpleName()
      );
    }

    // Campos comunes (heredados); los setters validan cada valor.
    existente.setMarca(datos.getMarca());
    existente.setModelo(datos.getModelo());
    existente.setAnio(datos.getAnio());
    existente.setPrecioBase(datos.getPrecioBase());

    // Campos específicos, solo del subtipo correspondiente. Usamos pattern
    // matching (Java 21) + instanceof: es seguro porque ya validamos que
    // ambos objetos son de la MISMA clase.
    if (datos instanceof Auto autoDatos) {
      Auto autoExistente = (Auto) existente;
      autoExistente.setNumeroPuertas(autoDatos.getNumeroPuertas());
      autoExistente.setTipoCombustible(autoDatos.getTipoCombustible());
    } else if (datos instanceof Motocicleta motoDatos) {
      Motocicleta motoExistente = (Motocicleta) existente;
      motoExistente.setCilindrada(motoDatos.getCilindrada());
      motoExistente.setTipoFreno(motoDatos.getTipoFreno());
    }

    return vehiculoRepository.save(existente);
  }

  @Transactional
  public void eliminar(Long id) {
    Vehiculo existente = obtenerPorId(id);
    // Hibernate borra de la tabla vehiculos; el ON DELETE CASCADE de la
    // fase 01 limpia automáticamente autos/motocicletas (la tabla hija).
    vehiculoRepository.delete(existente);
  }
}
