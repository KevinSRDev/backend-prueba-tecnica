package com.intexus.inventario.repository;

import com.intexus.inventario.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CAPA DE DATOS con Spring Data JPA: esta interfaz ES todo lo que se necesita
 * para el CRUD — NO escribimos SQL porque Spring Data genera las consultas
 * automáticamente a partir del tipo de entidad (Vehiculo y sus subclases) y
 * del nombre de los métodos heredados:
 *
 *   - findAll()        -> SELECT ... FROM vehiculos (con join a las tablas
 *                         hijas, gracias a @Inheritance(JOINED))
 *   - findById(id)     -> SELECT ... WHERE id = ?
 *   - save(vehiculo)   -> INSERT / UPDATE (Hibernate decide según el id)
 *   - delete(vehiculo) -> DELETE (el ON DELETE CASCADE limpia la tabla hija)
 *
 * La capa de negocio (service) solo habla con esta interfaz, nunca con JDBC.
 */
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {}
