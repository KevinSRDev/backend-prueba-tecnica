-- ============================================================================
-- 02-seed.sql — Datos de ejemplo para el Sistema de Gestión de Vehículos
-- ----------------------------------------------------------------------------
-- Patrón para materializar la herencia Table-per-Type en los insert:
--   1) INSERT en la tabla base (vehiculos) -> AUTO_INCREMENT genera el id.
--   2) SET @ultimo = LAST_INSERT_ID();    -> captura el id recién generado.
--   3) INSERT en la tabla hija (autos / motocicletas) usando @ultimo.
-- Así la fila hija queda "heredada" y enlazada con su padre por la PK/FK.
-- ----------------------------------------------------------------------------
-- Datos: 2 AUTOS (uno GASOLINA y uno ELECTRICO) y 2 MOTOS (150cc y 250cc).
-- ============================================================================

USE inventario_vehiculos;

-- ----------------------------------------------------------------------------
-- AUTO 1: Toyota Corolla (GASOLINA, 4 puertas)
-- ----------------------------------------------------------------------------
INSERT INTO vehiculos (marca, modelo, anio, precio_base, tipo_vehiculo)
VALUES ('Toyota', 'Corolla', 2022, 42000.00, 'Auto');
SET @ultimo = LAST_INSERT_ID();
INSERT INTO autos (vehiculo_id, numero_puertas, tipo_combustible)
VALUES (@ultimo, 4, 'GASOLINA');

-- ----------------------------------------------------------------------------
-- AUTO 2: Tesla Model 3 (ELECTRICO, 5 puertas)
-- ----------------------------------------------------------------------------
INSERT INTO vehiculos (marca, modelo, anio, precio_base, tipo_vehiculo)
VALUES ('Tesla', 'Model 3', 2024, 55000.00, 'Auto');
SET @ultimo = LAST_INSERT_ID();
INSERT INTO autos (vehiculo_id, numero_puertas, tipo_combustible)
VALUES (@ultimo, 5, 'ELECTRICO');

-- ----------------------------------------------------------------------------
-- MOTO 1: Honda CG 150 (150cc, freno DISCO)
-- ----------------------------------------------------------------------------
INSERT INTO vehiculos (marca, modelo, anio, precio_base, tipo_vehiculo)
VALUES ('Honda', 'CG 150', 2021, 8900.00, 'Moto');
SET @ultimo = LAST_INSERT_ID();
INSERT INTO motocicletas (vehiculo_id, cilindrada, tipo_freno)
VALUES (@ultimo, 150, 'DISCO');

-- ----------------------------------------------------------------------------
-- MOTO 2: Yamaha FZ 250 (250cc, freno ABS)
-- ----------------------------------------------------------------------------
INSERT INTO vehiculos (marca, modelo, anio, precio_base, tipo_vehiculo)
VALUES ('Yamaha', 'FZ 250', 2023, 15500.50, 'Moto');
SET @ultimo = LAST_INSERT_ID();
INSERT INTO motocicletas (vehiculo_id, cilindrada, tipo_freno)
VALUES (@ultimo, 250, 'ABS');
