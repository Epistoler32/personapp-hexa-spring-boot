# PersonAPP - Arquitectura Hexagonal con Spring Boot

Laboratorio de Arquitectura de Software. Servicio para administrar personas,
profesiones, teléfonos y estudios siguiendo arquitectura hexagonal (puertos y
adaptadores). Tiene dos adaptadores de entrada (API REST y consola) y dos de
salida (MariaDB y MongoDB); en cada operación se elige con qué base trabajar.

## Stack

| Herramienta | Versión |
| --- | --- |
| JDK | 25 (LTS) |
| Spring Boot | 4.1.2 |
| Maven | 3.9.x |
| MariaDB | 12.3 (LTS) |
| MongoDB | 9.0 |
| springdoc-openapi (Swagger / OpenAPI 3) | 3.2.0 |
| Lombok | 1.18.48 |
| Docker / Docker Compose | para levantar las bases |

## Estructura del proyecto

```
personapp-hexa-spring-boot
├── common                 anotaciones (@Port, @UseCase, @Adapter, @Mapper) y excepciones
├── domain                 entidades de dominio: Person, Profession, Phone, Study
├── application            puertos de entrada/salida y casos de uso
├── maria-output-adapter   adaptador de salida JPA para MariaDB
├── mongo-output-adapter   adaptador de salida Spring Data para MongoDB
├── rest-input-adapter     API REST + Swagger (puerto 3000)
├── cli-input-adapter      menú de consola
├── scripts                DDL y DML de MariaDB y MongoDB
└── docker-compose.yml     MariaDB y MongoDB con los scripts cargados
```

El flujo es siempre el mismo: el adaptador de entrada (REST o CLI) recibe la
base elegida (`MARIA` o `MONGO`), crea el caso de uso con el adaptador de salida
correspondiente y el caso de uso solo conoce el puerto de salida, no la base.

## Modelo de datos

| Tabla / colección | Campos | Notas |
| --- | --- | --- |
| `persona` | `cc` (PK), `nombre`, `apellido`, `genero` (`M`/`F`), `edad` | `edad` es opcional y no puede ser negativa |
| `profesion` | `id` (PK), `nom`, `des` | `des` es opcional |
| `telefono` | `num` (PK), `oper`, `duenio` (FK a persona) | |
| `estudios` | `id_prof` + `cc_per` (PK compuesta), `fecha`, `univer` | FK a profesion y persona |

Al eliminar una persona se eliminan también sus teléfonos y estudios; al
eliminar una profesión se eliminan los estudios asociados.

## 1. Configurar el ambiente

Se necesita JDK 25, Maven 3.9 y Docker. Verificar con:

```bash
java -version
mvn -version
docker compose version
```

`mvn -version` tiene que mostrar Java 25. Si muestra otra versión, apuntar
`JAVA_HOME` al JDK 25:

```bash
# Linux / macOS
export JAVA_HOME=/ruta/al/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
```

