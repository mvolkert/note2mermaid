# ADR-006: Arazzo Specification für Workflows

**Status:** Akzeptiert  
**Datum:** 2024

## Kontext

Dokumentation von API-Workflows und typischen Nutzungssequenzen.

## Entscheidung

Arazzo 1.0.1 Specification in `src/main/resources/static/arazzo.yaml`.

## Begründung

- Ergänzt OpenAPI um Workflow-Beschreibungen
- Dokumentiert typische Nutzungssequenzen
- Maschinenlesbar für potenzielle Codegen
- Zeigt wie Endpoints zusammenspielen

## Konsequenzen

### Definierte Workflows

| Workflow ID | Beschreibung |
|-------------|--------------|
| `createAndManageNote` | CRUD-Operationen |
| `captureImageAndCreateNote` | Kamera → AI → Notiz |
| `listAllNotes` | Alle Notizen abrufen |
| `updateNoteContent` | Notiz aktualisieren |
| `deleteNote` | Notiz löschen |
| `sketchToMermaid` | Skizze zu Mermaid-Diagramm |

### Datei-Struktur

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

### operationId Konvention

Die `operationId` muss dem Java-Methodennamen entsprechen:

- `getAllNotes`
- `getNoteById`
- `createNote`
- `updateNote`
- `deleteNote`
- `createNoteFromImage`

### Siehe auch

- Skill: `update-api-docs` für automatische Aktualisierung
- ADR-005 für OpenAPI-Dokumentation
