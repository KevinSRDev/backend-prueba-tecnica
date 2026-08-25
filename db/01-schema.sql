-- ============================================================================
-- 01-schema.sql — Esquema de base de datos: Sistema de Gestión de Vehículos
-- Ejercicio práctico de POO (Herencia: Vehiculo -> Auto / Motocicleta)
-- ============================================================================

-- ▸ ESTRATEGIA DE HERENCIA: TABLE-PER-TYPE (tabla por tipo)
-- ----------------------------------------------------------------------------
-- En un esquema relacional la herencia de POO no existe de forma nativa, por
-- eso usamos la estrategia "Table-per-Type" (una tabla por tipo de clase):
--
--   * `vehiculos`    -> tabla BASE ("madre"): atributos comunes a todo vehículo
--   * `autos`        -> tabla HIJA: atributos específicos de un Auto
--   * `motocicletas` -> tabla HIJA: atributos específicos de una Motocicleta
--
-- Es el reflejo físico de la herencia POO:
--   Vehiculo (clase base) ──> Auto / Motocicleta (subclases concretas)
--
-- Para reconstruir un vehículo concreto se combina su fila en `vehiculos`
-- (datos comunes) con la fila de su tabla hija (datos específicos) usando el
-- mismo identificador; por eso `vehiculo_id` es PK en las tablas hijas (1:1).
--
-- ▸ EN JPA / HIBERNATE
-- ----------------------------------------------------------------------------
-- La clase base `Vehiculo` se anota con:
--
--     @Entity
--     @Inheritance(strategy = InheritanceType.JOINED)  -- Table-per-Type
--
-- La columna `tipo_vehiculo` actúa como DISCRIMINADOR: indica qué subtipo
-- concreto es cada fila ('Auto' | 'Moto') y su valor coincide EXACTAMENTE
-- con el valor que viaja en el JSON del backend (campo `tipoVehiculo`).
--
-- ----------------------------------------------------------------------------
-- NOTAS GLOBALES:
--   * CHARSET/COLLATE: utf8mb4 + utf8mb4_0900_ai_ci (acentos y emojis seguros).
--   * ENGINE: InnoDB (obligatorio para poder declarar claves foráneas).
-- ----------------------------------------------------------------------------

USE inventario_vehiculos;

-- ============================================================================
-- TABLA BASE: vehiculos
-- Representa la clase abstracta `Vehiculo` del modelo POO: solo contiene los
-- atributos que comparten los Autos y las Motocicletas.
-- ============================================================================
CREATE TABLE IF NOT EXISTS vehiculos (
  id            INT UNSIGNED   NOT NULL AUTO_INCREMENT,
  marca         VARCHAR(60)    NOT NULL,
  modelo        VARCHAR(60)    NOT NULL,
  -- 'anio' en ASCII (sin tilde) por convención del backend; la UI muestra "Año"
  anio          INT            NOT NULL,
  precio_base   DECIMAL(10, 2) NOT NULL,
  -- Discriminador de la herencia: mismo valor que viaja en el JSON (tipoVehiculo)
  tipo_vehiculo ENUM('Auto', 'Moto') NOT NULL,
  PRIMARY KEY (id),
  -- Regla de negocio: el año debe estar en el rango permitido (1950-2030).
  CONSTRAINT chk_vehiculos_anio CHECK (anio BETWEEN 1950 AND 2030),
  -- Requisito clave del ejercicio POO: el precio base NO puede ser <= 0.
  CONSTRAINT chk_vehiculos_precio_base CHECK (precio_base > 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = 'Tabla base: atributos comunes de todo vehiculo (Vehiculo en POO)';

-- ============================================================================
-- TABLA HIJA: autos
-- Representa la subclase `Auto`. `vehiculo_id` es FK a `vehiculos.id` y a la
-- vez PK: garantiza como máximo una fila hija por vehículo (relación 1:1).
-- ON DELETE CASCADE: si se borra el padre en `vehiculos`, se borra su hijo.
-- ============================================================================
CREATE TABLE IF NOT EXISTS autos (
  vehiculo_id    INT UNSIGNED NOT NULL,
  numero_puertas TINYINT UNSIGNED NOT NULL,
  -- Valores ASCII sin tilde; la UI muestra "Gasolina", "Diésel", "Eléctrico"
  tipo_combustible ENUM('GASOLINA', 'DIESEL', 'ELECTRICO') NOT NULL,
  PRIMARY KEY (vehiculo_id),
  CONSTRAINT fk_autos_vehiculo FOREIGN KEY (vehiculo_id)
    REFERENCES vehiculos (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT chk_autos_puertas CHECK (numero_puertas BETWEEN 2 AND 6)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = 'Tabla hija: atributos especificos de Auto (subclase en POO)';

-- ============================================================================
-- TABLA HIJA: motocicletas
-- Representa la subclase `Motocicleta`. Misma técnica: PK + FK a la base.
-- ============================================================================
CREATE TABLE IF NOT EXISTS motocicletas (
  vehiculo_id  INT UNSIGNED NOT NULL,
  -- Cilindrada en centímetros cúbicos (regula el impuesto anual en POO).
  cilindrada   INT UNSIGNED NOT NULL,
  -- Valores ASCII sin tilde; la UI muestra "Disco", "Tambor", "ABS"
  tipo_freno   ENUM('DISCO', 'TAMBOR', 'ABS') NOT NULL,
  PRIMARY KEY (vehiculo_id),
  CONSTRAINT fk_motocicletas_vehiculo FOREIGN KEY (vehiculo_id)
    REFERENCES vehiculos (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT chk_motocicletas_cilindrada CHECK (cilindrada > 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = 'Tabla hija: atributos especificos de Motocicleta (subclase en POO)';

-- ----------------------------------------------------------------------------
-- ▸ EJEMPLO DE CONSULTA: reconstruir un vehículo "completo" (la herencia ya
--   materializada en una sola fila). Se hace LEFT JOIN desde la tabla base
--   hacia la tabla hija según el discriminador tipo_vehiculo:
--
--     SELECT v.id,
--            v.marca,
--            v.modelo,
--            v.anio,
--            v.precio_base,
--            v.tipo_vehiculo,
--            a.numero_puertas,
--            a.tipo_combustible,
--            m.cilindrada,
--            m.tipo_freno
--     FROM vehiculos v
--     LEFT JOIN autos a        ON a.vehiculo_id = v.id
--     LEFT JOIN motocicletas m ON m.vehiculo_id = v.id
--     ORDER BY v.id;
--
--   Como cada vehículo solo tiene una fila en UNA de las tablas hijas, los
--   campos de la otra tabla salen NULL. Eso es justamente lo que representa
--   el "es un" de la herencia: un auto NO es una motocicleta y viceversa.
-- ----------------------------------------------------------------------------
