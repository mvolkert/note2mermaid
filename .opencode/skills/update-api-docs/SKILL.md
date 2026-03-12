---
name: update-api-docs
description: Aktualisiert OpenAPI-Annotationen und Arazzo-Workflows wenn REST-Endpoints hinzugefuegt oder geaendert werden
license: MIT
compatibility: opencode
metadata:
  audience: developers
  workflow: api-documentation
---

# Update API Documentation Skill

Dieser Skill wird automatisch aktiviert, wenn REST-Endpoints hinzugefügt, geändert oder gelöscht werden.

## Wann diesen Skill verwenden

- Neuer REST-Endpoint wird erstellt
- Bestehender Endpoint wird modifiziert (URL, Parameter, Response)
- Endpoint wird gelöscht
- Request/Response DTOs werden geändert

## Aufgaben

### 1. OpenAPI-Annotationen aktualisieren

Für jeden Controller-Endpoint müssen folgende Annotationen vorhanden sein:

```java
@Operation(
    summary = "Kurze Beschreibung (max 120 Zeichen)",
    description = "Ausführliche Beschreibung mit Markdown-Support"
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Erfolg", 
        content = @Content(schema = @Schema(implementation = ResponseClass.class))),
    @ApiResponse(responseCode = "404", description = "Nicht gefunden", 
        content = @Content)
})
```

**Für Path-Parameter:**
```java
@Parameter(description = "Beschreibung", required = true, example = "1")
@PathVariable Long id
```

**Für Request Bodies:**
```java
@io.swagger.v3.oas.annotations.parameters.RequestBody(
    description = "Beschreibung",
    required = true,
    content = @Content(schema = @Schema(implementation = RequestClass.class))
)
@RequestBody MyRequest request
```

### 2. Entity/DTO Schema-Annotationen

Für alle Felder in Entities und DTOs:

```java
@Schema(description = "Beschreibung des Feldes", example = "Beispielwert")
private String fieldName;
```

Für Enums:
```java
@Schema(description = "Art des Inhalts", enumAsRef = true)
private ContentType contentType;
```

### 3. Arazzo-Workflows aktualisieren

Die Datei `src/main/resources/static/arazzo.yaml` muss aktualisiert werden:

**Bei neuem Endpoint:**
1. Prüfen ob ein bestehender Workflow erweitert werden sollte
2. Oder einen neuen Workflow erstellen wenn es ein neuer Use-Case ist

**Workflow-Struktur:**
```yaml
- workflowId: camelCaseWorkflowName
  summary: Kurze Beschreibung
  description: |
    Ausführliche Beschreibung des Workflows.
    Was macht der Workflow? Wann wird er verwendet?
  inputs:
    type: object
    properties:
      inputName:
        type: string
        description: Beschreibung
    required:
      - inputName
  steps:
    - stepId: stepName
      description: Was dieser Schritt macht
      operationId: controllerMethodName  # muss mit @Operation übereinstimmen
      parameters:
        - name: id
          in: path
          value: $inputs.noteId
      requestBody:
        contentType: application/json
        payload:
          field: $inputs.fieldValue
      successCriteria:
        - condition: $statusCode == 200
      outputs:
        result: $response.body
  outputs:
    finalOutput: $steps.stepName.outputs.result
```

**operationId-Konvention:**
- Muss dem Java-Methodennamen entsprechen
- z.B. `getAllNotes`, `getNoteById`, `createNote`, `updateNote`, `deleteNote`

### 4. Verifikation

Nach den Änderungen:

1. **App starten:** `./mvnw spring-boot:run`
2. **OpenAPI JSON prüfen:** `curl http://localhost:8080/v3/api-docs | jq .`
3. **Swagger UI testen:** http://localhost:8080/swagger-ui.html
4. **Arazzo validieren:** YAML-Syntax prüfen

## Beispiel: Neuen Endpoint hinzufügen

Wenn ein neuer Endpoint `/api/notes/{id}/tags` hinzugefügt wird:

### Controller-Änderung:
```java
@Operation(
    summary = "Get tags for a note",
    description = "Retrieves all tags associated with a specific note."
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Tags retrieved"),
    @ApiResponse(responseCode = "404", description = "Note not found")
})
@GetMapping("/{id}/tags")
public ResponseEntity<List<String>> getNoteTags(
        @Parameter(description = "Note ID", required = true)
        @PathVariable Long id) {
    // ...
}
```

### Arazzo-Erweiterung:
```yaml
- workflowId: manageNoteTags
  summary: Manage tags on a note
  steps:
    - stepId: getTags
      operationId: getNoteTags
      parameters:
        - name: id
          in: path
          value: $inputs.noteId
```

## Checkliste

- [ ] `@Operation` mit summary und description
- [ ] `@ApiResponses` für alle möglichen Status-Codes
- [ ] `@Parameter` für alle Path/Query-Parameter
- [ ] `@Schema` auf neuen DTOs/Entities
- [ ] Arazzo-Workflow erstellt/aktualisiert
- [ ] operationId stimmt mit Methodenname überein
- [ ] App startet ohne Fehler
- [ ] Swagger UI zeigt Endpoint korrekt an

## Referenz-Dateien

- **Controller:** `src/main/java/com/mvolkert/note2mermaid/controller/NoteController.java`
- **Entity:** `src/main/java/com/mvolkert/note2mermaid/entity/Note.java`
- **DTOs:** `src/main/java/com/mvolkert/note2mermaid/dto/`
- **Arazzo:** `src/main/resources/static/arazzo.yaml`
- **OpenAPI Config:** SpringDoc auto-konfiguriert via `pom.xml`
