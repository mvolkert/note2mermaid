# ADR-006: Arazzo Specification for Workflows

**Status:** Accepted  
**Date:** 2024

## Context

Documentation of API workflows and typical usage sequences.

## Decision

Arazzo 1.0.1 Specification in `src/main/resources/static/arazzo.yaml`.

## Rationale

- Complements OpenAPI with workflow descriptions
- Documents typical usage sequences
- Machine-readable for potential code generation
- Shows how endpoints work together

## Consequences

### Defined Workflows

| Workflow ID | Description |
|-------------|-------------|
| `createAndManageNote` | CRUD operations |
| `captureImageAndCreateNote` | Camera → AI → Note |
| `listAllNotes` | Retrieve all notes |
| `updateNoteContent` | Update a note |
| `deleteNote` | Delete a note |
| `sketchToMermaid` | Sketch to Mermaid diagram |

### File Structure

```yaml
arazzo: 1.0.1
info:
  title: note2mermaid API Workflows
  version: 1.0.0

sourceDescriptions:
  - name: note2mermaidApi
    url: /v3/api-docs
    type: openapi

workflows:
  - workflowId: captureImageAndCreateNote
    summary: Capture image and create note with AI
    steps:
      - stepId: analyzeAndCreateNote
        operationId: createNoteFromImage
        # ...
```

### operationId Convention

The `operationId` must match the Java method name:

- `getAllNotes`
- `getNoteById`
- `createNote`
- `updateNote`
- `deleteNote`
- `createNoteFromImage`

### See Also

- Skill: `update-api-docs` for automatic updates
- ADR-005 for OpenAPI documentation
