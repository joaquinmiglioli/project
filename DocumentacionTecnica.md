# Trabajo Práctico Central de Monitoreo Urbano: Documentación técnica

**Materia:** Programación B

**Docentes:** Claudio Gea y Luis Buffoni

**Estudiantes:** Matías Messina, Joaquín Miglioli Flores, Diego Henríquez y Facundo Fontanals

**Fecha de entrega:** 07/11/2025

## Introducción

En el siguiente documento se presenta la arquitectura, el diseño e información técnica del proyecto "Central de Monitoreo Urbano". Este informe detalla la estructura del código, la funcionalidad de las clases y métodos, así como las decisiones de diseño clave que sustentan la aplicación. Cubre desde la interacción entre el front-end y el back-end hasta la gestión de dispositivos, simulaciones de eventos y la persistencia de datos, cumpliendo con todos los requisitos establecidos en la consigna. Esta documentación describe la estructura del código, las clases, métodos y las decisiones de diseño clave del proyecto, cumpliendo con los requisitos de la consigna.

## 1. Relación entre Front-end, Back-end y la API REST

La aplicación sigue una arquitectura moderna de tipo "Cliente-Servidor":

*   **Back-end (El Cerebro):** Es la aplicación Java construida con Spring Boot. Se ejecuta en un servidor y es responsable de toda la lógica de negocio:
    *   Manejar los ciclos de los semáforos.
    *   Simular fallas y violaciones.
    *   Conectarse a la base de datos PostgreSQL para guardar y leer multas, autos, etc.
    *   Generar los reportes.
    *   Crear los PDFs de las multas.
    *   Persistir (guardar) el estado de la aplicación en el archivo `state.bin`.

*   **Front-end (La Interfaz):** Es un único archivo, `index.html`. Este archivo contiene todo el HTML (la estructura), el CSS (los estilos) y el JavaScript (la lógica) que se ejecuta en el navegador del usuario. Utiliza la librería Leaflet.js para mostrar el mapa interactivo.

*   **API REST (El Puente):** Es el conjunto de "endpoints" (URLs) que el back-end expone para que el Front-end pueda comunicarse con él. Esta es la "API" (Application Programming Interface).
    *   **Ejemplo:** El JavaScript del `index.html` hace una llamada `fetch` a la URL `/api/devices`.
    *   El back-end recibe esta llamada en su `DeviceController`.
    *   El controlador prepara los datos del dispositivo (en formato JSON) y se los devuelve al Front-end.
    *   El JavaScript recibe ese JSON y lo usa para dibujar los íconos en el mapa.

Con este diseño se desacopla el Front-end del back-end.

## 2. Especificación de Clases por Paquete

A continuación se detalla la función de cada paquete y clase.

### Paquete `com.example.demo` (Raíz)

*   **`MapWebApplication.java`:** Es el punto de entrada de la aplicación Spring Boot. El método `main` inicia el servidor.
*   **`appContext()`:** Crea un Bean de `AppContext`, que actuará como el coordinador central de toda la aplicación.
*   **`onExit()`:** Se ejecuta al cerrar la aplicación. Llama a `ctx.saveOnExit()` para guardar el estado actual en `state.bin`, cumpliendo el requisito de persistencia.

### Paquete `com.example.demo.controllers`

Contiene las clases que definen la API REST. Son la puerta de entrada desde el Front-end hacia el back-end.

*   **`CarManagementController.java`:** Maneja las peticiones de la pestaña "Settings" para el CRUD (Crear, Leer, Actualizar, Borrar) de autos. Expone endpoints como `/api/cars/brands` y `/api/cars/add`.
*   **`DeviceController.java`:** Es el controlador principal para el mapa.
    *   `grouped()`: Responde a `GET /api/devices`. Prepara un JSON con todos los dispositivos agrupados por tipo.
    *   `setStatus()`, `repair()`, `fail()`, `intermittent()`: Exponen los endpoints para el mantenimiento de dispositivos (ej. `/api/devices/{id}/repair`).