```powershell
# Windows (PowerShell)
$env:JAVA_HOME = "C:\ruta\al\jdk-25"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

## 2. Levantar las bases de datos

Desde la raíz del repositorio:

```bash
docker compose up -d
docker compose ps
```

Esto crea los contenedores `personapp-mariadb` (puerto 3307) y
`personapp-mongo` (puerto 27017). La primera vez que arrancan ejecutan los
scripts de `scripts/`, que crean el esquema, el usuario de la aplicación
(`persona_db` / `persona_db`) y cinco registros de ejemplo por entidad. Hay que
esperar a que ambos aparezcan como `healthy`.

Las contraseñas de root por defecto son `root`; se pueden cambiar con las
variables `MARIADB_ROOT_PASSWORD`, `MONGO_ROOT_USERNAME` y `MONGO_ROOT_PASSWORD`
antes del primer `up`.

Para detenerlas: `docker compose stop`. Para borrarlas con todos sus datos y
volver a cargar los scripts desde cero: `docker compose down -v`.

### Cargar los scripts manualmente

Si las bases ya existen y no se usa el compose, los scripts se pueden ejecutar
a mano (primero el DDL y después el DML):

```bash
mariadb -h 127.0.0.1 -P 3307 -u root -p < scripts/persona_ddl_maria.sql
mariadb -h 127.0.0.1 -P 3307 -u root -p < scripts/persona_dml_maria.sql
mongosh "mongodb://root:root@localhost:27017/admin" < scripts/persona_ddl_mongo.js
mongosh "mongodb://root:root@localhost:27017/admin" < scripts/persona_dml_mongo.js
```

Ojo: el DDL de MariaDB borra el esquema `persona_db` si ya existe.

La conexión de la aplicación está en el `application.properties` de cada
adaptador de entrada:
[REST](rest-input-adapter/src/main/resources/application.properties) y
[CLI](cli-input-adapter/src/main/resources/application.properties).

## 3. Compilar

```bash
mvn clean package
```

Compila todos los módulos, corre las pruebas unitarias y deja los jar
ejecutables en:

- `rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar`
- `cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar`

Para compilar sin pruebas: `mvn clean package -DskipTests`.

## 4. Desplegar y ejecutar

Con las bases arriba:

**API REST**

```bash
java -jar rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar
```

Queda escuchando en `http://localhost:3000`.

- Swagger UI: <http://localhost:3000/swagger-ui.html>
- OpenAPI (JSON): <http://localhost:3000/api-docs>

Si el puerto 3000 está ocupado se puede cambiar al arrancar:
`java -jar rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar --server.port=8080`.

**CLI**

```bash
java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar
```

El menú pide el módulo (persona, profesión, teléfono o estudio), la base
(MariaDB o MongoDB) y la operación (listar, buscar, contar, crear, editar,
eliminar o ver relaciones). La opción de relaciones muestra, según el módulo,
los teléfonos, estudios y profesiones de una persona, los estudios de una
profesión, el dueño de un teléfono o la persona y profesión de un estudio. Los
campos opcionales se dejan vacíos con Enter.

También se pueden ejecutar sin generar el jar, instalando primero los módulos:

```bash
mvn install -DskipTests
mvn -pl rest-input-adapter spring-boot:run
mvn -pl cli-input-adapter spring-boot:run
```

## Endpoints

`{db}` puede ser `MARIA` o `MONGO`. En los POST la base va en el cuerpo
(`"database"`); en el resto va en la ruta.

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/api/v1/persona/{db}` | Listar personas |
| GET | `/api/v1/persona/{db}/count` | Contar personas |
| GET | `/api/v1/persona/{db}/{cc}` | Buscar persona |
| GET | `/api/v1/persona/{db}/{cc}/telefonos` | Teléfonos de una persona |
| GET | `/api/v1/persona/{db}/{cc}/estudios` | Estudios de una persona |
| GET | `/api/v1/persona/{db}/{cc}/profesiones` | Profesiones que estudió una persona |
| POST | `/api/v1/persona` | Crear persona |
| PUT | `/api/v1/persona/{db}/{cc}` | Editar persona |
| DELETE | `/api/v1/persona/{db}/{cc}` | Eliminar persona |
| GET | `/api/v1/profesion/{db}` | Listar profesiones |
| GET | `/api/v1/profesion/{db}/count` | Contar profesiones |
| GET | `/api/v1/profesion/{db}/{id}` | Buscar profesión |
| GET | `/api/v1/profesion/{db}/{id}/estudios` | Estudios de una profesión |
| POST | `/api/v1/profesion` | Crear profesión |
| PUT | `/api/v1/profesion/{db}/{id}` | Editar profesión |
| DELETE | `/api/v1/profesion/{db}/{id}` | Eliminar profesión |
| GET | `/api/v1/telefono/{db}` | Listar teléfonos |
| GET | `/api/v1/telefono/{db}/count` | Contar teléfonos |
| GET | `/api/v1/telefono/{db}/{num}` | Buscar teléfono |
| GET | `/api/v1/telefono/{db}/{num}/duenio` | Dueño de un teléfono |
| POST | `/api/v1/telefono` | Crear teléfono |
| PUT | `/api/v1/telefono/{db}/{num}` | Editar teléfono |
| DELETE | `/api/v1/telefono/{db}/{num}` | Eliminar teléfono |
| GET | `/api/v1/estudio/{db}` | Listar estudios |
| GET | `/api/v1/estudio/{db}/count` | Contar estudios |
| GET | `/api/v1/estudio/{db}/{cc}/{idProf}` | Buscar estudio |
| POST | `/api/v1/estudio` | Crear estudio |
| PUT | `/api/v1/estudio/{db}/{cc}/{idProf}` | Editar estudio |
| DELETE | `/api/v1/estudio/{db}/{cc}/{idProf}` | Eliminar estudio |

Códigos de respuesta: `200` consulta/edición/eliminación correcta, `201`
creación, `400` datos inválidos, `404` no existe el registro, `409` registro
duplicado o conflicto con datos relacionados.

### Ejemplos

```bash
# crear una persona en MariaDB (edad es opcional)
curl -X POST http://localhost:3000/api/v1/persona -H 'Content-Type: application/json' \
  -d '{"dni":"1010","firstName":"Laura","lastName":"Rojas","age":"22","sex":"F","database":"MARIA"}'

