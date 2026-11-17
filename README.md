# PersonAPP: arquitectura hexagonal

Aplicación de ejemplo para administrar personas, profesiones, teléfonos y
estudios. Tiene dos entradas independientes: una API REST y una interfaz de
consola (CLI). Ambas pueden trabajar con MariaDB o MongoDB.

## Módulos

| Módulo | Función |
| --- | --- |
| `domain` | Modelos y reglas del dominio. |
| `application` | Casos de uso y puertos de entrada y salida. |
| `common` | Elementos compartidos. |
| `rest-input-adapter` | API REST y documentación Swagger. |
| `cli-input-adapter` | Menús de consola. |
| `maria-output-adapter` | Persistencia en MariaDB. |
| `mongo-output-adapter` | Persistencia en MongoDB. |

El `pom.xml` raíz agrupa los módulos en un reactor Maven.

## Requisitos

- JDK 25 y Maven. El proyecto utiliza Spring Boot 4.1.2 y Lombok.
- MariaDB y MongoDB. En este laboratorio se probaron MariaDB 12.3 y MongoDB 9.0.
- Docker Desktop si vas a utilizar los ejemplos con contenedores.
- En VS Code, extensiones de Java y soporte para Lombok si se utiliza el IDE.

Comprueba en la terminal:

```bash
java -version
mvn -version
docker --version
```

Maven debe indicar que usa Java 25. Si no encuentra `mvn`, agrega el directorio
`bin` de Maven al `PATH` o invoca directamente `mvn.cmd`. Los comandos Bash y
PowerShell no son intercambiables.

### Java y Maven en Git Bash

Desde la raíz del proyecto, reemplaza las rutas de ejemplo por las de tu equipo:

```bash
export JAVA_HOME='C:\ruta\al\jdk-25'
export PATH="$(cygpath -u "$JAVA_HOME\bin"):$PATH"
MAVEN="$(cygpath -u 'C:\ruta\a\maven\bin\mvn.cmd')"
"$MAVEN" -version
```

Los comandos siguientes usan `"$MAVEN"` en Git Bash. Estas variables se deben
configurar de nuevo cuando se abra otra terminal.

En PowerShell, reemplaza también las rutas de ejemplo:

```powershell
$env:JAVA_HOME = "C:\ruta\al\jdk-25"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$mvn = "C:\ruta\a\maven\bin\mvn.cmd"
& $mvn -version
```

Para usar los comandos Maven mostrados más abajo en PowerShell, reemplaza
`"$MAVEN"` por `& $mvn`.

## Configuración de las bases

Por defecto, ambos adaptadores utilizan la base `persona_db`:

| Motor | Host | Puerto | Usuario de aplicación |
| --- | --- | --- | --- |
| MariaDB | `localhost` | `3307` | `persona_db` |
| MongoDB | `localhost` | `27017` | `persona_db` (autenticación en `admin`) |

Las conexiones se configuran por separado en los archivos
[REST application.properties](rest-input-adapter/src/main/resources/application.properties)
y [CLI application.properties](cli-input-adapter/src/main/resources/application.properties).
Las credenciales incluidas son únicamente para el laboratorio local; no son
adecuadas para producción. Configura contraseñas seguras externamente y no las
subas al repositorio para otros entornos.

### Bases existentes de este laboratorio

Si ya existen los contenedores de este equipo, inicia Docker Desktop y, si están
detenidos, ejecuta:

```bash
docker start personapp-lts-mariadb personapp-lts-mongo
docker ps
```

No ejecutes los scripts de inicialización sobre estas bases sin revisar sus
datos. Los contenedores y volúmenes anteriores están detenidos como respaldo;
no los arranques simultáneamente porque usan los mismos puertos.

### Crear bases locales nuevas con Docker

Esta alternativa es para una instalación **nueva**, con los puertos 3307 y
27017 libres. No la ejecutes si ya estás usando las bases anteriores en esos
puertos. Los siguientes comandos son para **Git Bash**:

```bash
read -rs -p "Contraseña root de MariaDB: " MARIADB_ROOT_PASSWORD; echo
read -rs -p "Contraseña root de MongoDB: " MONGO_INITDB_ROOT_PASSWORD; echo
export MARIADB_ROOT_PASSWORD MONGO_INITDB_ROOT_PASSWORD
export MONGO_INITDB_ROOT_USERNAME=personapp_root

MSYS_NO_PATHCONV=1 docker run -d --name personapp-mariadb -p 127.0.0.1:3307:3306 \
  -e MARIADB_ROOT_PASSWORD -v personapp-mariadb-data:/var/lib/mysql mariadb:12.3
MSYS_NO_PATHCONV=1 docker run -d --name personapp-mongo -p 127.0.0.1:27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME -e MONGO_INITDB_ROOT_PASSWORD \
  -v personapp-mongo-data:/data/db mongo:9.0
unset MARIADB_ROOT_PASSWORD MONGO_INITDB_ROOT_USERNAME MONGO_INITDB_ROOT_PASSWORD
```