*   **`FineNotificationController.java`:** Implementa un sistema de "polling" para notificar al Front-end sobre nuevas multas.
    *   `updateLastFine()`: Es llamado por el back-end cuando se genera una multa.
    *   `getLastFine()`: Es llamado por el Front-end periódicamente para consultar si hay una multa nueva.
*   **`FinesController.java`:** Maneja la API para la pestaña "Fines".
    *   `listAll()`: Responde a `GET /api/fines` y devuelve todas las multas.
    *   `deleteAll()`: Responde a `DELETE /api/fines` y borra todas las multas.
*   **`ReportController.java`:** Expone los 5 reportes como endpoints (ej. `GET /api/reports/device-status`). Es un intermediario que llama al `ReportService`.
*   **`SecurityOpsController.java`:** Maneja las operaciones de las cámaras de seguridad.
    *   `notifyService()`: Responde a `POST /api/security/notify` cuando el operador presiona "Policía", "Bomberos", etc.
    *   `log()`: Responde a `GET /api/security/log` y devuelve la lista de avisos para la pestaña "Logs".
*   **`SystemController.java`:** Expone funciones administrativas.
    *   `resetState()`: Responde a `POST /api/system/reset`. Borra `state.bin` y reinicia la aplicación para volver al estado inicial de `devices.json`.
*   **`TrafficLightApiController.java`:** Controlador optimizado para el estado de los semáforos.
    *   `getTrafficLights()`: Responde a `GET /api/trafficlights`. Es consultado constantemente (cada 800ms) para animar las luces en el mapa.

### Paquete `com.example.demo.core`

Es el corazón de la aplicación. Se encarga de la lógica principal, los simuladores y el estado.

*   **`AppContext.java`:** La clase iniciadora central. Inicializa todos los servicios, DAOs, y arranca los simuladores y el ciclo de semáforos. Implementa `IMaintenanceContext` para el polimorfismo de mantenimiento.
*   **`BootstrapLoader.java`:** Clase utilitaria que lee `devices.json` para rellenar `CentralState` con los dispositivos iniciales.
*   **`CentralState.java`:** Contiene todo el estado vivo de la aplicación (lista de dispositivos, etc.). Es `Serializable` para poder guardarse en `state.bin`.
*   **`DeviceFailureSimulator.java`:** Un hilo que simula fallos de dispositivos al azar.
*   **`StatePersistenceService.java`:** Maneja la persistencia del estado.
    *   `save()`: Serializa `CentralState` a `state.bin`.
    *   `loadOrBootstrap()`: Intenta leer `state.bin` o carga el estado inicial desde `devices.json`.
*   **`ViolationCoordinator.java`:** Se activa cuando se detecta una violación (usando el patrón Observer). Coordina la creación de la multa, la generación del PDF y la notificación al Front-end.
*   **`ViolationSimulator.java`:** Otro hilo que simula la ocurrencia de infracciones.

### Paquete `com.example.demo.exceptions`

Define excepciones personalizadas para un mejor manejo de errores.

*   **`MonitoringException.java`:** Clase base para todas las excepciones del proyecto.
*   **`DatabaseOperationException.java`:** Para errores de SQL.
*   **`DuplicateResourceException.java`:** Para recursos duplicados (ej. una patente).
*   **`ResourceNotFoundException.java`:** Cuando no se encuentra un recurso por ID.

### Paquete `com.example.demo.reports`

Contiene las estructuras de datos (records) para los reportes que se envían como JSON al Front-end.

*   `DeviceStatusReport.java`
*   `FinesReport.java`
*   `SecurityLogsReport.java`
*   `DeviceEventsReport.java`

### Paquete `com.example.demo.runtime`

Clases que conectan el estado simple (`CentralState`) con los objetos de dominio con lógica.

