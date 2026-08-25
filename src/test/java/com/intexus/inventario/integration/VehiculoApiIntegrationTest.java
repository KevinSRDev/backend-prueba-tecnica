package com.intexus.inventario.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * PRUEBAS DE INTEGRACIÓN de la API /api/vehiculos contra un MySQL REAL y
 * EFÍMERO (Testcontainers) que se inicializa con los MISMOS scripts de la
 * fase 01: db/01-schema.sql y db/02-seed.sql se montan en
 * /docker-entrypoint-initdb.d del contenedor. Así se prueba el CRUD sobre el
 * ESQUEMA DE PRODUCCIÓN, no sobre un esquema de test.
 *
 * Cada test es autocontenido (crea sus propios vehículos con datos únicos) y
 * @AfterEach limpia vía API únicamente lo que creó el propio test.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Integración: API Vehiculos sobre MySQL real (Testcontainers + esquema de db/)")
class VehiculoApiIntegrationTest {

  @Container
  @ServiceConnection
  static final MySQLContainer<?> mysql =
    new MySQLContainer<>("mysql:8.4")
      .withDatabaseName("inventario_vehiculos")
      .withUsername("appuser")
      .withPassword("app_pass_123")
      // Monta el esquema y el seed REALES del repo (modo solo lectura).
      .withFileSystemBind(
        new File("db").getAbsolutePath(),
        "/docker-entrypoint-initdb.d",
        BindMode.READ_ONLY
      );

  @Autowired
  private TestRestTemplate rest;

  @Autowired
  private ObjectMapper objectMapper;

  /** Ids de los vehículos que cada test crea (para limpiarlos en @AfterEach). */
  private final List<Long> idsCreados = new ArrayList<>();