Espera a que ambos motores terminen de iniciar; consulta `docker ps` y, si es
necesario, `docker logs personapp-mariadb` y `docker logs personapp-mongo`.
Los volúmenes conservan los datos cuando se detienen los contenedores.

**Solo para bases vacías y de laboratorio**, crea el esquema y el usuario de
aplicación con estos scripts:

```bash
docker exec -i personapp-mariadb sh -c 'exec mariadb -u root -p"$MARIADB_ROOT_PASSWORD"' < "$(cygpath -u 'scripts\persona_ddl_maria.sql')"
docker exec -i personapp-mongo sh -c 'exec mongosh --quiet --username "$MONGO_INITDB_ROOT_USERNAME" --password "$MONGO_INITDB_ROOT_PASSWORD" --authenticationDatabase admin' < "$(cygpath -u 'scripts\persona_ddl_mongo.js')"
```

**Advertencia:** el DDL de MariaDB elimina el esquema y el usuario
`persona_db` antes de crearlos. Ambos DDL crean credenciales de laboratorio;
revísalos y adáptalos antes de usarlos fuera de este entorno.

Para cargar los ejemplos en las bases **vacías**, después de los DDL:

```bash
docker exec -i personapp-mariadb sh -c 'exec mariadb -u root -p"$MARIADB_ROOT_PASSWORD"' < "$(cygpath -u 'scripts\persona_dml_maria.sql')"
docker exec -i personapp-mongo sh -c 'exec mongosh --quiet --username "$MONGO_INITDB_ROOT_USERNAME" --password "$MONGO_INITDB_ROOT_PASSWORD" --authenticationDatabase admin' < "$(cygpath -u 'scripts\persona_dml_mongo.js')"
```

Cada DML contiene cinco registros por entidad. No lo ejecutes completo en una
base con las mismas claves: puede fallar por duplicados o dejar una carga
parcial. Los ejemplos de fechas Mongo están ajustados a Colombia (UTC-5).
Spring Data las lee usando la zona horaria del equipo; ajústalas si se utiliza
otra zona.

## Compilación y pruebas

Desde la raíz, en Git Bash:

```bash
"$MAVEN" test
```

Las pruebas de integración opcionales requieren bases de prueba **aisladas** y
se habilitan con `PERSONAPP_ISOLATED_INTEGRATION_TEST=true` y conexiones
configuradas explícitamente. Crean y eliminan registros: nunca las habilites
contra las bases que contienen datos reales.

Para compilar e instalar los módulos necesarios para REST:

```bash
"$MAVEN" -pl rest-input-adapter -am -DskipTests install
```

Para la CLI:

```bash
"$MAVEN" -pl cli-input-adapter -am -DskipTests install
```

Espera a que el comando correspondiente termine con `BUILD SUCCESS`. Instalar
los módulos en el repositorio local de Maven permite resolver las dependencias
entre módulos al ejecutar cada adaptador por separado.

## Despliegue y ejecución local

Enciende ambas bases y ejecuta estos comandos desde la raíz, en Git Bash, después
de instalar los módulos correspondientes:

**API REST:**

```bash
"$MAVEN" -pl rest-input-adapter spring-boot:run
```

Deja la terminal abierta. La API escucha en `http://localhost:3000`. Abre
[Swagger UI](http://localhost:3000/swagger-ui.html) para consultar y probar
los endpoints; la definición OpenAPI está en
[`/api-docs`](http://localhost:3000/api-docs).

**CLI:** abre otra terminal, configura de nuevo Java y Maven como se indicó
arriba y ejecuta:

```bash
"$MAVEN" -pl cli-input-adapter spring-boot:run
```

El menú aparece en esa terminal. Para detener cualquiera de las aplicaciones,
pulsa `Ctrl+C` en su terminal. REST y CLI son aplicaciones Spring diferentes.

En este equipo existe una configuración local **PersonAPP REST (3000)** en
Ejecutar y depurar. La carpeta `.vscode` está excluida del repositorio, por lo
que otros equipos deben crear su propia configuración. Si el depurador señala
problemas de compilación, revisa sus mensajes y verifica el resultado de Maven.

El repositorio no incluye Dockerfile ni una configuración de despliegue de
producción; los pasos anteriores describen la ejecución local.

## Problemas frecuentes

- **Versión de Java incorrecta:** revisa tanto `java -version` como
  `"$MAVEN" -version` y configura `JAVA_HOME` con el JDK 25.
- **Error de conexión:** verifica que ambos motores estén activos, sus puertos,
  credenciales y las propiedades de los adaptadores.
- **Puerto 3000 ocupado:** detén únicamente la instancia anterior de la API o
  cambia `server.port` en la configuración REST.
- **Swagger no muestra la definición:** prueba una recarga forzada del navegador
  (`Ctrl+Shift+R`) o una ventana privada para descartar recursos en caché.
- **Claves duplicadas al cargar DML:** revisa los registros existentes; no
  ejecutes el DDL destructivo como solución automática.

Este repositorio es una plantilla de laboratorio de arquitectura limpia.
