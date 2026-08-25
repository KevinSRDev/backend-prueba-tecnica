# backend-prueba-tecnica

## Sistema de Gestión de Vehículos — Backend API REST

Ejercicio práctico de **Programación Orientada a Objetos (POO)** — Backend de una
aplicación fullstack CRUD para administrar el inventario de vehículos (Autos y
Motocicletas) de una empresa de arrendamiento de transporte.

+ **Objetivo del ejercicio:** modelar con POO explícita —*Abstracción*, *Herencia*,
  *Encapsulamiento* y *Polimorfismo*— una API REST que persiste en MySQL y devuelve
  el **cálculo dinámico del impuesto anual** por tipo de vehículo.

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje / Runtime | **Java 21 (Amazon Corretto)** |
| Framework | **Spring Boot 3.5.3** (Web + Data JPA) |
| Base de datos | **MySQL 8.4** (contenedor Docker, ver `docker-compose.yml`) |
| Build | Maven 3.9+ |
| Formato de código | Prettier + `prettier-plugin-java` (via npm, `npm run format`) |

## Modelado POO (lo que evalúa la rúbrica)

- **Abstracción:** clase abstracta `Vehiculo` (src/main/java/com/intexus/inventario/model/Vehiculo.java)
  con atributos encapsulados (`private`) y validaciones de negocio (p. ej. precio base > 0).
- **Herencia:** `Auto` y `Motocicleta` extienden `Vehiculo` e incorporan sus
  atributos específicos.
- **Polimorfismo:** método abstracto `calcularImpuestoAnual()` implementado por cada subclase:
  - **Auto:** 5% del precio base + 2% adicional si el combustible es Gasolina o Diésel.
  - **Motocicleta:** 3% si `cilindrada <= 150`; 6% si `cilindrada > 150`.
- **Persistencia de la herencia:** `@Inheritance(strategy = InheritanceType.JOINED)`
  (Tabla-per-Tipo) → tablas `vehiculos` (base), `autos` y `motocicletas` (hijas).

## Endpoints (CRUD)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/vehiculos` | Lista total de vehículos, incluido el campo `impuestoAnual` |
| GET | `/api/vehiculos/{id}` | Detalle de un vehículo específico |
| POST | `/api/vehiculos` | Crea un Auto o Motocicleta según `tipoVehiculo` del payload |
| PUT | `/api/vehiculos/{id}` | Actualiza un vehículo existente |
| DELETE | `/api/vehiculos/{id}` | Elimina un vehículo |

## Requisitos previos

- **Java 21** (`java -version`)
- **Maven 3.9+** (`mvn -version`)
- **Docker** con el daemon activo (`docker ps`) — para levantar MySQL
- Node + npm (necesario solo para el formateo con Prettier: `npm install` una vez)

## Cómo ejecutar

### 1. Levantar la base de datos (MySQL 8.4)

```bash
docker compose up -d
```

Los scripts `db/01-schema.sql` (esquema Table-per-Type) y `db/02-seed.sql`
(datos de ejemplo) se ejecutan automáticamente en el primer arranque.
Verificar que está healthy:

```bash
docker compose ps   # STATUS: healthy
```

### 2. Arrancar la API (puerto 8080)

```bash
mvn spring-boot:run
```

### 3. Verificar

```bash
curl http://localhost:8080/api/vehiculos
```

Debe responder `200` con JSON en camelCase, incluyendo `impuestoAnual`.

## Credenciales de base de datos (entorno local)

| Parámetro | Valor |
|---|---|
| Host | `127.0.0.1:3306` |
| Base de datos | `inventario_vehiculos` |
| Usuario | `appuser` |
| Contraseña | `app_pass_123` |

> Son credenciales del entorno de desarrollo del ejercicio, definidas en
> `docker-compose.yml` y `src/main/resources/application.properties`. No son
> secretos de producción.

## Tests

```bash
mvn test
```

Incluye pruebas unitarias de los modelos (`AutoTest`, `MotocicletaTest`,
`ValidacionesTest`) y pruebas de integración de la API con Testcontainers
(`src/test/java/com/intexus/inventario/integration/`).

## Estructura del proyecto

```
├── docker-compose.yml        # MySQL 8.4 + scripts de init
├── db/
│   ├── 01-schema.sql         # Esquema Table-per-Type (vehiculos/autos/motocicletas)
│   └── 02-seed.sql           # Datos de ejemplo
└── src/main/java/com/intexus/inventario/
    ├── model/                # 🎓 POO: Vehiculo (abstracta) + Auto + Motocicleta
    ├── repository/           # Spring Data JPA
    ├── service/              # Lógica de negocio (cálculo de impuestos)
    ├── controller/           # Endpoints REST
    ├── exception/            # Manejo de errores global
    └── config/               # CORS
```

## Nombre y formato

- `spring.jpa.hibernate.ddl-auto=none`: Hibernate no modifica el esquema; la
  BD creada por los scripts SQL es la única fuente de verdad.
- Formateo: `npm run format` (configuración compartida en `.prettierrc`).