# crear una profesión en MongoDB
curl -X POST http://localhost:3000/api/v1/profesion -H 'Content-Type: application/json' \
  -d '{"id":6,"name":"Arquitectura","description":"Diseno de edificaciones","database":"MONGO"}'

# crear un teléfono (el dueño debe existir en la misma base)
curl -X POST http://localhost:3000/api/v1/telefono -H 'Content-Type: application/json' \
  -d '{"number":"3101234567","company":"Claro","ownerIdentification":1010,"database":"MARIA"}'

# crear un estudio (persona y profesión deben existir)
curl -X POST http://localhost:3000/api/v1/estudio -H 'Content-Type: application/json' \
  -d '{"personIdentification":1010,"professionIdentification":1,"graduationDate":"2024-06-14","universityName":"Javeriana","database":"MARIA"}'

# editar una persona
curl -X PUT http://localhost:3000/api/v1/persona/MARIA/1010 -H 'Content-Type: application/json' \
  -d '{"firstName":"Laura","lastName":"Rojas","age":"23","sex":"F"}'

# consultar sus teléfonos y profesiones, y eliminarla
curl http://localhost:3000/api/v1/persona/MARIA/1010/telefonos
curl http://localhost:3000/api/v1/persona/MARIA/1010/profesiones
curl -X DELETE http://localhost:3000/api/v1/persona/MARIA/1010
```

## Scripts

| Archivo | Contenido |
| --- | --- |
| `scripts/persona_ddl_maria.sql` | Esquema `persona_db`, usuario de la aplicación y tablas con sus llaves |
| `scripts/persona_dml_maria.sql` | 5 registros por tabla |
| `scripts/persona_ddl_mongo.js` | Usuario de la aplicación y validador de la colección `persona` |
| `scripts/persona_dml_mongo.js` | 5 documentos por colección |

## Pruebas

```bash
mvn test
```

Las pruebas unitarias no necesitan bases de datos. Hay además pruebas de
integración (`IsolatedPersistenceIntegrationTest`, `IsolatedCliIntegrationTest`)
que crean y borran registros reales; solo se ejecutan si se define
`PERSONAPP_ISOLATED_INTEGRATION_TEST=true` y se apuntan a bases de prueba en
puertos distintos a los de desarrollo (ver las validaciones en cada clase).

## Problemas frecuentes

- **`release version 25 not supported`**: Maven está usando otro JDK; revisar
  `JAVA_HOME` y `mvn -version`.
- **`Connection refused` al iniciar**: las bases no están arriba o no han
  terminado de iniciar; revisar `docker compose ps`.
- **Los scripts no se ejecutaron**: solo corren la primera vez que se crea el
  volumen. Usar `docker compose down -v` y volver a levantar.
- **Puerto 3307 o 27017 ocupado**: detener la otra instancia o cambiar el
  puerto del lado izquierdo en `docker-compose.yml` y en los
  `application.properties`.
