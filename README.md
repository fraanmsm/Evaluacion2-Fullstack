# Sistema de Triage Clínico  y Gestión Hospitalaria 

Este repositorio corresponde a la evolución y continuidad directa del ecosistema de microservicios desarrollado durante el semestre. 

## Integrantes del equipo: 
* Francisca Saavedra
* Ariel Ulloa
* Ashley Garrido
* José Beas

## Microservicios Implementados: 
1. **àpi-triage`(Microservicio principal):** Encargado de la admisión, clasificación de gravedad (1 a 5), control de flujo clínico de urgencia y el bloqueo de ingresos accidentales para pacientes críticos 
2.  **àpi-camas`:(Microservicio Secundario)** Servicio externo de Gestión de camas hospitalarias consultado en tiempo real de forma síncrona para confirmar cupos antes de confirmar una admisión.

## Rutas principales (`api-triage - Puerto `16800`)
* **`GET /api/v1/triage`** -> Listar todos los pacientes en atención activa en la sala de espera.
* **`POST /api/v1/triage`** -> Registrar la admisión, clasificación de gravedad y activar el flujo de contingencia distribuida.
* **`GET /api/v1/triage/{id}`** -> Buscar un registro de triage específico por su identificador único.
* **`PUT /api/v1/triage/{id}`** -> Actualizar la evolución y el estado del paciente.
* **`DELETE /api/v1/triage/{id}`** -> Dar de alta a un paciente mediante un mecanismo de ocultamiento lógico (Soft Delete).
* **`GET /api/v1/triage/criticos`** -> Vista especializada de monitoreo para pacientes en riesgo máximo (Gravedad 1 y 2).


### Mejoras e implementaciones que se buscaron en esta etapa:
**Automatización con CI/CD (GitHub Actions):** Se diseñó y ejecutó un pipeline automatizado de Integración Continua a través de un archivo `.yml`. Cada vez que se realiza un `Push` o un `Pull Request` a la rama principal, un servidor virtual compila el proyecto con Maven y ejecuta automáticamente todo el set de pruebas unitarias (JUnit/Mockito), garantizando que código defectuoso jamás llegue al entorno de producción.
  
**Mensajería Asíncrona (RabbitMQ / Kafka):** Migración del consumo síncrono actual (`RestTemplate`) hacia una arquitectura dirigida por eventos para eliminar el cuello de botella cuando el servicio de camas experimente latencia o caídas catastróficas.

**Seguridad con Spring Security y JWT:** Incorporación de un microservicio de autenticación (`api-auth`) y filtros en el Gateway para proteger los endpoints públicos mediante tokens encriptados y roles estrictos (ej. Médico Jefe).

**Migraciones de Base de Datos con Flyway/Liquibase:** Automatización del control de versiones del esquema relacional directamente en el ciclo de vida de la aplicación Java, eliminando la ejecución manual de scripts SQL.

##Enlace a la Documentación Swagger: 

La interfaz interactiva de Swagger UI se autogenera en tiempo real al levantar el perfil local de desarrollo:

* **Portal Swagger UI Local:** [http://localhost:16800/swagger-ui/index.html](http://localhost:16800/swagger-ui/index.html)

## Intrucciones para la ejecución local: 
* Docker Desktop instalado y activo.
* IntelliJ IDEA (con soporte para Java y Maven).
* Java Development Kit (JDK) versión 21.

### Paso 1: Levantar el Motor de Base de Datos en Docker
Abra su terminal (PowerShell o CMD) e inicialice el contenedor local aislado de Oracle XE ejecutando:
```bash
docker run -d --name oracle-xe-local -p 1521:1521 -e ORACLE_PASSWORD=PasswordDoce123 gvenzl/oracle-xe

Paso 2: Configurar el Perfil de Arranque en IntelliJ
Abra el proyecto en IntelliJ IDEA y espere a que Maven indexe las dependencias.

Ingrese a la ventana de Run/Debug Configurations.

En las opciones de la máquina virtual (VM Options) de la aplicación, inyecte el parámetro de activación de perfil:
-Dspring.profiles.active=local

Presione Play (Run). La aplicación iniciará leyendo de forma exclusiva el archivo application-local.yaml y apuntará de manera automática a su contenedor de Docker local.







