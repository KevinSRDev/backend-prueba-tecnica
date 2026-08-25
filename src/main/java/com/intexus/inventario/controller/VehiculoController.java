package com.intexus.inventario.controller;

import com.intexus.inventario.model.Vehiculo;
import com.intexus.inventario.service.VehiculoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * CAPA DE PRESENTACIÓN (HTTP): recibe las peticiones del frontend, delega la
 * lógica al servicio y devuelve el JSON. No contiene reglas de negocio.
 * El CORS se configura en config/CorsConfig, no con @CrossOrigin.
 */
@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

  private final VehiculoService vehiculoService;

  public VehiculoController(VehiculoService vehiculoService) {
    this.vehiculoService = vehiculoService;
  }

  /**
   * GET /api/vehiculos -> lista completa. Cada elemento se serializa con su
   * tipo (tipoVehiculo) y su impuestoAnual calculado al serializar (el
   * getter getImpuestoAnual() llama a calcularImpuestoAnual()).
   */
  @GetMapping
  public List<Vehiculo> listarTodos() {
    return vehiculoService.listar();
  }

  /**
   * GET /api/vehiculos/{id} -> detalle de un vehículo (404 si no existe).
   */
  @GetMapping("/{id}")
  public Vehiculo obtenerPorId(@PathVariable Long id) {
    return vehiculoService.obtenerPorId(id);
  }

  /**
   * POST /api/vehiculos -> crea. El body lleva "tipoVehiculo": "Auto" o
   * "Moto"; Jackson instancia la subclase correcta gracias a @JsonSubTypes.
   * Responde 201 Created con el vehículo persistido (con id e impuesto).
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Vehiculo crear(@RequestBody Vehiculo vehiculo) {
    return vehiculoService.crear(vehiculo);
  }

  /**
   * PUT /api/vehiculos/{id} -> actualiza (200 con el vehículo actualizado).
   */
  @PutMapping("/{id}")
  public Vehiculo actualizar(@PathVariable Long id, @RequestBody Vehiculo datos) {
    return vehiculoService.actualizar(id, datos);
  }

  /**
   * DELETE /api/vehiculos/{id} -> 204 No Content, sin cuerpo.
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id) {
    vehiculoService.eliminar(id);
  }
}
