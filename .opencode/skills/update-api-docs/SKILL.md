---
name: update-api-docs
description: Updates OpenAPI annotations and Arazzo workflows when REST endpoints are added or modified
license: MIT
compatibility: opencode
metadata:
  audience: developers
  workflow: api-documentation
---

# Update API Documentation Skill

This skill is automatically activated when REST endpoints are added, modified, or deleted.

## When to Use This Skill

- A new REST endpoint is created
- An existing endpoint is modified (URL, parameters, response)
- An endpoint is deleted
- Request/response DTOs are changed

## Tasks

### 1. Update OpenAPI Annotations

For each controller endpoint, the following annotations must be present:

```java
@Operation(
    summary = "Short description (max 120 characters)",
    description = "Detailed description with Markdown support"
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Success", 
        content = @Content(schema = @Schema(implementation = ResponseClass.class))),
    @ApiResponse(responseCode = "404", description = "Not found", 
        content = @Content)
})
```

**For Path Parameters:**
```java
@Parameter(description = "Description", required = true, example = "1")
@PathVariable Long id
```

**For Request Bodies:**
```java
@io.swagger.v3.oas.annotations.parameters.RequestBody(
    description = "Description",
    required = true,
    content = @Content(schema = @Schema(implementation = RequestClass.class))
)
@RequestBody MyRequest request
```

### 2. Entity/DTO Schema Annotations

For all fields in entities and DTOs:

```java
@Schema(description = "Description of the field", example = "Example value")
private String fieldName;
```

For enums:
```java
@Schema(description = "Type of content", enumAsRef = true)
private ContentType contentType;
```

### 3. Update Arazzo Workflows

The file `src/main/resources/static/arazzo.yaml` must be updated:

**For a new endpoint:**
1. Check if an existing workflow should be extended
2. Or create a new workflow if it's a new use case

**Workflow Structure:**
```yaml
- workflowId: camelCaseWorkflowName
  summary: Short description
  description: |
    Detailed description of the workflow.
    What does the workflow do? When is it used?
  inputs:
    type: object
    properties:
      inputName:
        type: string
        description: Description
    required:
      - inputName
  steps:
    - stepId: stepName
      description: What this step does
      operationId: controllerMethodName  # must match @Operation
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

**operationId Convention:**
- Must match the Java method name
- e.g., `getAllNotes`, `getNoteById`, `createNote`, `updateNote`, `deleteNote`

### 4. Verification

After the changes:

1. **Start app:** `./mvnw spring-boot:run`
2. **Check OpenAPI JSON:** `curl http://localhost:8080/v3/api-docs | jq .`
3. **Test Swagger UI:** http://localhost:8080/swagger-ui.html
4. **Validate Arazzo:** Check YAML syntax

## Example: Adding a New Endpoint

If a new endpoint `/api/notes/{id}/tags` is added:

### Controller Change:
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

### Arazzo Extension:
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

## Checklist

- [ ] `@Operation` with summary and description
- [ ] `@ApiResponses` for all possible status codes
- [ ] `@Parameter` for all path/query parameters
- [ ] `@Schema` on new DTOs/entities
- [ ] Arazzo workflow created/updated
- [ ] operationId matches method name
- [ ] App starts without errors
- [ ] Swagger UI displays endpoint correctly

## Reference Files

- **Controller:** `src/main/java/com/mvolkert/note2mermaid/controller/NoteController.java`
- **Entity:** `src/main/java/com/mvolkert/note2mermaid/entity/Note.java`
- **DTOs:** `src/main/java/com/mvolkert/note2mermaid/dto/`
- **Arazzo:** `src/main/resources/static/arazzo.yaml`
- **OpenAPI Config:** SpringDoc auto-configured via `pom.xml`
