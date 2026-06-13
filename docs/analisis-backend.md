# Análisis de Arquitectura del Proyecto `learning-center-platform`

> **Documento reusable** — Esta guía documenta la arquitectura, patrones de diseño y estructura del proyecto. Puede usarse como referencia para iniciar nuevos proyectos o agregar nuevos Bounded Contexts siguiendo rigurosamente las convenciones de diseño del proyecto.

---

## Tabla de Contenidos

1. [Visión General](#1-visión-general)
2. [Estructura de Carpetas](#2-estructura-de-carpetas)
3. [Arquitectura: DDD + Puertos y Adaptadores (Hexagonal Pura)](#3-arquitectura-ddd--puertos-y-adaptadores-hexagonal-pura)
4. [El Módulo `shared` — El Núcleo Común](#4-el-módulo-shared--el-núcleo-común)
5. [El Módulo `profiles` — Bounded Context de Ejemplo](#5-el-módulo-profiles--bounded-context-de-ejemplo)
6. [Flujo de Dependencias e Integración (ACL y Eventos)](#6-flujo-de-dependencias-e-integración-acl-y-eventos)
7. [Patrones de Diseño Identificados](#7-patrones-de-diseño-identificados)
8. [Cómo Replicar Esta Estructura en un Proyecto Nuevo](#8-cómo-replicar-esta-estructura-en-un-proyecto-nuevo)
9. [Checklist de Reglas](#9-checklist-de-reglas)

---

## 1. Visión General

| Dimensión        | Detalle                                                   |
| ---------------- | --------------------------------------------------------- |
| **Lenguaje**     | Java 26                                                   |
| **Framework**    | Spring Boot 4.0.6                                         |
| **Build tool**   | Maven                                                     |
| **Persistencia** | Spring Data JPA (Hibernate) + MySQL                       |
| **API**          | REST (Spring Web) + OpenAPI 3 / Swagger (Springdoc 3.0.3) |
| **Validación**   | Jakarta Bean Validation                                   |
| **Utilidades**   | Lombok, JSpecify, Pluralize                               |

El proyecto implementa un **monolito modular** estructurado por **Bounded Contexts** de Domain-Driven Design (DDD). A diferencia de arquitecturas tradicionales acopladas a la base de datos, este proyecto implementa **Arquitectura Hexagonal Pura (Puertos y Adaptadores)**. Las clases del dominio son Java puro y no contienen anotaciones de JPA (`@Entity`, `@Table`, etc.). El código de infraestructura de persistencia se mantiene aislado y mapeado a través de adaptadores y convertidores de datos.

---

## 2. Estructura de Carpetas

La raíz del paquete del proyecto es `com.acme.learning.center.platform`.

```
src/
├── main/
│   ├── java/com/acme/learning/center/platform/
│   │   ├── LearningCenterPlatformApplication.java               ← Punto de entrada Spring Boot
│   │   │
│   │   ├── iam/                                                 ← Bounded Context: IAM (Placeholder)
│   │   ├── learning/                                            ← Bounded Context: Learning (Placeholder)
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
│   │   │   │   │   │   └── ProfileCreatedEvent.java             ← Evento de Dominio interno
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
│       ├── application.properties                               ← Configuración general
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

A diferencia de implementaciones clásicas de Spring, los agregados del dominio (como [Profile.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/domain/model/aggregates/Profile.java)) son clases puras de Java.

- **Sin `@Entity` ni `@Table`**: No contienen anotaciones de persistencia.
- **Herencia limpia**: Heredan de [AbstractDomainAggregateRoot.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/shared/domain/model/aggregates/AbstractDomainAggregateRoot.java) de la capa `shared/domain`, la cual delega a la clase de Spring Data Commons `AbstractAggregateRoot` para la gestión local de eventos, sin importar dependencias de JPA.
- **Constructores e inmutabilidad**: Se construyen a través de constructores explícitos que reciben Commands u objetos del dominio, validando que el estado inicial cumpla con las reglas del negocio.

### 3.2 Infraestructura de Persistencia Desacoplada

El acoplamiento a base de datos e Hibernate se confina completamente en `infrastructure/persistence/jpa/`:

- **Entidades de Persistencia**: [ProfilePersistenceEntity.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/infrastructure/persistence/jpa/entities/ProfilePersistenceEntity.java) contiene todas las anotaciones de base de datos (`@Entity`, `@Table`, `@Column`, `@Embedded`, `@AttributeOverrides`, `@Convert`). Estas heredan de [AuditableAbstractPersistenceEntity.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/shared/infrastructure/persistence/jpa/entities/AuditableAbstractPersistenceEntity.java) que gestiona los campos de auditoría (`createdAt`, `updatedAt`) de forma transparente para el dominio.
- **Embeddables**: Objetos como [PersonNamePersistenceEmbeddable.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/infrastructure/persistence/jpa/embeddables/PersonNamePersistenceEmbeddable.java) encapsulan el mapeo de base de datos para Value Objects estructurados.
- **Attribute Converters**: Se utilizan para convertir Value Objects atómicos a tipos primitivos nativos de la base de datos (por ejemplo, [EmailAddressPersistenceConverter.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/infrastructure/persistence/jpa/converters/EmailAddressPersistenceConverter.java)).
- **Mapeo Bilateral (Assemblers)**: [ProfilePersistenceAssembler.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/infrastructure/persistence/jpa/assemblers/ProfilePersistenceAssembler.java) contiene lógica con funciones puras `static` para mapear los objetos bidireccionalmente:
  - `toDomainFromPersistence(ProfilePersistenceEntity entity)`
  - `toPersistenceFromDomain(Profile profile)`

### 3.3 Repositorio Hexagonal

- **El Puerto (Dominio)**: [ProfileRepository.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/domain/repositories/ProfileRepository.java) es una interfaz de Java pura que trabaja únicamente con tipos y entidades de dominio.
- **El Repositorio Spring Data**: [ProfilePersistenceRepository.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/infrastructure/persistence/jpa/repositories/ProfilePersistenceRepository.java) extiende `JpaRepository` y opera sobre la entidad de persistencia de infraestructura.
- **El Adaptador**: [ProfileRepositoryImpl.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/infrastructure/persistence/jpa/adapters/ProfileRepositoryImpl.java) implementa la interfaz del dominio. Sus responsabilidades son:
  1. Inyectar el repositorio de Spring Data.
  2. Inyectar `ApplicationEventPublisher`.
  3. Mapear de Dominio a Persistencia al guardar, y de Persistencia a Dominio al leer.
  4. Publicar manualmente los eventos de dominio acumulados en el agregado tras persistir con éxito.

---

## 4. El Módulo `shared` — El Núcleo Común

El paquete `shared` contiene utilidades transversales libres de acoplamiento a contextos específicos del negocio.

### 4.1 La Mónada `Result<T, E>` Funcional

Ubicación: [Result.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/shared/application/result/Result.java)

Implementa una estructura de tipo mónada (Similar a `Either` en programación funcional) a través de una `sealed interface` de Java.

- **Records internos**: `Success` y `Failure` aseguran inmutabilidad y seguridad de tipos.
- **Métodos Funcionales**:
  - `map` y `mapError` para transformaciones lineales sin salir del flujo.
  - `flatMap` para encadenamiento secuencial de operaciones propensas a fallar.
  - `recover` para recuperación o fallbacks ante fallos.
  - `fold` para evaluar de forma exhaustiva ambos caminos en el extremo del flujo.
  - `toOptional()` y `getOrElse(T defaultValue)`.

### 4.2 Errores de Aplicación Estandarizados (`ApplicationError`)

Ubicación: [ApplicationError.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/shared/application/result/ApplicationError.java)

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

### 4.3 Pluralización Automática de Base de Datos

Ubicación: [SnakeCaseWithPluralizedTablePhysicalNamingStrategy.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/shared/infrastructure/persistence/jpa/configuration/strategy/SnakeCaseWithPluralizedTablePhysicalNamingStrategy.java)

Implementa `PhysicalNamingStrategy` de Hibernate y utiliza la dependencia `Pluralize`:

- **Nombres de columnas y campos**: Se traducen automáticamente a `snake_case`.
- **Nombres de tablas**: Se traducen a plural y `snake_case` (e.g. la entidad `ProfilePersistenceEntity` se mapea a la tabla `profiles` automáticamente).

### 4.4 Manejo Global de Excepciones e Internacionalización (i18n)

El framework implementa localización nativa de mensajes de error a nivel de HTTP REST:

- **`GlobalExceptionHandler`**: Intercepta excepciones de validación de Spring (`MethodArgumentNotValidException`), errores de argumentos incorrectos (`IllegalArgumentException`) y excepciones generales, traduciéndolas a una respuesta HTTP que transporta un `ErrorResource`.
- **`ErrorResponseAssembler`**: Traduce un `ApplicationError` a un `ResponseEntity<ErrorResource>`.
- **Locale Context**: Utiliza `LocaleContextHolder.getLocale()` y lee los archivos de recursos `messages.properties` y `messages_es.properties` para localizar los mensajes de error basándose en el header `Accept-Language` de la solicitud HTTP.

---

## 5. El Módulo `profiles` — Bounded Context de Ejemplo

A continuación, se detalla la implementación del contexto `profiles`, el cual sirve de referencia para todo el proyecto.

### 5.1 Agregado de Dominio: [Profile.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/domain/model/aggregates/Profile.java)

```java
public class Profile extends AbstractDomainAggregateRoot<Profile> {
    @Getter @Setter
    private Long id;
    @Getter
    private PersonName name;
    private EmailAddress emailAddress;
    private StreetAddress streetAddress;

    public Profile(CreateProfileCommand command) {
        this(null,
             new PersonName(command.firstName(), command.lastName()),
             new EmailAddress(command.email()),
             new StreetAddress(command.street(), command.number(), command.city(), command.postalCode(), command.country())
        );
    }

    public Profile(Long id, PersonName name, EmailAddress emailAddress, StreetAddress streetAddress) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.emailAddress = Objects.requireNonNull(emailAddress);
        this.streetAddress = Objects.requireNonNull(streetAddress);
    }

    public void onCreated() {
        registerDomainEvent(ProfileCreatedEvent.from(this));
    }

    // Getters específicos de encapsulamiento
    public String getEmailAddress() { return emailAddress.address(); }
    public String getStreetAddress() { return streetAddress.getStreetAddress(); }
    public String getFullName() { return name.getFullName(); }
}
```

### 5.2 Value Object: [EmailAddress.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/domain/model/valueobjects/EmailAddress.java)

```java
public record EmailAddress(@Email String address) {
    public EmailAddress {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Email address must not be null or blank");
        }
    }
}
```

### 5.3 Servicio de Comandos: [ProfileCommandServiceImpl.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/application/internal/commandservices/ProfileCommandServiceImpl.java)

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

### 5.4 Repositorio de Persistencia (Adaptador): [ProfileRepositoryImpl.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/infrastructure/persistence/jpa/adapters/ProfileRepositoryImpl.java)

```java
@Repository
public class ProfileRepositoryImpl implements ProfileRepository {
    private final ProfilePersistenceRepository profilePersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileRepositoryImpl(ProfilePersistenceRepository profilePersistenceRepository, ApplicationEventPublisher eventPublisher) {
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

## 6. Flujo de Dependencias e Integración (ACL y Eventos)

### 6.1 Regla Estricta del Flujo de Capas

Las dependencias de código fluyen de forma unidireccional y siempre hacia adentro, protegiendo las reglas de negocio expuestas en el Dominio:

```
interfaces (REST, ACL, Events) ────> application (Services) ────> domain (Aggregates, VOs)
           │                                 │
           └──────> infrastructure <─────────┘
```

La capa de `domain` nunca debe importar clases de `application`, `interfaces` ni `infrastructure` (a excepción del Shared Kernel).

### 6.2 Integración entre Contextos mediante ACL (Anti-Corruption Layer)

Cuando un Bounded Context (por ejemplo, `learning`) requiere interactuar con otro contexto (como `profiles`), **nunca** debe inyectar directamente los repositorios, servicios internos o entidades del otro contexto. En su lugar:

1. El contexto de `profiles` expone una interfaz fachada en [ProfilesContextFacade.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/interfaces/acl/ProfilesContextFacade.java) (capa `interfaces/acl`).
2. El contexto `profiles` implementa esta fachada en [ProfilesContextFacadeImpl.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/application/acl/ProfilesContextFacadeImpl.java) (capa `application/acl`).
3. La implementación traduce los tipos primitivos que entran de otras capas a los Commands y Queries internos, invoca los servicios y mapea los resultados de salida de forma segura.

### 6.3 Desacoplamiento mediante Eventos

Para la comunicación reactiva asíncrona o desacoplada entre contextos:

- **Evento de Dominio (Interno)**: [ProfileCreatedEvent.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/domain/model/events/ProfileCreatedEvent.java) es disparado por el propio agregado. No debe salir de los límites de su propio Bounded Context.
- **Evento de Integración (Público)**: [ProfileCreatedIntegrationEvent.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/interfaces/events/ProfileCreatedIntegrationEvent.java) representa el lenguaje público expuesto al exterior.
- **Manejador de Eventos**: [ProfileCreatedEventHandler.java](file:///d:/1.-%20UNIVERSIDAD/Ciclo%209/Aplicaciones%20Open%20Source/backend/learning-center-platform/learning-center-platform/src/main/java/com/acme/learning/center/platform/profiles/application/internal/eventhandlers/ProfileCreatedEventHandler.java) escucha el evento local de dominio a través de `@EventListener` y lo retransmite al exterior mapeado como un Evento de Integración mediante Spring `ApplicationEventPublisher`.

---

## 7. Patrones de Diseño Identificados

| Patrón                          | Ubicación                                                                                         | Propósito                                                                                                                  |
| ------------------------------- | ------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------- |
| **Mónada Result / Either**      | `shared/application/result/Result.java`                                                           | Modelar flujos de éxito y error de manera funcional y predecible.                                                          |
| **Separación Agregado/Entidad** | `profiles/domain/.../Profile.java` vs `profiles/infrastructure/.../ProfilePersistenceEntity.java` | Desacoplar completamente el diseño del negocio de las restricciones físicas de la persistencia (JPA/Hibernate).            |
| **Adapter (Hexagonal)**         | `profiles/infrastructure/.../ProfileRepositoryImpl.java`                                          | Adaptar el puerto del repositorio de dominio (`ProfileRepository`) al framework de persistencia de infraestructura.        |
| **Anti-Corruption Layer (ACL)** | `profiles/interfaces/acl/` & `profiles/application/acl/`                                          | Establecer una interfaz y fachada limpia que aísle e integre Bounded Contexts.                                             |
| **Domain & Integration Events** | `domain/.../events/` vs `interfaces/events/`                                                      | Desacoplar la ejecución de efectos secundarios inter-contexto.                                                             |
| **Assemblers Estáticos**        | `interfaces/rest/transform/`                                                                      | Traducir datos entre la infraestructura (DTOs REST) y los límites de dominio (Commands, Queries) mediante funciones puras. |
| **Value Object**                | `profiles/domain/model/valueobjects/`                                                             | Inmutabilidad, encapsulamiento de validaciones semánticas y lógica de dominio granular.                                    |

---

## 8. Cómo Replicar Esta Estructura en un Bounded Context Nuevo

Para agregar un nuevo contexto en este proyecto (por ejemplo, `iam` o `learning`):

### Paso 1: Definir los límites del Dominio (`domain`)

1. Crear los **Value Objects** inmutables (usando `record` de Java) con validaciones dentro de su constructor compacto.
2. Crear los **Commands** y **Queries** como records inmutables de Java.
3. Crear el **Agregado de Dominio** heredando de `AbstractDomainAggregateRoot<MiAgregado>`. Su constructor debe recibir únicamente commands u otros objetos del dominio.
4. Crear la interfaz **Repository del Dominio** en `domain/repositories/` que declare métodos de dominio puros (e.g. `save(MiAgregado a)`).
5. (Opcional) Si el agregado publica eventos, crear el Record de evento de dominio bajo `domain/model/events/`.

### Paso 2: Crear el flujo de Casos de Uso (`application`)

1. Definir los contratos de los servicios en `application/commandservices/` y `application/queryservices/`.
2. Implementar los servicios en `application/internal/commandservices/` y `application/internal/queryservices/`.
3. Anotar las implementaciones con `@Service` y `@Transactional`. Retornar `Result<MiAgregado, ApplicationError>` en comandos y `Optional<MiAgregado>` en consultas.

### Paso 3: Configurar la Persistencia en Infraestructura (`infrastructure`)

1. Crear la entidad de persistencia `MiEntidadPersistenceEntity` heredando de `AuditableAbstractPersistenceEntity` y anotarla con `@Entity` y `@Table`.
2. Crear los `@Embeddable` correspondientes y los `@Converter` que implementen `AttributeConverter`.
3. Crear el ensamblador `MiEntidadPersistenceAssembler` con métodos estáticos `toDomainFromPersistence` y `toPersistenceFromDomain`.
4. Crear el repositorio de persistencia `MiEntidadPersistenceRepository` extendiendo `JpaRepository`.
5. Crear el adaptador `MiEntidadRepositoryImpl` en la carpeta `adapters`, anotándolo con `@Repository`. Debe implementar la interfaz del dominio definida en el Paso 1, inyectar el JpaRepository y `ApplicationEventPublisher`, y realizar la lógica de guardado y mapeo descrita en la sección 3.3.

### Paso 4: Definir la interfaz exterior (`interfaces`)

1. Si requiere integrarse localmente con otros Bounded Contexts, definir la interfaz del facade de ACL en `interfaces/acl/` e implementarlo en `application/acl/`.
2. Si expone APIs REST:
   - Crear los DTOs de Request y Response en `interfaces/rest/resources/` (Records inmutables decorados con anotaciones Jakarta Validation `@NotBlank`, `@Size` y anotaciones OpenAPI/Swagger `@Schema`).
   - Crear los assemblers de REST en `interfaces/rest/transform/`.
   - Crear el REST Controller en `interfaces/rest/` usando la utilidad `ResponseEntityAssembler` para procesar el retorno del servicio de comandos.
3. (Opcional) Implementar el mapeador y disparador de eventos de integración si otros contextos reaccionan a los cambios.

---

## 9. Checklist de Reglas

| #   | Regla                                                                                                                                                  | ¿Cómo validarlo?                                                                                                                                                 |
| --- | ------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | Los agregados de dominio no contienen anotaciones `@Entity` ni de JPA.                                                                                 | Inspeccionar imports de `domain/model/aggregates/` (No deben existir imports de `jakarta.persistence.*`).                                                        |
| 2   | Los agregados heredan únicamente de `AbstractDomainAggregateRoot`.                                                                                     | Validar la herencia en la clase del agregado.                                                                                                                    |
| 3   | La persistencia JPA e Hibernate se restringe a la capa `infrastructure`.                                                                               | Verificar que `@Entity` e `@Id` solo estén dentro de `infrastructure/persistence/jpa/entities/`.                                                                 |
| 4   | Las firmas de los comandos del servicio de aplicación devuelven la mónada `Result<Aggregate, ApplicationError>`.                                       | Inspeccionar los tipos de retorno en interfaces de servicios de comandos.                                                                                        |
| 5   | Las implementaciones de servicios y repositorios están marcadas con `@Service` y `@Repository` en sus clases concretas internas, no en las interfaces. | Revisar que las interfaces del puerto o del servicio de aplicación estén limpias de anotaciones de Spring.                                                       |
| 6   | Todos los Value Objects de dominio son `record` inmutables y lanzan `IllegalArgumentException` al fallar sus constructores.                            | Revisar constructores de Value Objects.                                                                                                                          |
| 7   | Los DTOs REST de entrada (Resources) implementan validación de Beans de Jakarta (`@NotBlank`, etc.) e i18n.                                            | Validar que los recursos tengan anotaciones de validación y sus mensajes apunten a claves o textos predecibles.                                                  |
| 8   | El mapeado entre la capa física de persistencia y la del dominio se realiza en una clase `Assembler` estática de persistencia.                         | Revisar que la clase se denomine `XxxPersistenceAssembler` y contenga funciones mapeadoras puras.                                                                |
| 9   | La comunicación local entre Bounded Contexts se realiza exclusivamente a través de Fachadas ACL.                                                       | Ningún Bounded Context externo puede importar el repositorio, servicios internos o agregados de otro contexto.                                                   |
| 10  | Las tablas e Hibernate se configuran con la estrategia de nombrado física pluralizada en snake_case.                                                   | Confirmar que en `application.properties` la propiedad `spring.jpa.hibernate.naming.physical-strategy` use `SnakeCaseWithPluralizedTablePhysicalNamingStrategy`. |

---

## Resumen de Dependencias Clave (`pom.xml`)

| Dependencia | Uso / Propósito |
| :--- | :--- |
| `spring-boot-starter-data-jpa` | Soporte para persistencia relacional con Spring Data JPA y Hibernate. |
| `spring-boot-starter-validation` | Implementación de Jakarta Bean Validation para validación de DTOs en la API REST. |
| `spring-boot-starter-web` | Soporte para desarrollo web REST con Spring MVC. |
| `spring-boot-devtools` | Herramientas de desarrollo (recarga rápida en tiempo de ejecución, etc.). |
| `mysql-connector-j` | Driver de conexión JDBC nativo para la base de datos MySQL. |
| `lombok` | Biblioteca auxiliar para la generación automática de boilerplate (Getters, Setters, Constructor Injection, etc.). |
| `spring-boot-starter-test` | Framework base para la ejecución de pruebas unitarias y de integración de Spring Boot. |
| `springdoc-openapi-starter-webmvc-ui` (v3.0.3) | Generación interactiva de documentación de API con OpenAPI 3 / Swagger UI. |
| `pluralize` (v1.0.0) | Utilizado en la estrategia de base de datos para la pluralización automática de nombres de tablas. |

---

Este documento de arquitectura debe servir de plantilla y manual de desarrollo continuo para asegurar la consistencia y la excelente modularidad del proyecto.
