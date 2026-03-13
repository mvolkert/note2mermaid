# ADR-005: SpringDoc OpenAPI for API Documentation

**Status:** Accepted  
**Date:** 2024

## Context

Automatic API documentation for REST endpoints.

## Decision

SpringDoc OpenAPI 3 with Swagger UI.

## Rationale

- Automatic generation from controller annotations
- Interactive Swagger UI for testing
- Standard-compliant (OpenAPI 3.0)
- No manual sync between code and docs

## Consequences

### Endpoints

| URL | Description |
|-----|-------------|
| `/swagger-ui.html` | Interactive UI |
| `/v3/api-docs` | OpenAPI JSON |
| `/v3/api-docs.yaml` | OpenAPI YAML |

### Annotations

**Controller Level:**
```java
@Tag(name = "Notes", description = "API for managing notes")
```

**Method Level:**
```java
@Operation(
    summary = "Short description",
    description = "Detailed description"
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Success"),
    @ApiResponse(responseCode = "404", description = "Not found")
})
```

**Parameters:**
```java
@Parameter(description = "Note ID", required = true, example = "1")
@PathVariable Long id
```

**Entity/DTO Fields:**
```java
@Schema(description = "Title of the note", example = "My Note")
private String title;
```

### Dependency

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.6</version>
</dependency>
```

### See Also

- Skill: `update-api-docs` for automatic updates
- ADR-006 for Arazzo workflow documentation