  @AfterEach
  void limpiarVehiculosCreados() {
    for (Long id : idsCreados) {
      try {
        rest.exchange("/api/vehiculos/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, String.class, id);
      } catch (Exception ignorado) {
        // Ya no existe (los tests de borrado lo eliminan): nada que hacer.
      }
    }
    idsCreados.clear();
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  private ResponseEntity<String> postJson(String cuerpo) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return rest.postForEntity("/api/vehiculos", new HttpEntity<>(cuerpo, headers), String.class);
  }

  private JsonNode json(ResponseEntity<String> respuesta) throws Exception {
    return objectMapper.readTree(respuesta.getBody());
  }

  /** Crea un Auto GASOLINA de precio 100000 y devuelve el body JSON parseado. */
  private JsonNode crearAutoGasolina(String marca) throws Exception {
    String cuerpo =
      "{\"tipoVehiculo\":\"Auto\",\"marca\":\"" +
      marca +
      "\",\"modelo\":\"Sedan\",\"anio\":2022,\"precioBase\":100000," +
      "\"numeroPuertas\":4,\"tipoCombustible\":\"GASOLINA\"}";
    ResponseEntity<String> respuesta = postJson(cuerpo);
    assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
    JsonNode creado = json(respuesta);
    idsCreados.add(creado.get("id").asLong());
    return creado;
  }

  private JsonNode crearMoto(String marca, int cilindrada, int precioBase) throws Exception {
    String cuerpo =
      "{\"tipoVehiculo\":\"Moto\",\"marca\":\"" +
      marca +
      "\",\"modelo\":\"Sport\",\"anio\":2021,\"precioBase\":" +
      precioBase +
      ",\"cilindrada\":" +
      cilindrada +
      ",\"tipoFreno\":\"DISCO\"}";
    ResponseEntity<String> respuesta = postJson(cuerpo);
    assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
    JsonNode creado = json(respuesta);
    idsCreados.add(creado.get("id").asLong());
    return creado;
  }

  private void assertImpuesto(JsonNode vehiculo, String impuestoEsperado) {
    assertEquals(
      0,
      new BigDecimal(impuestoEsperado).compareTo(vehiculo.get("impuestoAnual").decimalValue())
    );
  }

  // ---------------------------------------------------------------------------
  // Tests
  // ---------------------------------------------------------------------------

  @Test
  @DisplayName(
    "crearAutoGasolina -> 201, tipoVehiculo Auto, id asignado e impuesto 7000.00 (5%+2%)"
  )
  void crearAutoGasolina_impuestoSiete() throws Exception {
    JsonNode creado = crearAutoGasolina("TestAutoGasolina");
    assertEquals("Auto", creado.get("tipoVehiculo").asText());
    assertNotNull(creado.get("id"));
    assertEquals(100000, creado.get("precioBase").decimalValue().intValue());
    assertImpuesto(creado, "7000.00");
  }

  @Test
  @DisplayName("crearMoto150 -> 201 e impuesto 150.00 (3%)")
  void crearMoto150_impuestoTres() throws Exception {
    JsonNode creado = crearMoto("TestMoto150", 150, 5000);
    assertEquals("Moto", creado.get("tipoVehiculo").asText());
    assertImpuesto(creado, "150.00");
  }

  @Test
  @DisplayName("crearMoto250 -> 201 e impuesto 300.00 (6%)")
  void crearMoto250_impuestoSeis() throws Exception {
    JsonNode creado = crearMoto("TestMoto250", 250, 5000);
    assertEquals("Moto", creado.get("tipoVehiculo").asText());
    assertImpuesto(creado, "300.00");
  }

  @Test
  @DisplayName(
    "listar -> incluye el vehículo recién creado y cada ítem trae impuestoAnual numérico"
  )
  void listar_incluyeImpuesto() throws Exception {
    JsonNode creado = crearAutoGasolina("TestListar");
    long idBuscado = creado.get("id").asLong();

    ResponseEntity<String> respuesta = rest.getForEntity("/api/vehiculos", String.class);
    assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    JsonNode lista = json(respuesta);
    assertTrue(lista.isArray());

    JsonNode encontrado = null;
    for (JsonNode item : lista) {
      assertTrue(
        item.get("impuestoAnual").isNumber(),
        "cada ítem de la lista debe tener impuestoAnual numérico"
      );
      if (item.get("id").asLong() == idBuscado) {
        encontrado = item;
      }
    }
    assertNotNull(
      encontrado,
      "la lista debe contener el vehículo recién creado (id " + idBuscado + ")"
    );
    assertEquals("TestListar", encontrado.get("marca").asText());
    assertImpuesto(encontrado, "7000.00");
  }

  @Test
  @DisplayName("obtenerPorId -> 200 con datos correctos del vehículo creado")
  void obtenerPorId() throws Exception {
    JsonNode creado = crearAutoGasolina("TestObtener");
    long id = creado.get("id").asLong();

    ResponseEntity<String> respuesta = rest.getForEntity("/api/vehiculos/{id}", String.class, id);
    assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    JsonNode detalle = json(respuesta);
    assertEquals(id, detalle.get("id").asLong());
    assertEquals("Auto", detalle.get("tipoVehiculo").asText());
    assertEquals("TestObtener", detalle.get("marca").asText());
    assertEquals(2022, detalle.get("anio").asInt());
    assertImpuesto(detalle, "7000.00");
  }

  @Test
  @DisplayName("obtenerInexistente -> 404 con mensaje de error")
  void obtenerInexistente_404() throws Exception {
    ResponseEntity<String> respuesta = rest.getForEntity(
      "/api/vehiculos/{id}",
      String.class,
      99_999_999
    );
    assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    JsonNode cuerpo = json(respuesta);
    assertNotNull(cuerpo.get("error"));
    assertTrue(!cuerpo.get("error").asText().isBlank());
  }

  @Test
  @DisplayName("actualizar -> 200 y el impuestoAnual se recalcula con el nuevo precioBase")
  void actualizar_cambiaImpuesto() throws Exception {
    JsonNode creado = crearAutoGasolina("TestActualizar");
    long id = creado.get("id").asLong();
    assertImpuesto(creado, "7000.00");

    String cuerpo =
      "{\"tipoVehiculo\":\"Auto\",\"marca\":\"TestActualizar\",\"modelo\":\"Sedan\",\"anio\":2022," +
      "\"precioBase\":200000,\"numeroPuertas\":4,\"tipoCombustible\":\"GASOLINA\"}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<String> respuesta = rest.exchange(
      "/api/vehiculos/{id}",
      HttpMethod.PUT,
      new HttpEntity<>(cuerpo, headers),
      String.class,
      id
    );

    assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    JsonNode actualizado = json(respuesta);
    assertEquals(200000, actualizado.get("precioBase").decimalValue().intValue());
    // 200000 * 7% = 14000.00 (distinto de los 7000.00 iniciales)
    assertImpuesto(actualizado, "14000.00");
  }

  @Test
  @DisplayName("eliminar -> 204 y luego GET del mismo id -> 404 (borrado real)")
  void eliminar_yNoDisponible() throws Exception {
    JsonNode creado = crearMoto("TestEliminar", 150, 5000);
    long id = creado.get("id").asLong();

    ResponseEntity<String> borrado = rest.exchange(
      "/api/vehiculos/{id}",
      HttpMethod.DELETE,
      HttpEntity.EMPTY,
      String.class,
      id
    );
    assertEquals(HttpStatus.NO_CONTENT, borrado.getStatusCode());

    ResponseEntity<String> siguiente = rest.getForEntity("/api/vehiculos/{id}", String.class, id);
    assertEquals(HttpStatus.NOT_FOUND, siguiente.getStatusCode());
  }

  @Test
  @DisplayName("precioInvalido -> POST precioBase 0 -> 400 con mensaje de error")
  void precioInvalido_400() throws Exception {
    String cuerpo =
      "{\"tipoVehiculo\":\"Auto\",\"marca\":\"TestInvalido\",\"modelo\":\"Sedan\",\"anio\":2022," +
      "\"precioBase\":0,\"numeroPuertas\":4,\"tipoCombustible\":\"GASOLINA\"}";
    ResponseEntity<String> respuesta = postJson(cuerpo);
    assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
    JsonNode error = json(respuesta);
    assertNotNull(error.get("error"));
    assertTrue(!error.get("error").asText().isBlank());
    // El id NO debe existir: la BD sigue consistente.
    assertNull(error.get("id"));
  }
}