*   **`DeviceFactory.java`:** Fabrica los objetos de dispositivo (Radar, TrafficLightController, etc.) basándose en `CentralState`.
*   **`DeviceCatalog.java`:** Almacena la lista de todos los objetos de dispositivo fabricados.
*   **`SnapshotSync.java`:** Sincroniza los cambios desde los objetos de `DeviceCatalog` hacia `CentralState` para mantener un único punto de verdad y evitar problemas de concurrencia.

### Paquete `com.example.demo.services`

Implementa la lógica de negocio principal del back-end.

*   **`PdfGenerator.java`:** Usa Apache PDFBox para crear un PDF a partir de un objeto `Fine`.
*   **`ReportService.java`:** Contiene la lógica para generar cada uno de los 5 reportes.
*   **`TrafficLightCycleService.java`:** Maneja el ciclo de los semáforos usando un hilo (`ScheduledExecutorService`) para actualizar su estado periódicamente.
*   **`ViolationService.java`:** Mantiene una `ObservableList` de violaciones, permitiendo que `ViolationCoordinator` se suscriba a los cambios (Patrón Observer).

### Paquete `cars`

Modela el dominio de los autos.

*   `Car.java`
*   `CarBrand.java`
*   `CarModel.java`
*   `CarService.java`

### Paquete `db`

Encapsula toda la lógica de SQL, ocultándola del resto de la aplicación.

*   **`DBConnection.java`:** Provee la conexión a la base de datos PostgreSQL.
*   **`CarBrandDAO.java`:** Métodos CRUD para la tabla `carbrands`.
*   **`CarModelDAO.java`:** Métodos CRUD para la tabla `carmodels`.
*   **`CarDAO.java`:** Métodos CRUD para la tabla `cars`.
*   **`FineDAO.java`:** Métodos para la tabla `fines`, incluyendo la inserción y la búsqueda por patente.

### Paquete `devices`

El "Modelo de Dominio" que define los objetos de negocio principales.

*   **`Device.java`:** Clase base abstracta de la que heredan `Radar`, `ParkingCamera`, etc. (Herencia). Define métodos polimórficos como `fail()` y `repair()`.
*   **`TrafficLightController.java`:** Sobrescribe `fail` y `repair` para pausar y reanudar el ciclo del semáforo usando el `IMaintenanceContext` (Polimorfismo).
*   **`IMaintenanceContext.java`:** Una Interfaz que define el contrato para pausar/reanudar semáforos, reduciendo el acoplamiento (Abstracción).
*   **`DeviceStatus.java`, `ServiceType.java`, `TrafficLightStatus.java`:** Enums para constantes seguras.
*   `Photo.java`
*   `SecurityWarning.java`
*   `TrafficLight.java`

### Paquete `fines`

El Modelo de Dominio para las multas.

*   **`Fine.java`:** Clase base abstracta con el método `abstract void compute()`, forzando a las subclases a implementar su propia lógica de cálculo (Polimorfismo y Abstracción).
*   **`SpeedingFine.java`, `ParkingFine.java`, `RedLightFine.java`:** Heredan de `Fine` e implementan `compute()` con su lógica específica.
*   **`FineIssuer.java`:** Interfaz que define el contrato `issue()`.
*   **`SimpleFineIssuer.java`:** Implementa `FineIssuer`. Su método `issue()` crea la subclase de `Fine` correcta y llama a `compute()` sin necesidad de conocer los detalles de cada tipo de multa.
*   **`FineType.java`:** Enum para los tipos de multas.

## Otros Archivos Relevantes

*   **`pom.xml`:** Define las dependencias del proyecto (Spring Boot, PostgreSQL, PDFBox, JavaFX) y la configuración de Maven.
*   **`monitoringcenter.sql`:** Script SQL para crear la estructura de la base de datos.
*   **`src/main/resources/static/devices.json`:** Archivo de configuración inicial de dispositivos.
*   **`src/main/resources/static/index.html`:** El Front-end completo de la aplicación.
*   **Archivos en `src/main/resources/static/images/`:** Íconos del mapa y fotos de evidencia.
