# Análisis de Arquitectura del Proyecto `catch-up-platform`

> **Documento reusable** — Esta guía documenta la arquitectura, patrones de diseño y estructura del proyecto. Puede usarse como referencia para iniciar nuevos proyectos que sigan las mismas reglas.

---

## Tabla de Contenidos

1. [Visión General](#1-visión-general)
2. [Estructura de Carpetas](#2-estructura-de-carpetas)
3. [Arquitectura: DDD + Puertos y Adaptadores (Hexagonal)](#3-arquitectura-ddd--puertos-y-adaptadores-hexagonal)
4. [El Módulo `shared` — La Columna Vertebral](#4-el-módulo-shared--la-columna-vertebral)
5. [El Módulo `news` — Bounded Context de Ejemplo](#5-el-módulo-news--bounded-context-de-ejemplo)
6. [Flujo de Dependencias](#6-flujo-de-dependencias)
7. [Patrones de Diseño Identificados](#7-patrones-de-diseño-identificados)
8. [Cómo Replicar Esta Estructura en un Proyecto Nuevo](#8-cómo-replicar-esta-estructura-en-un-proyecto-nuevo)
9. [Checklist de Reglas](#9-checklist-de-reglas)

---

## 1. Visión General

| Dimensión | Detalle |
|-----------|---------|
| **Lenguaje** | Java 26 |
| **Framework** | Spring Boot 4.0.6 |
| **Build tool** | Maven |
| **Persistencia** | Spring Data JPA (Hibernate) + MySQL |
| **API** | REST (Spring Web) + OpenAPI 3 / Swagger |
| **Validación** | Jakarta Bean Validation |
| **Utilidades** | Lombok |

El proyecto implementa un **monolito modular** estructurado por **Bounded Contexts** de Domain-Driven Design (DDD). Cada bounded context tiene su propia carpeta raíz bajo la clase principal del paquete y replica internamente la misma estructura de capas. El código compartido entre contextos reside en un módulo `shared` que actúa como **núcleo común (shared kernel)**.

---

## 2. Estructura de Carpetas

```
src/
├── main/
│   ├── java/com/acme/catchup/platform/
│   │   ├── CatchUpPlatformApplication.java          ← punto de entrada Spring Boot
│   │   ├── news/                                     ← Bounded Context: News
│   │   │   ├── application/
│   │   │   │   ├── commandservices/                  ← contratos (interfaces) de comandos
│   │   │   │   │   └── FavoriteSourceCommandFailure.java
│   │   │   │   ├── internal/
│   │   │   │   │   ├── commandservices/              ← implementaciones de comandos
│   │   │   │   │   │   └── FavoriteSourceCommandServiceImpl.java
│   │   │   │   │   └── queryservices/                ← implementaciones de queries
│   │   │   │   │       └── FavoriteSourceQueryServiceImpl.java
│   │   │   │   └── queryservices/                    ← contratos (interfaces) de queries
│   │   │   │       └── FavoriteSourceQueryService.java
│   │   │   ├── domain/
│   │   │   │   └── model/
│   │   │   │       ├── aggregates/                   ← raíces de agregados (entidades JPA)
│   │   │   │       │   └── FavoriteSource.java
│   │   │   │       ├── commands/                     ← objetos de comando (immutables)
│   │   │   │       │   └── CreateFavoriteSourceCommand.java
│   │   │   │       ├── queries/                      ← objetos de consulta (immutables)
│   │   │   │       │   ├── GetFavoriteSourceByIdQuery.java
│   │   │   │       │   ├── GetAllFavoriteSourcesByNewsApiKeyQuery.java
│   │   │   │       │   └── GetFavoriteSourceByNewsApiKeyAndSourceIdQuery.java
│   │   │   │       └── valueobjects/                 ← value objects del dominio
│   │   │   │           ├── NewsApiKey.java
│   │   │   │           └── SourceId.java
│   │   │   ├── infrastructure/
│   │   │   │   └── persistence/jpa/
│   │   │   │       ├── FavoriteSourceRepository.java ← repositorio JPA
│   │   │   │       └── converters/                   ← convertidores JPA ↔ value objects
│   │   │   │           ├── NewsApiKeyAttributeConverter.java
│   │   │   │           └── SourceIdAttributeConverter.java
│   │   │   └── interfaces/
│   │   │       └── rest/
│   │   │           ├── resources/                    ← DTOs de entrada/salida REST
│   │   │           │   ├── CreateFavoriteSourceResource.java
│   │   │           │   └── FavoriteSourceResource.java
│   │   │           └── transform/                    ← ensambladores (mappers)
│   │   │               ├── CreateFavoriteSourceCommandFromResourceAssembler.java
│   │   │               ├── NewsValueObjectFromStringAssembler.java
│   │   │               ├── favoriteSourceResourceFromEntityAssembler.java
│   │   │               ├── ResponseEntityFromFavoriteSourceCommandResultAssembler.java
│   │   │               └── ResponseEntityFromSourceQueryResultAssembler.java
│   │   └── shared/                                  ← Shared Kernel (código común)
│   │       ├── application/
│   │       │   └── result/
│   │       │       └── Result.java                  ← Result<T,E> (Success | Failure)
│   │       ├── infrastructure/
│   │       │   └── persistence/jpa/strategy/
│   │       │       └── SnakeCasePhysicalNamingStrategy.java
│   │       └── interfaces/
│   │           └── rest/
│   │               └── GlobalExceptionHandler.java   ← manejo global de excepciones
│   └── resources/
│       ├── application.properties
│       ├── static/                                   ← recursos estáticos (vacío)
│       └── templates/                                ← plantillas (vacío)
└── test/
    └── java/com/acme/catchup/platform/
        └── CatchUpPlatformApplicationTests.java
```

### Regla de Nomenclatura de Carpetas

Cada Bounded Context (`news`, `shared`, etc.) replica esta jerarquía interna:

```
<bounded-context>/
├── application/          ← Capa de aplicación (casos de uso)
│   ├── commandservices/  ← Interfaces públicas de comandos
│   ├── queryservices/    ← Interfaces públicas de consultas
│   └── internal/         ← Implementaciones (NO expuestas fuera del contexto)
│       ├── commandservices/
│       └── queryservices/
├── domain/               ← Capa de dominio (lógica de negocio)
│   └── model/
│       ├── aggregates/   ← Entidades raíz de agregados
│       ├── commands/     ← Command objects (records inmutables)
│       ├── queries/      ← Query objects (records inmutables)
│       └── valueobjects/ ← Value Objects (records inmutables con validación)
├── infrastructure/       ← Capa de infraestructura
│   └── persistence/jpa/
│       ├── XxxRepository.java
│       └── converters/   ← JPA AttributeConverters
└── interfaces/           ← Capa de interfaces (entrada/salida)
    └── rest/
        ├── resources/    ← DTOs de request/response
        └── transform/    ← Assemblers (mappers sin framework)
```

---

## 3. Arquitectura: DDD + Puertos y Adaptadores (Hexagonal)

El proyecto combina dos estilos arquitectónicos:

### 3.1 Domain-Driven Design (DDD) Táctico

| Concepto DDD | Implementación en el proyecto |
|---|---|
| **Bounded Context** | Cada carpeta hermana de `shared` (`news`, etc.) |
| **Aggregate Root** | `FavoriteSource` — anotada con `@Entity`, extiende `AbstractAggregateRoot` |
| **Value Object** | `NewsApiKey`, `SourceId` — `record` de Java con validación en constructor |
| **Command** | `CreateFavoriteSourceCommand` — `record` inmutable con validación |
| **Query** | `GetFavoriteSourceByIdQuery`, etc. — `record` inmutable con validación |
| **Repository** | `FavoriteSourceRepository` — interfaz que extiende `JpaRepository` |
| **Domain Event** | Habilitado mediante `AbstractAggregateRoot` (Spring Data domain events) |
| **Shared Kernel** | Módulo `shared` con `Result<T,E>`, naming strategy, exception handler |

### 3.2 Puertos y Adaptadores (Hexagonal)

El proyecto separa claramente:

- **Puertos (interfaces)** → `application/commandservices/` y `application/queryservices/` definen qué operaciones existen
- **Adaptadores de entrada** → `interfaces/rest/` traduce HTTP a comandos/queries del dominio
- **Adaptadores de salida** → `infrastructure/persistence/jpa/` implementa la persistencia
- **Núcleo de dominio** → `domain/model/` no depende de frameworks externos

### 3.3 Separación CQRS (Command Query Responsibility Segregation)

- **Command side**: `commandservices/` — operaciones que mutan estado, retornan `Result<T,E>`
- **Query side**: `queryservices/` — operaciones de solo lectura, retornan `Optional<T>` o `List<T>`
- Las implementaciones de comandos usan `@Transactional` (read/write)
- Las implementaciones de queries usan `@Transactional(readOnly = true)`

---

## 4. El Módulo `shared` — La Columna Vertebral

> **Esta es la parte más importante para replicar.** El módulo `shared` contiene código que todo Bounded Context puede usar sin acoplarse a detalles de ningún contexto específico.

### 4.1 Estructura interna de `shared`

```
shared/
├── application/
│   └── result/
│       └── Result.java
├── infrastructure/
│   └── persistence/jpa/strategy/
│       └── SnakeCasePhysicalNamingStrategy.java
└── interfaces/
    └── rest/
        └── GlobalExceptionHandler.java
```

### 4.2 `Result<T, E>` — Resultado Genérico de Operaciones

**Ubicación**: `shared/application/result/Result.java`

Es el patrón más importante del shared kernel. Modela resultados de comandos como **Success** o **Failure** sin introducir conceptos HTTP en la capa de aplicación.

```java
// Definición: sealed interface con dos implementaciones permitidas
public sealed interface Result<T, E> permits Result.Success, Result.Failure {

    // Fábricas estáticas
    static <T, E> Result<T, E> success(T value) { ... }
    static <T, E> Result<T, E> failure(E error) { ... }

    // Métodos de consulta
    boolean isSuccess();
    boolean isFailure();
    Optional<T> success();
    Optional<E> failure();

    // fold: transforma éxito o fracaso en un solo tipo de salida
    <R> R fold(Function<T, R> onSuccess, Function<E, R> onFailure);

    // Implementaciones como records
    record Success<T, E>(T value) implements Result<T, E> {}
    record Failure<T, E>(E error) implements Result<T, E> {}
}
```

**Características clave**:
- Usa **sealed interface** de Java → el compilador sabe todos los subtipos posibles
- Usa **records** → inmutabilidad garantizada
- Método **`fold`** → elimina la necesidad de `if/else` o `instanceof` en el caller
- Genérico en `<T, E>` → reutilizable en cualquier bounded context

**Ejemplo de uso en `news`**:

```java
// En FavoriteSourceCommandService (contrato)
Result<FavoriteSource, FavoriteSourceCommandFailure> handle(CreateFavoriteSourceCommand command);

// En FavoriteSourceCommandServiceImpl (implementación)
if (existeDuplicado) {
    return Result.failure(new FavoriteSourceCommandFailure.Duplicate());
}
return Result.success(favoriteSourceRepository.save(nuevo));
```

**Ejemplo de `fold` en el assembler HTTP**:

```java
public static ResponseEntity<?> toResponseEntityFromResult(
        Result<FavoriteSource, FavoriteSourceCommandFailure> result,
        MessageSource messageSource) {
    return result.fold(
        source -> new ResponseEntity<>(toResource(source), CREATED),       // éxito → 201
        failure -> ResponseEntity.status(statusFromFailure(failure))       // fallo → 409/400
                .body(ProblemDetail.forStatusAndDetail(...))
    );
}
```

### 4.3 `SnakeCasePhysicalNamingStrategy` — Convención de Nombrado en BD

**Ubicación**: `shared/infrastructure/persistence/jpa/strategy/SnakeCasePhysicalNamingStrategy.java`

Implementa `PhysicalNamingStrategy` de Hibernate para:

- Convertir nombres de columnas/tablas de `camelCase` a `snake_case`
- Pluralizar nombres de tablas automáticamente (usando la librería `pluralize`)
- Ejemplo: `FavoriteSource` → tabla `favorite_sources`, campo `newsApiKey` → columna `news_api_key`

### 4.4 `GlobalExceptionHandler` — Traducción de Errores a HTTP

**Ubicación**: `shared/interfaces/rest/GlobalExceptionHandler.java`

Usa `@RestControllerAdvice` para interceptar excepciones del dominio y traducirlas a respuestas HTTP con `ProblemDetail` (RFC 9457):

| Excepción | HTTP Status |
|---|---|
| `IllegalArgumentException` | `400 Bad Request` |
| `MethodArgumentNotValidException` | `400 Bad Request` |

Los mensajes de error se **localizan** usando `MessageSource` según el `Locale` de la request.

### 4.5 Dependencias del módulo `shared`

```
shared
  ├── application/result/Result.java
  │     └── (sin dependencias externas — Java puro)
  ├── infrastructure/.../SnakeCasePhysicalNamingStrategy.java
  │     ├── org.hibernate.boot.model.naming.PhysicalNamingStrategy
  │     └── io.github.encryptorcode:pluralize
  └── interfaces/rest/GlobalExceptionHandler.java
        ├── org.springframework.context.MessageSource
        ├── org.springframework.web.bind.annotation.*
        └── lombok.extern.slf4j
```

---

## 5. El Módulo `news` — Bounded Context de Ejemplo

### 5.1 Modelo de Dominio

#### Agregado: `FavoriteSource`

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class FavoriteSource extends AbstractAggregateRoot<FavoriteSource> {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = NewsApiKeyAttributeConverter.class)
    private NewsApiKey newsApiKey;         // Value Object

    @Convert(converter = SourceIdAttributeConverter.class)
    private SourceId sourceId;             // Value Object

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    protected FavoriteSource() {}          // Constructor vacío requerido por JPA

    public FavoriteSource(CreateFavoriteSourceCommand command) {  // Factory method
        this.newsApiKey = command.newsApiKey();
        this.sourceId = command.sourceId();
    }
}
```

**Reglas observadas**:
- Constructor `protected` sin argumentos para JPA
- Constructor público que recibe un **Command** (no DTOs de infraestructura)
- Value Objects se persisten vía `@Convert` con `AttributeConverter`
- `AbstractAggregateRoot` habilita eventos de dominio de Spring Data
- `@CreatedDate` / `@LastModifiedDate` con `AuditingEntityListener`

#### Value Objects: `NewsApiKey` y `SourceId`

```java
public record NewsApiKey(String value) {
    private static final int MAX_LENGTH = 256;
    private static final Pattern ALLOWED_PATTERN = Pattern.compile("^[A-Za-z0-9._:-]+$");

    public NewsApiKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("favorite.source.error.newsApiKey.notBlank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("favorite.source.error.newsApiKey.size");
        }
        if (!ALLOWED_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("favorite.source.error.newsApiKey.pattern");
        }
    }
}
```

**Reglas observadas**:
- Usan **Java `record`** → inmutabilidad, equals/hashCode/toString automáticos
- **Validación en el constructor canónico** del record
- Lanzan `IllegalArgumentException` con **message keys** (no mensajes hardcodeados) para i18n
- Constantes de validación (`MAX_LENGTH`, `PATTERN`) documentadas como `private static final`

### 5.2 Capa de Aplicación

#### Contrato de Servicio (Interfaz pública)

```java
public interface FavoriteSourceCommandService {
    Result<FavoriteSource, FavoriteSourceCommandFailure> handle(CreateFavoriteSourceCommand command);
}
```

#### Tipos de Falla como sealed interface

```java
public sealed interface FavoriteSourceCommandFailure
        permits FavoriteSourceCommandFailure.Duplicate {

    String messageKey();  // clave i18n del mensaje de error

    record Duplicate() implements FavoriteSourceCommandFailure {
        public String messageKey() { return "favorite.source.error.duplicate"; }
    }
}
```

#### Implementación en `internal/`

```java
@Service
public class FavoriteSourceCommandServiceImpl implements FavoriteSourceCommandService {
    // La anotación @Service está en la IMPLEMENTACIÓN, no en la interfaz
    // La interfaz no tiene anotaciones Spring → se mantiene pura
}
```

**Regla**: La carpeta `internal/` existe para que las implementaciones **no sean expuestas** fuera del bounded context. Otros contextos solo dependen de la interfaz.

### 5.3 Capa de Infraestructura

#### Repositorio

```java
@Repository
public interface FavoriteSourceRepository extends JpaRepository<FavoriteSource, Long> {
    List<FavoriteSource> findAllByNewsApiKey(NewsApiKey newsApiKey);
    boolean existsByNewsApiKeyAndSourceId(NewsApiKey newsApiKey, SourceId sourceId);
    Optional<FavoriteSource> findByNewsApiKeyAndSourceId(NewsApiKey newsApiKey, SourceId sourceId);
}
```

**Regla**: El repositorio trabaja con **Value Objects** (`NewsApiKey`, `SourceId`), no con `String`. Los converters de JPA hacen la traducción.

#### Converters JPA

```java
@Converter
public class NewsApiKeyAttributeConverter implements AttributeConverter<NewsApiKey, String> {
    @Override
    public String convertToDatabaseColumn(NewsApiKey attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public NewsApiKey convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new NewsApiKey(dbData);  // validación al leer de BD
    }
}
```

### 5.4 Capa de Interfaces (REST)

#### Resources (DTOs)

- **Request resource**: `CreateFavoriteSourceResource` — `record` con validaciones Jakarta
- **Response resource**: `FavoriteSourceResource` — `record` con anotaciones Swagger

#### Assemblers (Transformers)

Cada assembler tiene una responsabilidad única y métodos `static`:

| Assembler | Responsabilidad |
|---|---|
| `NewsValueObjectFromStringAssembler` | `String` → `NewsApiKey` / `SourceId` |
| `CreateFavoriteSourceCommandFromResourceAssembler` | `CreateFavoriteSourceResource` → `CreateFavoriteSourceCommand` |
| `favoriteSourceResourceFromEntityAssembler` | `FavoriteSource` (entidad) → `FavoriteSourceResource` |
| `ResponseEntityFromFavoriteSourceCommandResultAssembler` | `Result<FavoriteSource, Failure>` → `ResponseEntity<?>` |
| `ResponseEntityFromSourceQueryResultAssembler` | Resultados de query → `ResponseEntity<?>` |

---

## 6. Flujo de Dependencias

### 6.1 Regla fundamental

```
interfaces → application → domain
     ↓            ↓
infrastructure ───┘
```

**La dependencia siempre fluye hacia adentro.** La capa de dominio **NO depende** de ninguna otra capa.

### 6.2 Dependencias entre módulos

```
┌─────────────────────────────────────────────┐
│                  shared                      │
│  (Result, ExceptionHandler, NamingStrategy) │
└──────────────┬──────────────────────────────┘
               │ importa
    ┌──────────▼──────────┐
    │        news          │
    │  (FavoriteSource)   │
    └─────────────────────┘
```

- `news` **importa de** `shared` (ej: `Result<T,E>`)
- `shared` **NO importa de** `news` ni de ningún otro bounded context
- Los bounded contexts son independientes entre sí

### 6.3 Diagrama de dependencias entre capas

```
┌──────────────────────────────────────────────────────────────┐
│                    interfaces/rest                            │
│  resources/  ←── DTOs (Jakarta Validation + Swagger)         │
│  transform/  ←── Assemblers (mappers estáticos)              │
│      │                                                       │
│      │ depende de                                            │
│      ▼                                                       │
├──────────────────────────────────────────────────────────────┤
│                    application/                                │
│  commandservices/  ←── interfaces (contratos de casos de uso)│
│  queryservices/    ←── interfaces (contratos de queries)     │
│  internal/         ←── @Service implementaciones             │
│      │                                                       │
│      │ depende de                                            │
│      ▼                                                       │
├──────────────────────────────────────────────────────────────┤
│                    domain/model/                               │
│  aggregates/   ←── @Entity (raíces de agregados)             │
│  valueobjects/ ←── records inmutables con validación         │
│  commands/     ←── records (comandos)                        │
│  queries/      ←── records (consultas)                       │
│      ▲                                                       │
│      │ es usado por                                          │
├──────┴───────────────────────────────────────────────────────┤
│               infrastructure/persistence/jpa/                  │
│  XxxRepository.java  ←── Spring Data JPA                     │
│  converters/         ←── JPA AttributeConverters              │
└──────────────────────────────────────────────────────────────┘
```

---

## 7. Patrones de Diseño Identificados

| Patrón | Dónde se usa | Propósito |
|--------|-------------|-----------|
| **Result / Either Monad** | `shared/.../Result.java` | Modelar éxito/fallo sin excepciones, permitiendo `fold` |
| **Sealed Interface + Records** | `Result`, `FavoriteSourceCommandFailure` | Sum types algebraicos — el compilador garantiza que todos los casos están cubiertos |
| **CQRS** | `commandservices/` vs `queryservices/` | Separar lecturas de escrituras; optimizar cada lado |
| **Factory Method** | `FavoriteSource(CreateFavoriteSourceCommand)` | El agregado se construye desde un comando, no con setters |
| **Assembler / Mapper** | `transform/` | Traducir entre capas sin acoplamiento ni frameworks de mapping |
| **Attribute Converter** | `infrastructure/.../converters/` | Convertir Value Objects ↔ columnas de BD |
| **Strategy** | `SnakeCasePhysicalNamingStrategy` | Comportamiento de nombrado de BD reemplazable |
| **Global Exception Handler** | `GlobalExceptionHandler` | Traducción centralizada de excepciones de dominio a HTTP |
| **Repository (DDD)** | `FavoriteSourceRepository` | Abstracción de colección para el agregado |
| **Domain Events** | `AbstractAggregateRoot` | Eventos de dominio para desacoplar side-effects |
| **Value Object** | `NewsApiKey`, `SourceId` | Inmutabilidad, validación propia, igualdad por valor |
| **Shared Kernel** | Módulo `shared` | Código común entre bounded contexts sin duplicación |

---

## 8. Cómo Replicar Esta Estructura en un Proyecto Nuevo

### Paso 1 — Crear la estructura base

```
src/main/java/<tu-paquete>/
├── <nombre-app>Application.java
└── shared/
    ├── application/result/Result.java
    ├── infrastructure/persistence/jpa/strategy/SnakeCasePhysicalNamingStrategy.java
    └── interfaces/rest/GlobalExceptionHandler.java
```

### Paso 2 — Definir el primer Bounded Context

Para cada contexto nuevo (`users`, `orders`, `payments`, etc.), crear la estructura:

```
<contexto>/
├── application/
│   ├── commandservices/       ← interfaces públicas
│   ├── queryservices/         ← interfaces públicas
│   └── internal/
│       ├── commandservices/   ← implementaciones
│       └── queryservices/     ← implementaciones
├── domain/model/
│   ├── aggregates/            ← entidades JPA raíz
│   ├── commands/              ← records inmutables
│   ├── queries/               ← records inmutables
│   └── valueobjects/          ← records con validación
├── infrastructure/persistence/jpa/
│   ├── XxxRepository.java
│   └── converters/
└── interfaces/rest/
    ├── resources/             ← DTOs request/response
    └── transform/             ← assemblers
```

### Paso 3 — Seguir reglas estrictas

**Regla 1: Value Objects como records**
```java
public record Email(String value) {
    public Email {
        if (value == null || !value.contains("@"))
            throw new IllegalArgumentException("email.invalid");
    }
}
```

**Regla 2: Commands y Queries como records**
```java
public record CreateUserCommand(Email email, String name) {
    public CreateUserCommand {
        Objects.requireNonNull(email, "email.null");
        Objects.requireNonNull(name, "name.null");
    }
}
```

**Regla 3: Servicios de aplicación con `Result<T,E>`**
```java
public interface UserCommandService {
    Result<User, UserCommandFailure> handle(CreateUserCommand command);
}
```

**Regla 4: `internal/` para implementaciones**
```java
// En application/internal/commandservices/
@Service
class UserCommandServiceImpl implements UserCommandService { ... }
```

**Regla 5: Assemblers estáticos para traducción entre capas**
```java
public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User entity) {
        return new UserResource(entity.getId(), entity.getEmail().value(), entity.getName());
    }
}
```

**Regla 6: El dominio NUNCA importa de Spring, JPA, o capas externas**
- `domain/model/` solo puede importar de: `java.*`, `shared/*`
- Las entidades `@Entity` son la excepción controlada (usan anotaciones JPA pero no lógica de negocio)

---

## 9. Checklist de Reglas

| # | Regla | Verificación |
|---|-------|-------------|
| 1 | Cada Bounded Context replica la estructura `application/` `domain/` `infrastructure/` `interfaces/` | ¿El nuevo contexto tiene las 4 carpetas? |
| 2 | El dominio no importa de Spring, JPA, ni capas externas | Revisar imports en `domain/model/` |
| 3 | Los Value Objects son `record` con validación en el constructor | ¿Lanza `IllegalArgumentException` con message key? |
| 4 | Los Commands/Queries son `record` inmutables | ¿Tiene solo campos `final`? ¿Validación en el constructor compacto? |
| 5 | Las interfaces de servicio están en `application/commandservices/` (o `queryservices/`) | ¿Sin anotaciones Spring? |
| 6 | Las implementaciones están en `application/internal/` | ¿Tiene `@Service` y `@Transactional`? |
| 7 | Los comandos retornan `Result<T, E>` | ¿El tipo de fallo es un `sealed interface`? |
| 8 | Los assemblers son clases con métodos `static` | ¿Sin estado, sin anotaciones Spring? |
| 9 | Los converters JPA están en `infrastructure/persistence/jpa/converters/` | ¿Implementan `AttributeConverter<ValueObject, String>`? |
| 10 | Los Resources REST son `record` con validación Jakarta + Swagger | ¿Usan `@Schema`, `@NotBlank`, `@Size`? |
| 11 | `shared` no importa de ningún bounded context | Revisar imports de `shared/` |
| 12 | Los mensajes de error son claves i18n, no strings literales | ¿El `IllegalArgumentException` recibe una key como `"favorite.source.error.duplicate"`? |

---

## Resumen de Dependencias Clave (`pom.xml`)

| Dependencia | Uso |
|---|---|
| `spring-boot-starter-data-jpa` | Persistencia con Hibernate |
| `spring-boot-starter-web` | REST controllers |
| `spring-boot-starter-validation` | Jakarta Bean Validation en Resources |
| `springdoc-openapi-starter-webmvc-ui` | Documentación OpenAPI / Swagger |
| `lombok` | `@Slf4j`, `@Getter`, reducción de boilerplate |
| `mysql-connector-j` | Driver JDBC para MySQL |
| `pluralize` | Pluralización de nombres de tablas en BD |

---

Este documento puede ser usado como plantilla de referencia para cualquier proyecto nuevo que desee seguir la misma arquitectura DDD + Puertos y Adaptadores con Spring Boot. La clave es mantener la disciplina en la separación de capas y la dirección de las dependencias: **siempre hacia adentro, nunca hacia afuera.**
