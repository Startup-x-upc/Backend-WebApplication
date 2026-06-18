# Análisis de Arquitectura del Proyecto `learning-center-platform`

> **Documento reusable** — Esta guía documenta la arquitectura, patrones de diseño y estructura del proyecto. Puede usarse como referencia para iniciar nuevos proyectos o agregar nuevos Bounded Contexts siguiendo rigurosamente las convenciones de diseño del proyecto.

---

## Tabla de Contenidos

1. [Visión General](#1-visión-general)
2. [Estructura de Carpetas](#2-estructura-de-carpetas)
3. [Arquitectura: DDD + Puertos y Adaptadores (Hexagonal Pura)](#3-arquitectura-ddd--puertos-y-adaptadores-hexagonal-pura)
4. [El Módulo `shared` — Contrato del Shared Kernel](#4-el-módulo-shared--contrato-del-shared-kernel)
5. [El Módulo `profiles` — Bounded Context de Referencia](#5-el-módulo-profiles--bounded-context-de-referencia)
6. [El Módulo `learning` — Bounded Context Principal](#6-el-módulo-learning--bounded-context-principal)
7. [Flujo de Dependencias e Integración (ACL y Eventos)](#7-flujo-de-dependencias-e-integración-acl-y-eventos)
8. [Patrones de Diseño Identificados](#8-patrones-de-diseño-identificados)
9. [Cómo Replicar Esta Estructura en un Bounded Context Nuevo](#9-cómo-replicar-esta-estructura-en-un-bounded-context-nuevo)
10. [Checklist de Reglas](#10-checklist-de-reglas)
11. [Errores Comunes y Advertencias](#11-errores-comunes-y-advertencias)

---

## 1. Visión General

| Dimensión        | Detalle                                                    |
| ---------------- | ---------------------------------------------------------- |
| **Lenguaje**     | Java 26                                                    |
| **Framework**    | Spring Boot 4.0.6                                          |
| **Build tool**   | Maven                                                      |
| **Persistencia** | Spring Data JPA (Hibernate) + MySQL                        |
| **API**          | REST (Spring Web) + OpenAPI 3 / Swagger (Springdoc 3.0.3) |
| **Validación**   | Jakarta Bean Validation                                    |
| **Utilidades**   | Lombok, JSpecify, Pluralize                                |

El proyecto implementa un **monolito modular** estructurado por **Bounded Contexts** de Domain-Driven Design (DDD). A diferencia de arquitecturas tradicionales acopladas a la base de datos, este proyecto implementa **Arquitectura Hexagonal Pura (Puertos y Adaptadores)**. Las clases del dominio son Java puro y no contienen anotaciones de JPA (`@Entity`, `@Table`, etc.). El código de infraestructura de persistencia se mantiene aislado y mapeado a través de adaptadores y convertidores de datos.

---

## 2. Estructura de Carpetas

La raíz del paquete del proyecto es `com.acme.learning.center.platform`.

```
src/
├── main/
│   ├── java/com/acme/learning/center/platform/
│   │   ├── LearningCenterPlatformApplication.java               ← Punto de entrada Spring Boot (@EnableJpaAuditing)
│   │   │
│   │   ├── iam/                                                 ← Bounded Context: IAM (en desarrollo)
│   │   │
│   │   ├── learning/                                            ← Bounded Context: Learning
│   │   │   ├── domain/
│   │   │   │   ├── exceptions/
│   │   │   │   │   ├── CourseNotFoundException.java             ← Excepción de dominio
│   │   │   │   │   ├── EnrollmentNotFoundException.java
│   │   │   │   │   ├── EnrollmentRequestException.java
│   │   │   │   │   └── StudentNotFoundException.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── aggregates/
│   │   │   │   │   │   ├── Course.java                         ← Agregado: Curso con LearningPath
│   │   │   │   │   │   ├── Enrollment.java                     ← Agregado: Matrícula con máquina de estados
│   │   │   │   │   │   └── Student.java                        ← Agregado: Estudiante con métricas
│   │   │   │   │   ├── commands/                               ← 13 Command Objects (records)
│   │   │   │   │   ├── entities/
│   │   │   │   │   │   ├── LearningPathItem.java               ← Entidad subordinada (nodo de linked list)
│   │   │   │   │   │   └── ProgressRecordItem.java             ← Entidad subordinada (ítem de progreso)
│   │   │   │   │   ├── events/
│   │   │   │   │   │   └── TutorialCompletedEvent.java         ← Evento de dominio (extiende ApplicationEvent)
│   │   │   │   │   ├── queries/                                ← 10 Query Objects (records)
│   │   │   │   │   └── valueobjects/
│   │   │   │   │       ├── AcmeStudentRecordId.java            ← VO: UUID generado automáticamente
│   │   │   │   │       ├── EnrollmentStatus.java               ← Enum: REQUESTED/CONFIRMED/REJECTED/CANCELLED
│   │   │   │   │       ├── LearningPath.java                   ← VO con colección: linked list de ítems
│   │   │   │   │       ├── ProfileId.java                      ← VO: referencia ID al agregado Profile
│   │   │   │   │       ├── ProgressRecord.java                 ← VO con colección: registro de progreso
│   │   │   │   │       ├── ProgressStatus.java                 ← Enum: NOT_STARTED/STARTED/COMPLETED
│   │   │   │   │       ├── StudentPerformanceMetricSet.java     ← VO inmutable: métricas del estudiante
│   │   │   │   │       └── TutorialId.java                     ← VO: identificador de tutorial externo
│   │   │   │   └── repositories/
│   │   │   │       ├── CourseRepository.java                   ← PUERTO: Repositorio de Curso
│   │   │   │       ├── EnrollmentRepository.java               ← PUERTO: Repositorio de Matrícula
│   │   │   │       └── StudentRepository.java                  ← PUERTO: Repositorio de Estudiante
│   │   │   └── infrastructure/
│   │   │       └── persistence/jpa/
│   │   │           ├── assemblers/
│   │   │           │   ├── CoursePersistenceAssembler.java     ← Mapeador con reconstitución de linked list
│   │   │           │   ├── EnrollmentPersistenceAssembler.java
│   │   │           │   └── StudentPersistenceAssembler.java
│   │   │           ├── converters/
│   │   │           │   ├── AcmeStudentRecordIdPersistenceConverter.java
│   │   │           │   └── ProfileIdPersistenceConverter.java
│   │   │           └── repositories/
│   │   │               ├── CoursePersistenceRepository.java
│   │   │               ├── EnrollmentPersistenceRepository.java
│   │   │               └── StudentPersistenceRepository.java
│   │   │
│   │   ├── profiles/                                            ← Bounded Context: Profiles
│   │   │   ├── application/
│   │   │   │   ├── acl/
│   │   │   │   │   └── ProfilesContextFacadeImpl.java           ← Impl de la Fachada ACL
│   │   │   │   ├── commandservices/
│   │   │   │   │   └── ProfileCommandService.java               ← Interfaz de Casos de Uso de Escritura
│   │   │   │   ├── internal/
│   │   │   │   │   ├── commandservices/
│   │   │   │   │   │   └── ProfileCommandServiceImpl.java       ← Impl del Servicio de Comandos
│   │   │   │   │   ├── eventhandlers/
│   │   │   │   │   │   └── ProfileCreatedEventHandler.java      ← Manejador de eventos locales
│   │   │   │   │   └── queryservices/
│   │   │   │   │       └── ProfileQueryServiceImpl.java         ← Impl del Servicio de Consultas
│   │   │   │   └── queryservices/
│   │   │   │       └── ProfileQueryService.java                 ← Interfaz de Casos de Uso de Lectura
│   │   │   │
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── aggregates/
│   │   │   │   │   │   └── Profile.java                         ← AGREGADO (Java puro, sin JPA)
│   │   │   │   │   ├── commands/
│   │   │   │   │   │   └── CreateProfileCommand.java            ← Command Object (Record inmutable)
│   │   │   │   │   ├── events/
│   │   │   │   │   │   └── ProfileCreatedEvent.java             ← Evento de Dominio (record puro)
│   │   │   │   │   ├── queries/
│   │   │   │   │   │   ├── GetAllProfilesQuery.java             ← Query Objects
│   │   │   │   │   │   ├── GetProfileByEmailQuery.java
│   │   │   │   │   │   └── GetProfileByIdQuery.java
│   │   │   │   │   └── valueobjects/
│   │   │   │   │       ├── EmailAddress.java                    ← Value Objects del Dominio (Records)
│   │   │   │   │       ├── PersonName.java
│   │   │   │   │       └── StreetAddress.java
│   │   │   │   └── repositories/
│   │   │   │       └── ProfileRepository.java                   ← PUERTO: Interfaz de Repositorio de Dominio
│   │   │   │
│   │   │   ├── infrastructure/
│   │   │   │   └── persistence/jpa/
│   │   │   │       ├── adapters/
│   │   │   │       │   └── ProfileRepositoryImpl.java           ← ADAPTADOR: Impl de persistencia de dominio
│   │   │   │       ├── assemblers/
│   │   │   │       │   └── ProfilePersistenceAssembler.java     ← Mapeador Dominio ↔ Persistencia
│   │   │   │       ├── converters/
│   │   │   │       │   └── EmailAddressPersistenceConverter.java ← AttributeConverter JPA
│   │   │   │       ├── embeddables/
│   │   │   │       │   ├── PersonNamePersistenceEmbeddable.java  ← Embeddables JPA
│   │   │   │       │   └── StreetAddressPersistenceEmbeddable.java
│   │   │   │       ├── entities/
│   │   │   │       │   └── ProfilePersistenceEntity.java        ← Entidad JPA (Infraestructura)
│   │   │   │       └── repositories/
│   │   │   │           └── ProfilePersistenceRepository.java    ← Spring Data JPA Repository
│   │   │   │
│   │   │   └── interfaces/
│   │   │       ├── acl/
│   │   │       │   └── ProfilesContextFacade.java               ← Puerto ACL para otros contextos
│   │   │       ├── events/
│   │   │       │   └── ProfileCreatedIntegrationEvent.java      ← Evento de Integración (público)
│   │   │       └── rest/
│   │   │           ├── ProfilesController.java                  ← REST Controller
│   │   │           ├── resources/
│   │   │           │   ├── CreateProfileResource.java           ← DTO de Entrada
│   │   │           │   └── ProfileResource.java                 ← DTO de Salida
│   │   │           └── transform/
│   │   │               ├── CreateProfileCommandFromResourceAssembler.java
│   │   │               └── ProfileResourceFromEntityAssembler.java
│   │   │
│   │   └── shared/                                              ← Módulo Shared Kernel (Código Común)
│   │       ├── application/
│   │       │   └── result/
│   │       │       ├── ApplicationError.java                    ← Errores estructurados i18n
│   │       │       └── Result.java                              ← Mónada Result<T, E> funcional
│   │       ├── domain/
│   │       │   └── model/
│   │       │       └── aggregates/
│   │       │           └── AbstractDomainAggregateRoot.java     ← Clase base para agregados sin JPA
│   │       ├── infrastructure/
│   │       │   ├── documentation/openapi/configuration/
│   │       │   │   └── OpenApiConfiguration.java                ← Configuración de Swagger/OpenAPI
│   │       │   ├── i18n/configuration/
│   │       │   │   └── LocaleConfiguration.java                 ← Configuración i18n
│   │       │   └── persistence/jpa/
│   │       │       ├── configuration/strategy/
│   │       │       │   └── SnakeCaseWithPluralizedTablePhysicalNamingStrategy.java ← Naming Strategy
│   │       │       └── entities/
│   │       │           └── AuditableAbstractPersistenceEntity.java ← Entidad base auditable JPA
│   │       └── interfaces/
│   │           └── rest/
│   │               ├── GlobalExceptionHandler.java              ← Manejo centralizado de excepciones
│   │               ├── resources/
│   │               │   ├── ErrorResource.java
│   │               │   └── MessageResource.java
│   │               └── transform/
│   │                   ├── ErrorResponseAssembler.java
│   │                   └── ResponseEntityAssembler.java         ← Utilidad de conversión Result ↔ ResponseEntity
│   │
│   └── resources/
│       ├── application.properties                               ← Configuración general (perfil por defecto: dev)
│       ├── application-dev.properties                           ← Configuración de desarrollo (DDL update)
│       ├── application-prod.properties                          ← Configuración de producción
│       ├── messages.properties                                  ← Mensajes traducidos (Inglés por defecto)
│       ├── messages_es.properties                               ← Mensajes traducidos (Español)
│       ├── ValidationMessages.properties                        ← Validaciones de campos (Inglés)
│       └── ValidationMessages_es.properties                     ← Validaciones de campos (Español)
```

---

## 3. Arquitectura: DDD + Puertos y Adaptadores (Hexagonal Pura)

Este proyecto está diseñado bajo los principios de la **Arquitectura Hexagonal Pura**, logrando el desacoplamiento total del modelo de negocio de los detalles técnicos (como la base de datos o frameworks).

```
   [ Interfaces REST ] ───> [ Application Services (Comandos/Consultas) ]
                                            │
                                            ▼
                                   [ Domain aggregates ]
                                            │
                                            ▼
   [ JPA Entities ] <─── [ Pers. Adapters ] ───> [ Domain Repository Interfaces ]
```

### 3.1 Agregados de Dominio Puros

A diferencia de implementaciones clásicas de Spring, los agregados del dominio (como `Profile.java`) son clases puras de Java.

- **Sin `@Entity` ni `@Table`**: No contienen anotaciones de persistencia.
- **Herencia limpia**: Heredan de `AbstractDomainAggregateRoot.java` de la capa `shared/domain`, la cual delega a la clase de Spring Data Commons `AbstractAggregateRoot` para la gestión local de eventos, sin importar dependencias de JPA.
- **Constructores e inmutabilidad**: Se construyen a través de constructores explícitos que reciben Commands u objetos del dominio, validando que el estado inicial cumpla con las reglas del negocio.
- **Setter de `id`**: Los agregados exponen un setter público para `id` exclusivamente para que el assembler de persistencia asigne la identidad generada por JPA tras el `save`. No debe invocarse en lógica de dominio; el identificador es asignado una única vez por la capa de infraestructura.

### 3.2 Infraestructura de Persistencia Desacoplada

El acoplamiento a base de datos e Hibernate se confina completamente en `infrastructure/persistence/jpa/`:

- **Entidades de Persistencia**: `ProfilePersistenceEntity.java` contiene todas las anotaciones de base de datos (`@Entity`, `@Table`, `@Column`, `@Embedded`, `@AttributeOverrides`, `@Convert`). Estas heredan de `AuditableAbstractPersistenceEntity.java` que gestiona los campos de auditoría (`createdAt`, `updatedAt`) de forma transparente para el dominio.
- **Embeddables**: Objetos como `PersonNamePersistenceEmbeddable.java` encapsulan el mapeo de base de datos para Value Objects estructurados.
- **Attribute Converters**: Se utilizan para convertir Value Objects atómicos a tipos primitivos nativos de la base de datos (por ejemplo, `EmailAddressPersistenceConverter.java`).
- **Mapeo Bilateral (Assemblers)**: `ProfilePersistenceAssembler.java` contiene lógica con funciones puras `static` para mapear los objetos bidireccionalmente:
  - `toDomainFromPersistence(ProfilePersistenceEntity entity)`
  - `toPersistenceFromDomain(Profile profile)`

### 3.3 Repositorio Hexagonal

- **El Puerto (Dominio)**: `ProfileRepository.java` es una interfaz de Java pura que trabaja únicamente con tipos y entidades de dominio.
- **El Repositorio Spring Data**: `ProfilePersistenceRepository.java` extiende `JpaRepository` y opera sobre la entidad de persistencia de infraestructura.
- **El Adaptador**: `ProfileRepositoryImpl.java` implementa la interfaz del dominio. Sus responsabilidades son:
  1. Inyectar el repositorio de Spring Data.
  2. Inyectar `ApplicationEventPublisher`.
  3. Mapear de Dominio a Persistencia al guardar, y de Persistencia a Dominio al leer.
  4. Publicar manualmente los eventos de dominio acumulados en el agregado tras persistir con éxito.

> **¿Por qué publicación manual en vez de `@DomainEvents` nativo de Spring Data?**
> Spring Data puede publicar eventos automáticamente si el agregado declara `@DomainEvents` y `@AfterDomainEventPublication`. Se eligió la publicación manual explícita porque: (a) mantiene el dominio libre de anotaciones de Spring en los agregados, (b) da control preciso sobre el momento en que se llama a `onCreated()` y se recopilan los eventos, y (c) es más legible para quien implementa un contexto nuevo sin conocimiento previo de Spring Data internals.

---

## 4. El Módulo `shared` — Contrato del Shared Kernel

El paquete `shared` contiene utilidades transversales libres de acoplamiento a contextos específicos del negocio.

### 4.1 Inventario Completo de `shared/`

| Clase / Componente | Capa | Rol |
| :--- | :--- | :--- |
| `AbstractDomainAggregateRoot<T>` | `domain/model/aggregates/` | Clase base de todos los agregados del proyecto. Delega a `AbstractAggregateRoot` de Spring Data Commons para gestión de eventos de dominio sin traer dependencias JPA al modelo. |
| `Result<T, E>` | `application/result/` | Mónada funcional `sealed interface` con variantes `Success` y `Failure`. Usada en servicios de comando para comunicar resultado o error de forma tipada y sin excepciones. |
| `ApplicationError` | `application/result/` | `record` de error estructurado con `code`, `message` y `details`. Provee fábricas semánticas: `notFound`, `conflict`, `validationError`, `businessRuleViolation`, `unexpected`. |
| `SnakeCaseWithPluralizedTablePhysicalNamingStrategy` | `infrastructure/persistence/jpa/.../strategy/` | Implementa `PhysicalNamingStrategy` de Hibernate. Convierte todos los nombres de columna a `snake_case` y todos los nombres de tabla a `snake_case` en plural. |
| `AuditableAbstractPersistenceEntity` | `infrastructure/persistence/jpa/entities/` | Clase base JPA (`@MappedSuperclass`) con campos `id` (autoincremental), `createdAt` y `updatedAt`. Requiere `@EnableJpaAuditing` en la clase principal de la aplicación. |
| `OpenApiConfiguration` | `infrastructure/documentation/openapi/` | Registra el bean `OpenAPI` de Springdoc usando título, descripción y versión leídos de `application.properties`. |
| `LocaleConfiguration` | `infrastructure/i18n/` | Registra `AcceptHeaderLocaleResolver` con soporte para `en` y `es` según el header `Accept-Language`. |
| `GlobalExceptionHandler` | `interfaces/rest/` | `@RestControllerAdvice` que intercepta `MethodArgumentNotValidException`, `IllegalArgumentException`, `RuntimeException` y `Exception`, traduciéndolos a `ErrorResource` con el HTTP status correspondiente. |
| `ErrorResource` | `interfaces/rest/resources/` | DTO de salida para errores HTTP: `code`, `message`, `details`. |
| `MessageResource` | `interfaces/rest/resources/` | DTO genérico de mensaje textual simple. |
| `ErrorResponseAssembler` | `interfaces/rest/transform/` | Convierte un `ApplicationError` a `ResponseEntity<ErrorResource>`. Mapea el código de error al HTTP status correcto y localiza el mensaje mediante `messages.properties`. |
| `ResponseEntityAssembler` | `interfaces/rest/transform/` | Convierte un `Result<T, ApplicationError>` a `ResponseEntity<?>` aplicando un assembler de recurso al valor exitoso. |

### 4.2 Regla de Membresía en el Shared Kernel

Un artefacto **pertenece a `shared/`** si cumple los tres criterios simultáneamente:

1. **Sin lógica de negocio**: No contiene reglas, validaciones ni conceptos de ningún Bounded Context específico.
2. **Uso transversal**: Es utilizado (o es razonablemente esperable que sea utilizado) por más de un Bounded Context.
3. **Infraestructura técnica**: Es un patrón funcional, configuración de framework, estrategia de mapeo o utilidad REST que cualquier contexto necesitaría de forma idéntica.

**Lo que NO debe ir en `shared/`:**
- DTOs (Resources) de un contexto específico.
- Excepciones de dominio de un contexto (`EnrollmentNotFoundException`, etc.).
- Assemblers de persistencia de un contexto.
- Lógica de negocio de cualquier tipo.

### 4.3 Consecuencias de Violar el Shared Kernel

| Violación | Consecuencia |
| :--- | :--- |
| Agregar lógica de negocio de un contexto en `shared/` | Todos los contextos quedan acoplados a ese negocio, imposibilitando extraer el contexto a un microservicio futuro. |
| Dos contextos modifican `shared/` para sus propias necesidades | Se crea un cuello de botella de cambios y conflictos de merge. |
| Un artefacto de `shared/` importa clases de un Bounded Context | La dirección de dependencia se invierte; `shared/` debe ser agnóstico a cualquier contexto. |

### 4.4 La Mónada `Result<T, E>` Funcional

Ubicación: `shared/application/result/Result.java`

Implementa una estructura de tipo mónada (similar a `Either` en programación funcional) a través de una `sealed interface` de Java.

- **Records internos**: `Success` y `Failure` aseguran inmutabilidad y seguridad de tipos.
- **Métodos Funcionales**:
  - `map` y `mapError` para transformaciones lineales sin salir del flujo.
  - `flatMap` para encadenamiento secuencial de operaciones propensas a fallar.
  - `recover` para recuperación o fallbacks ante fallos.
  - `fold` para evaluar de forma exhaustiva ambos caminos en el extremo del flujo.
  - `toOptional()` y `getOrElse(T defaultValue)`.

### 4.5 Errores de Aplicación Estandarizados (`ApplicationError`)

Ubicación: `shared/application/result/ApplicationError.java`

Es un Java `record` que representa fallos en la capa de aplicación con una estructura clara y unificada:

- `code`: Un String semántico identificador del tipo de error (e.g., `PROFILE_NOT_FOUND`, `PROFILE_CONFLICT`).
- `message`: El mensaje legible de error por defecto.
- `details`: Información o mensajes contextuales detallados.

Ofrece fábricas estáticas semánticas para los tipos de errores más comunes:

- `validationError(String fieldOrConcept, String reason)` (HTTP 400)
- `notFound(String resourceType, String identifier)` (HTTP 404)
- `businessRuleViolation(String rule, String reason)` (HTTP 422)
- `conflict(String resource, String reason)` (HTTP 409)
- `unexpected(String context, String reason)` (HTTP 500)

### 4.6 Pluralización Automática de Base de Datos

Ubicación: `shared/infrastructure/persistence/jpa/configuration/strategy/SnakeCaseWithPluralizedTablePhysicalNamingStrategy.java`

Implementa `PhysicalNamingStrategy` de Hibernate y utiliza la dependencia `Pluralize`:

- **Nombres de columnas y campos**: Se traducen automáticamente a `snake_case`.
- **Nombres de tablas**: Se traducen a plural y `snake_case` (e.g. la entidad `ProfilePersistenceEntity` se mapea a la tabla `profiles` automáticamente).

> **Advertencia sobre entidades con nombre compuesto**: La estrategia pluraliza el nombre *completo* de la clase antes de convertir a snake_case. Por ejemplo, `LearningPathItemPersistenceEntity` produciría `learning_path_item_persistence_entities`, no `learning_path_items`. Las entidades cuyo nombre pluralizado automáticamente no sea el nombre de tabla deseado **deben declarar `@Table(name = "...")` explícito** para sobreescribir el resultado.

### 4.7 Manejo Global de Excepciones e Internacionalización (i18n)

El framework implementa localización nativa de mensajes de error a nivel de HTTP REST:

- **`GlobalExceptionHandler`**: Intercepta excepciones de validación de Spring (`MethodArgumentNotValidException`), errores de argumentos incorrectos (`IllegalArgumentException`) y excepciones generales, traduciéndolas a una respuesta HTTP que transporta un `ErrorResource`. Captura en orden: `MethodArgumentNotValidException` → `IllegalArgumentException` → `RuntimeException` → `Exception`.
- **`ErrorResponseAssembler`**: Traduce un `ApplicationError` a un `ResponseEntity<ErrorResource>`. Determina el HTTP status a partir del código de error (`_NOT_FOUND` → 404, `_CONFLICT` → 409, `VALIDATION_ERROR` → 400, `BUSINESS_RULE_VIOLATION` → 422, `UNEXPECTED_ERROR` → 500).
- **Locale Context**: Utiliza `LocaleContextHolder.getLocale()` y lee los archivos de recursos `messages.properties` y `messages_es.properties` para localizar los mensajes de error basándose en el header `Accept-Language` de la solicitud HTTP.

---

## 5. El Módulo `profiles` — Bounded Context de Referencia

A continuación, se detalla la implementación del contexto `profiles`, el cual sirve de referencia para todo el proyecto.

### 5.1 Agregado de Dominio: `Profile.java`

```java
public class Profile extends AbstractDomainAggregateRoot<Profile> {
    @Getter @Setter
    private Long id;        // Setter solo para uso del assembler de persistencia

    @Getter
    private PersonName name;
    private EmailAddress emailAddress;
    private StreetAddress streetAddress;

    // Constructor de reconstitución desde persistencia (recibe tipos de dominio)
    public Profile(Long id, PersonName name, EmailAddress emailAddress, StreetAddress streetAddress) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.emailAddress = Objects.requireNonNull(emailAddress, "emailAddress must not be null");
        this.streetAddress = Objects.requireNonNull(streetAddress, "streetAddress must not be null");
    }

    // Constructor de creación (sin id; JPA lo asignará tras persistir)
    public Profile(PersonName name, EmailAddress emailAddress, StreetAddress streetAddress) {
        this(null, name, emailAddress, streetAddress);
    }

    // Constructor de creación desde primitivos (delega al anterior)
    public Profile(String firstName, String lastName, String email,
                   String street, String number, String city, String postalCode, String country) {
        this(new PersonName(firstName, lastName),
             new EmailAddress(email),
             new StreetAddress(street, number, city, postalCode, country));
    }

    // Constructor de caso de uso (recibe Command del dominio)
    public Profile(CreateProfileCommand command) {
        this(command.firstName(), command.lastName(), command.email(),
             command.street(), command.number(), command.city(),
             command.postalCode(), command.country());
    }

    // Invocado por el adaptador tras persistir; registra el evento de dominio
    public void onCreated() { registerDomainEvent(ProfileCreatedEvent.from(this)); }

    // Getters de encapsulamiento (exponen primitivos, no los VOs internos)
    public String getEmailAddress() { return emailAddress.address(); }
    public String getStreetAddress() { return streetAddress.getStreetAddress(); }
    public String getFullName()      { return name.getFullName(); }
}
```

### 5.2 Value Object Atómico: `EmailAddress.java`

Los Value Objects *atómicos* se implementan como `record` de Java con validaciones en el constructor compacto:

```java
public record EmailAddress(@Email String address) {
    public EmailAddress {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Email address must not be null or blank");
        }
    }
}
```

El mismo patrón aplica para `PersonName` (valida `firstName` y `lastName`) y `StreetAddress` (valida `street`, `city`, `postalCode`, `country`; el campo `number` es opcional).

### 5.3 Servicio de Comandos: `ProfileCommandServiceImpl.java`

```java
@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {
    private final ProfileRepository profileRepository;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Result<Profile, ApplicationError> handle(CreateProfileCommand command) {
        try {
            var emailAddress = new EmailAddress(command.email());
            if (profileRepository.existsByEmailAddress(emailAddress)) {
                return Result.failure(ApplicationError.conflict("Profile",
                        "A profile with email address '%s' already exists".formatted(command.email())));
            }
            var profile = new Profile(command);
            var savedProfile = profileRepository.save(profile);
            return Result.success(savedProfile);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Profile", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Profile creation", e.getMessage()));
        }
    }
}
```

> **¿Por qué los comandos retornan `Result` pero las consultas retornan `Optional`?**
> Los comandos pueden fallar por múltiples razones de negocio (conflicto, validación, error inesperado) y deben comunicar el *tipo* de fallo estructuradamente para que la capa de interfaces construya la respuesta HTTP correcta. Las consultas solo fallan en el sentido de "no encontrado", que `Optional.empty()` modela suficientemente sin añadir complejidad. Usar `Result` en consultas sería sobrediseño para ese único caso de fallo.

### 5.4 Repositorio de Persistencia (Adaptador): `ProfileRepositoryImpl.java`

```java
@Repository
public class ProfileRepositoryImpl implements ProfileRepository {
    private final ProfilePersistenceRepository profilePersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileRepositoryImpl(ProfilePersistenceRepository profilePersistenceRepository,
                                  ApplicationEventPublisher eventPublisher) {
        this.profilePersistenceRepository = profilePersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Profile save(Profile profile) {
        boolean isNew = profile.getId() == null;
        var entity = ProfilePersistenceAssembler.toPersistenceFromDomain(profile);
        var savedEntity = profilePersistenceRepository.save(entity);
        var savedProfile = ProfilePersistenceAssembler.toDomainFromPersistence(savedEntity);
        if (isNew) {
            savedProfile.onCreated();
            savedProfile.domainEvents().forEach(eventPublisher::publishEvent);
            savedProfile.clearDomainEvents();
        }
        return savedProfile;
    }
    // ...
}
```

---

## 6. El Módulo `learning` — Bounded Context Principal

El contexto `learning` modela el proceso de aprendizaje: cursos con rutas ordenadas de tutoriales, estudiantes vinculados a perfiles, matrículas con seguimiento de progreso y métricas de rendimiento.

### 6.1 Agregados del Contexto `learning`

#### `Course` — Agregado de Curso

Representa un curso con su ruta de aprendizaje ordenada. Encapsula la lógica de agregar tutoriales a la ruta delegando en `LearningPath`.

- Contiene un `LearningPath` (Value Object con colección) que mantiene una **lista enlazada** de `LearningPathItem`.
- Delega la adición de ítems a `LearningPath.addItem(...)`, que actualiza los punteros `nextItem` automáticamente.

#### `Student` — Agregado de Estudiante

Representa a un estudiante identificado por un `AcmeStudentRecordId` (UUID generado automáticamente al crear el estudiante) y vinculado a un `Profile` del contexto `profiles` a través de `ProfileId` (referencia por ID, no por el objeto `Profile` completo).

- Acumula métricas de rendimiento en `StudentPerformanceMetricSet` (Value Object inmutable que se reemplaza con una instancia nueva al actualizarse).

#### `Enrollment` — Agregado de Matrícula

Representa la matrícula de un estudiante en un curso. Implementa una **máquina de estados** mediante `EnrollmentStatus`:

```
REQUESTED ──► CONFIRMED ──► (progreso completado implícitamente al completar todos los tutoriales)
          └──► REJECTED
          └──► CANCELLED
```

- `confirm()` → cambia estado a `CONFIRMED` e inicializa el `ProgressRecord` con el primer tutorial del curso.
- `reject()` / `cancel()` → transiciones de estado sin efectos secundarios adicionales.
- `completeTutorial(TutorialId)` → marca el tutorial como completado en el `ProgressRecord`, avanza al siguiente tutorial y registra el evento de dominio `TutorialCompletedEvent`.

### 6.2 Entidades Subordinadas (no-agregados con identidad)

A diferencia de los Value Objects, las entidades subordinadas **tienen identidad propia** (`id`) pero solo existen dentro del ciclo de vida de su agregado padre. Se ubican en `domain/model/entities/` y **no** heredan de `AbstractDomainAggregateRoot`.

| Entidad | Agregado padre | Rol |
| :--- | :--- | :--- |
| `LearningPathItem` | `Course` | Nodo de la lista enlazada de la ruta de aprendizaje. Contiene `tutorialId` y referencia al `nextItem`. |
| `ProgressRecordItem` | `Enrollment` | Ítem de progreso para un tutorial específico. Rastrea `ProgressStatus` y fechas de inicio/completado. |

### 6.3 Value Objects con Colecciones Internas

No todos los Value Objects son `record`. Cuando un Value Object necesita gestionar una **colección mutable internamente** para cumplir su invariante de dominio, se implementa como clase regular con acceso controlado.

| Clase | Razón para no ser `record` |
| :--- | :--- |
| `LearningPath` | Gestiona una `List<LearningPathItem>` que crece al agregar tutoriales. Sus mutaciones están encapsuladas y solo se exponen a través de métodos de dominio (`addItem`). |
| `ProgressRecord` | Gestiona una `List<ProgressRecordItem>` que crece al confirmar matrículas y al completar tutoriales. |

> **Regla**: Los setters de colección (`setLearningPathItems`, `setProgressRecordItems`) existen *exclusivamente* para los assemblers de persistencia al reconstituir el estado desde la base de datos. No deben usarse en lógica de dominio ni en servicios de aplicación.

### 6.4 Enums como Parte del Modelo de Dominio

Los enums modelan conjuntos de valores discretos y máquinas de estado. En persistencia JPA son almacenados por nombre (`String`) por defecto o a través de un `AttributeConverter`, sin requerir tablas adicionales.

| Enum | Usado en | Valores |
| :--- | :--- | :--- |
| `EnrollmentStatus` | `Enrollment` | `REQUESTED`, `CONFIRMED`, `REJECTED`, `CANCELLED` |
| `ProgressStatus` | `ProgressRecordItem` | `NOT_STARTED`, `STARTED`, `COMPLETED` |

### 6.5 Patrón de Lista Enlazada en `LearningPath`

`LearningPath` implementa una lista enlazada de `LearningPathItem` donde cada ítem apunta al siguiente (`nextItem`). Este patrón permite que el ordenamiento de la ruta sea parte del dominio sin depender del orden de filas en la base de datos.

**Reconstitución en dos pasadas** (`CoursePersistenceAssembler.toDomainFromPersistence`):

1. **Primera pasada**: Crea todos los objetos `LearningPathItem` sin enlazar (`nextItem = null`), indexados en un mapa por `id`.
2. **Segunda pasada**: Itera de nuevo los ítems de persistencia y asigna `nextItem` referenciando el objeto ya creado en el mapa, reconstruyendo la cadena correctamente.

Este patrón evita referencias circulares durante la construcción y reconstitución del grafo de ítems.

### 6.6 Excepciones de Dominio vs. `Result`

El contexto `learning` define excepciones de dominio propias (`CourseNotFoundException`, `EnrollmentNotFoundException`, `StudentNotFoundException`) que extienden `RuntimeException`. Estas coexisten con el patrón `Result` del contexto `profiles`.

**Cuándo usar cada mecanismo:**

| Situación | Mecanismo recomendado |
| :--- | :--- |
| Fallo esperable de un caso de uso (conflicto, validación, recurso no encontrado) que el llamador debe distinguir y manejar | `Result.failure(ApplicationError.notFound(...))` en el servicio de aplicación |
| Violación de invariante interna que solo ocurre por un bug en el código del sistema | Excepción de dominio (`RuntimeException`) que señala un estado nunca válido |

> **Advertencia crítica**: El `GlobalExceptionHandler` actual captura `RuntimeException` genéricamente y devuelve **HTTP 500**. Cualquier excepción de dominio que no tenga su propio `@ExceptionHandler` declarado será reportada como error interno del servidor en lugar del código correcto (404, 422, etc.). Al usar excepciones de dominio, añadir siempre un handler específico en `GlobalExceptionHandler` o convertir el error a `Result` en el servicio de aplicación.

### 6.7 Eventos de Dominio en `learning`

`TutorialCompletedEvent` extiende `ApplicationEvent` de Spring, a diferencia de `ProfileCreatedEvent` (que es un `record` puro). La diferencia obedece al origen de la publicación:

| Estrategia | Cuándo usarla |
| :--- | :--- |
| `record` puro (e.g., `ProfileCreatedEvent`) | El evento es *registrado* por el agregado y *publicado por el adaptador de repositorio* después del guardado. El agregado no conoce el `ApplicationEventPublisher`. |
| Extender `ApplicationEvent` (e.g., `TutorialCompletedEvent`) | El evento puede ser publicado directamente usando `ApplicationEventPublisher` desde cualquier lugar, incluyendo el propio agregado durante la ejecución de su lógica. |

---

## 7. Flujo de Dependencias e Integración (ACL y Eventos)

### 7.1 Regla Estricta del Flujo de Capas

Las dependencias de código fluyen de forma unidireccional y siempre hacia adentro, protegiendo las reglas de negocio expuestas en el Dominio:

```
interfaces (REST, ACL, Events) ────> application (Services) ────> domain (Aggregates, VOs)
           │                                 │
           └──────> infrastructure <─────────┘
```

La capa de `domain` nunca debe importar clases de `application`, `interfaces` ni `infrastructure` (a excepción del Shared Kernel).

### 7.2 Integración entre Contextos mediante ACL (Anti-Corruption Layer)

Cuando un Bounded Context (por ejemplo, `learning`) requiere interactuar con otro contexto (como `profiles`), **nunca** debe inyectar directamente los repositorios, servicios internos o entidades del otro contexto. En su lugar:

1. El contexto de `profiles` expone una interfaz fachada en `ProfilesContextFacade.java` (capa `interfaces/acl`).
2. El contexto `profiles` implementa esta fachada en `ProfilesContextFacadeImpl.java` (capa `application/acl`).
3. La implementación traduce los tipos primitivos que entran de otras capas a los Commands y Queries internos, invoca los servicios y mapea los resultados de salida de forma segura.

> **Convención del valor de retorno**: Los métodos de la fachada retornan primitivos (`Long`, `String`) en lugar de objetos de dominio, para no exponer tipos internos del contexto. Cuando una operación falla, la fachada retorna un valor centinela (`0L`). Esta convención es adecuada cuando el consumidor solo necesita saber si la operación tuvo éxito. Si el consumidor necesita distinguir el tipo de fallo (conflicto vs. validación vs. error inesperado), la firma debe evolucionar a `Optional<Long>` o `Result<Long, ApplicationError>`.

### 7.3 Desacoplamiento mediante Eventos

Para la comunicación reactiva asíncrona o desacoplada entre contextos:

- **Evento de Dominio (Interno)**: `ProfileCreatedEvent.java` es disparado por el propio agregado. No debe salir de los límites de su propio Bounded Context.
- **Evento de Integración (Público)**: `ProfileCreatedIntegrationEvent.java` representa el lenguaje público expuesto al exterior. Otros contextos escuchan este evento, nunca el evento de dominio interno.
- **Manejador de Eventos**: `ProfileCreatedEventHandler.java` escucha el evento local de dominio a través de `@EventListener` y lo retransmite al exterior mapeado como un Evento de Integración mediante Spring `ApplicationEventPublisher`.

> **Convención de calificador de bean en handlers**: Los event handlers se anotan con `@Service("contextoNombreDelHandler")` (ej: `@Service("profilesProfileCreatedEventHandler")`). Esto previene conflictos de nombre de bean cuando múltiples contextos definen handlers con el mismo nombre de clase simple.

---

## 8. Patrones de Diseño Identificados

| Patrón                          | Ubicación                                                                                         | Propósito                                                                                                                  |
| ------------------------------- | ------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------- |
| **Mónada Result / Either**      | `shared/application/result/Result.java`                                                           | Modelar flujos de éxito y error de manera funcional y predecible sin usar excepciones para control de flujo.               |
| **Separación Agregado/Entidad** | `profiles/domain/.../Profile.java` vs `profiles/infrastructure/.../ProfilePersistenceEntity.java` | Desacoplar completamente el diseño del negocio de las restricciones físicas de la persistencia (JPA/Hibernate).            |
| **Adapter (Hexagonal)**         | `profiles/infrastructure/.../ProfileRepositoryImpl.java`                                          | Adaptar el puerto del repositorio de dominio (`ProfileRepository`) al framework de persistencia de infraestructura.        |
| **Anti-Corruption Layer (ACL)** | `profiles/interfaces/acl/` & `profiles/application/acl/`                                          | Establecer una interfaz y fachada limpia que aísle e integre Bounded Contexts sin exponer modelos internos.                |
| **Domain & Integration Events** | `domain/.../events/` vs `interfaces/events/`                                                      | Desacoplar la ejecución de efectos secundarios inter-contexto mediante un canal de eventos de dos niveles.                 |
| **Assemblers Estáticos**        | `interfaces/rest/transform/` e `infrastructure/persistence/jpa/assemblers/`                       | Traducir datos entre capas mediante funciones puras sin estado, evitando acoplamiento bidireccional.                       |
| **Value Object**                | `profiles/domain/model/valueobjects/`                                                             | Inmutabilidad, encapsulamiento de validaciones semánticas y lógica de dominio granular en tipos con identidad por valor.   |
| **Linked List de Dominio**      | `learning/domain/model/valueobjects/LearningPath.java`                                            | Modelar el ordenamiento de la ruta de aprendizaje como estructura de dominio sin depender del orden de filas en base de datos. |
| **Máquina de Estados**          | `learning/domain/model/aggregates/Enrollment.java`                                                | Controlar transiciones válidas del estado de una matrícula mediante métodos de dominio explícitos con semántica de negocio. |

---

## 9. Cómo Replicar Esta Estructura en un Bounded Context Nuevo

Para agregar un nuevo contexto en este proyecto (por ejemplo, `iam`):

### Paso 1: Definir los límites del Dominio (`domain`)

1. Crear los **Value Objects** con validaciones:
   - *Atómicos*: usar `record` de Java con validaciones en el constructor compacto; lanzar `IllegalArgumentException` si las precondiciones no se cumplen.
   - *Con colecciones internas*: usar clase regular con estado controlado. Los setters de colección son exclusivos para assemblers de persistencia; no se usan en lógica de dominio.
2. Crear los **Enums** de estado si el agregado tiene una máquina de estados.
3. Crear los **Commands** y **Queries** como records inmutables de Java.
4. Crear el **Agregado de Dominio** heredando de `AbstractDomainAggregateRoot<MiAgregado>`. Incluir un constructor de reconstitución (recibe `id` y todos los tipos de dominio) para uso exclusivo del assembler, y un constructor de creación (sin `id`).
5. Crear la interfaz **Repository del Dominio** en `domain/repositories/` que declare métodos de dominio puros (e.g. `save(MiAgregado a)`, `Optional<MiAgregado> findById(Long id)`).
6. (Opcional) Si el agregado publica eventos, crear el record de evento de dominio bajo `domain/model/events/`.
7. (Opcional) Si el contexto necesita excepciones de dominio propias, crearlas en `domain/exceptions/` extendiendo `RuntimeException`. Registrar siempre un `@ExceptionHandler` específico en `GlobalExceptionHandler` para mapearlas al HTTP status correcto, o convertirlas a `Result` en la capa de aplicación.

### Paso 2: Crear el flujo de Casos de Uso (`application`)

1. Definir los contratos de los servicios en `application/commandservices/` y `application/queryservices/`.
2. Implementar los servicios en `application/internal/commandservices/` y `application/internal/queryservices/`. La carpeta `internal/` marca que las implementaciones son detalles privados del contexto, no parte del contrato público.
3. Anotar las implementaciones con `@Service` y `@Transactional`. Retornar `Result<MiAgregado, ApplicationError>` en comandos y `Optional<MiAgregado>` en consultas.

### Paso 3: Configurar la Persistencia en Infraestructura (`infrastructure`)

1. Crear la entidad de persistencia `MiEntidadPersistenceEntity` heredando de `AuditableAbstractPersistenceEntity` y anotarla con `@Entity`. Si el nombre automático de tabla no es el deseado (especialmente en entidades con nombre compuesto), añadir `@Table(name = "nombre_correcto")` explícito.
2. Crear los `@Embeddable` correspondientes y los `@Converter` que implementen `AttributeConverter`.
3. Crear el ensamblador `MiEntidadPersistenceAssembler` con métodos estáticos `toDomainFromPersistence` y `toPersistenceFromDomain`. Si el dominio tiene entidades subordinadas con referencias cruzadas (como `LearningPathItem.nextItem`), usar el patrón de reconstitución en dos pasadas.
4. Crear el repositorio de persistencia `MiEntidadPersistenceRepository` extendiendo `JpaRepository`.
5. Crear el adaptador `MiEntidadRepositoryImpl` en la carpeta `adapters`, anotándolo con `@Repository`. Debe implementar la interfaz del dominio definida en el Paso 1, inyectar el JpaRepository y `ApplicationEventPublisher`, y realizar la lógica de guardado y mapeo descrita en la sección 3.3.

### Paso 4: Definir la interfaz exterior (`interfaces`)

1. Si requiere integrarse localmente con otros Bounded Contexts, definir la interfaz del facade de ACL en `interfaces/acl/` e implementarlo en `application/acl/`.
2. Si expone APIs REST:
   - Crear los DTOs de Request y Response en `interfaces/rest/resources/` (Records inmutables con anotaciones Jakarta Validation `@NotBlank`, `@Size` y anotaciones OpenAPI/Swagger `@Schema`).
   - Crear los assemblers de REST en `interfaces/rest/transform/`.
   - Crear el REST Controller en `interfaces/rest/` usando `ResponseEntityAssembler` para procesar el retorno del servicio de comandos.
3. (Opcional) Implementar el mapeador y disparador de eventos de integración si otros contextos reaccionan a los cambios. Anotar el handler con `@Service("contextoNombreDelHandler")` para prevenir conflictos de nombre de bean.

---

## 10. Checklist de Reglas

| #   | Regla                                                                                                                                                                                                                                  | ¿Cómo validarlo?                                                                                                                                                 |
| --- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | Los agregados de dominio no contienen anotaciones `@Entity` ni de JPA.                                                                                                                                                                 | Inspeccionar imports de `domain/model/aggregates/` (no deben existir imports de `jakarta.persistence.*`).                                                        |
| 2   | Los agregados heredan únicamente de `AbstractDomainAggregateRoot`.                                                                                                                                                                     | Validar la herencia en la clase del agregado.                                                                                                                    |
| 3   | La persistencia JPA e Hibernate se restringe a la capa `infrastructure`.                                                                                                                                                               | Verificar que `@Entity` e `@Id` solo estén dentro de `infrastructure/persistence/jpa/entities/`.                                                                 |
| 4   | Las firmas de los comandos del servicio de aplicación devuelven la mónada `Result<Aggregate, ApplicationError>`. Las consultas devuelven `Optional<Aggregate>`.                                                                        | Inspeccionar los tipos de retorno en interfaces de servicios de comandos y consultas.                                                                            |
| 5   | Las implementaciones de servicios y repositorios están marcadas con `@Service` y `@Repository` en sus clases concretas internas, no en las interfaces.                                                                                 | Revisar que las interfaces del puerto o del servicio de aplicación estén limpias de anotaciones de Spring.                                                       |
| 6   | Los Value Objects *atómicos* son `record` inmutables y lanzan `IllegalArgumentException` en su constructor compacto. Los Value Objects con colecciones internas son clases regulares con mutación encapsulada; sus setters de colección son exclusivos para assemblers de persistencia, no para lógica de dominio. | Revisar constructores y acceso a setters de Value Objects.                                                                                           |
| 7   | Los DTOs REST de entrada (Resources) implementan validación de Beans de Jakarta (`@NotBlank`, etc.) e i18n.                                                                                                                            | Validar que los recursos tengan anotaciones de validación y sus mensajes apunten a claves predecibles.                                                           |
| 8   | El mapeado entre la capa física de persistencia y la del dominio se realiza en una clase `Assembler` estática de persistencia.                                                                                                         | Revisar que la clase se denomine `XxxPersistenceAssembler` y contenga únicamente funciones mapeadoras puras sin estado.                                          |
| 9   | La comunicación local entre Bounded Contexts se realiza exclusivamente a través de Fachadas ACL.                                                                                                                                       | Ningún Bounded Context externo puede importar el repositorio, servicios internos o agregados de otro contexto.                                                   |
| 10  | Las tablas de Hibernate se configuran con la estrategia de nombrado física pluralizada en snake_case. Las entidades con nombre compuesto o sufijos de infraestructura deben declarar `@Table(name = "...")` explícito.                  | Confirmar `spring.jpa.hibernate.naming.physical-strategy` en `application.properties`. Verificar manualmente los nombres de tabla de entidades compuestas.        |
| 11  | Los event handlers se anotan con `@Service("contextoNombreDelHandler")` para prevenir conflictos de nombre de bean entre contextos del monolito modular.                                                                               | Verificar que ningún bean de handler use el nombre de clase por defecto.                                                                                         |
| 12  | Cada excepción de dominio personalizada (`XxxNotFoundException`) tiene su propio `@ExceptionHandler` en `GlobalExceptionHandler` o se convierte a `Result` en la capa de aplicación antes de llegar a la capa de interfaces.           | Verificar que ninguna excepción de dominio quede capturada solo por el handler genérico de `RuntimeException` (que devuelve HTTP 500).                            |
| 13  | `@EnableJpaAuditing` está presente en `LearningCenterPlatformApplication` para que `AuditableAbstractPersistenceEntity` rellene automáticamente `createdAt` y `updatedAt`.                                                            | Verificar la anotación en la clase principal de la aplicación. Sin ella, las columnas `NOT NULL` de auditoría causarán errores en inserción.                     |

---

## 11. Errores Comunes y Advertencias

Esta sección recoge los errores más frecuentes al implementar un Bounded Context nuevo siguiendo esta arquitectura.

### 11.1 Paquete base incorrecto en imports

El paquete raíz del proyecto es `com.acme.learning.center.platform`. Todos los imports internos deben originarse aquí. El error más común al copiar código de un proyecto anterior es mantener un paquete raíz distinto (e.g., `com.acme.center.platform`).

**Síntoma**: Error de compilación `cannot find symbol` en repositorios, assemblers o entidades de persistencia del contexto.

### 11.2 Agregar lógica de negocio en `shared/`

`shared/` es infraestructura técnica transversal. Agregar DTOs, assemblers o reglas de negocio de un contexto específico en `shared/` acopla a todos los contextos a ese negocio y rompe la independencia modular, imposibilitando extraer ese contexto a un microservicio en el futuro.

### 11.3 Referenciar un agregado completo desde otro agregado

En DDD, los agregados solo deben referenciarse por **identificador**, no por el objeto completo. El patrón correcto es `private Long courseId` en lugar de `private Course course`. Referenciar el objeto completo fuerza la carga del grafo entero al reconstituir desde persistencia y crea acoplamiento implícito entre ciclos de vida de agregados.

### 11.4 Excepciones de dominio sin handler específico producen HTTP 500

El `GlobalExceptionHandler` captura `RuntimeException` genéricamente y devuelve HTTP 500. Cualquier excepción de dominio (`CourseNotFoundException`, `EnrollmentNotFoundException`, etc.) sin su propio `@ExceptionHandler` será reportada como error interno del servidor en lugar del código semántico correcto (404, 422, etc.).

**Solución**: Añadir un `@ExceptionHandler` específico por excepción de dominio en `GlobalExceptionHandler`, o convertir la excepción a `Result.failure(ApplicationError.notFound(...))` en el servicio de aplicación.

### 11.5 Nombres de tabla incorrectos en entidades con nombre compuesto

`SnakeCaseWithPluralizedTablePhysicalNamingStrategy` aplica la pluralización al nombre *completo* de la clase. Una entidad llamada `LearningPathItemPersistenceEntity` producirá `learning_path_item_persistence_entities`, no `learning_path_items`.

**Solución**: Toda entidad JPA con nombre compuesto o con sufijo de infraestructura en el nombre de clase debe declarar `@Table(name = "nombre_deseado")` explícito.

### 11.6 Publicación de eventos sin garantía transaccional

En el adaptador de repositorio, los eventos se publican con `eventPublisher.publishEvent(...)` inmediatamente tras el `save`. Si el listener ejecuta una operación que falla, la entidad ya está persistida pero el efecto secundario se perdió, creando inconsistencia.

**Para mayor seguridad**: Los listeners que deben ejecutarse solo tras el commit de la transacción deben usar `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` en lugar de `@EventListener`.

### 11.7 Usar el setter de `id` del agregado fuera del assembler

El setter público de `id` existe exclusivamente para que el assembler de persistencia asigne la identidad generada por JPA. Llamarlo desde un servicio de aplicación, desde otro agregado o desde una prueba unitaria viola la invariante de que la identidad la gestiona la capa de infraestructura.

### 11.8 Omitir `@EnableJpaAuditing` en la clase principal

Sin `@EnableJpaAuditing` en `LearningCenterPlatformApplication`, los campos `createdAt` y `updatedAt` de `AuditableAbstractPersistenceEntity` no se rellenan automáticamente. Como `created_at` tiene restricción `NOT NULL` en la base de datos, toda operación de inserción fallará silenciosamente en tiempo de ejecución.

---

## Resumen de Dependencias Clave (`pom.xml`)

| Dependencia | Uso / Propósito |
| :--- | :--- |
| `spring-boot-starter-data-jpa` | Soporte para persistencia relacional con Spring Data JPA y Hibernate. |
| `spring-boot-starter-validation` | Implementación de Jakarta Bean Validation para validación de DTOs en la API REST. |
| `spring-boot-starter-web` | Soporte para desarrollo web REST con Spring MVC. |
| `spring-boot-devtools` | Herramientas de desarrollo (recarga rápida en tiempo de ejecución, etc.). |
| `mysql-connector-j` | Driver de conexión JDBC nativo para la base de datos MySQL. |
| `lombok` | Biblioteca auxiliar para la generación automática de boilerplate (Getters, Setters, inyección por constructor, etc.). |
| `spring-boot-starter-test` | Framework base para la ejecución de pruebas unitarias y de integración de Spring Boot. |
| `springdoc-openapi-starter-webmvc-ui` (v3.0.3) | Generación interactiva de documentación de API con OpenAPI 3 / Swagger UI. |
| `pluralize` (v1.0.0) | Utilizado en la estrategia de base de datos para la pluralización automática de nombres de tablas. |

---

Este documento de arquitectura debe servir de plantilla y manual de desarrollo continuo para asegurar la consistencia y la excelente modularidad del proyecto.
