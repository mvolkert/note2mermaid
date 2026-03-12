# ADR-005: SpringDoc OpenAPI für API-Dokumentation

**Status:** Akzeptiert  
**Datum:** 2024

## Kontext

Automatische API-Dokumentation für REST-Endpoints.

## Entscheidung

SpringDoc OpenAPI 3 mit Swagger UI.

## Begründung

- Automatische Generierung aus Controller-Annotationen
- Interaktive Swagger UI zum Testen
- Standard-konform (OpenAPI 3.0)
- Keine manuelle Sync zwischen Code und Docs

## Konsequenzen

### Endpoints

| URL | Beschreibung |
|-----|--------------|
| `/swagger-ui.html` | Interaktive UI |
| `/v3/api-docs` | OpenAPI JSON |
| `/v3/api-docs.yaml` | OpenAPI YAML |

### Annotationen

**Controller-Ebene:**
```java
@Tag(name = "Notes", description = "API for managing notes")
```

**Methoden-Ebene:**
```java
@Operation(
    summary = "Kurze Beschreibung",
    description = "Ausführliche Beschreibung"
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Erfolg"),
    @ApiResponse(responseCode = "404", description = "Nicht gefunden")
})
```

**Parameter:**
```java
@Parameter(description = "Note ID", required = true, example = "1")
@PathVariable Long id
```

**Entity/DTO-Felder:**
```java
@Schema(description = "Titel der Notiz", example = "Meine Notiz")
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

### Siehe auch

- Skill: `update-api-docs` für automatische Aktualisierung
- ADR-006 für Arazzo Workflow-Dokumentation
