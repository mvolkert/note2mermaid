---
name: update-documentation
description: Updates documentation, comments, and README when code changes are made - ensures everything is in English
license: MIT
compatibility: opencode
metadata:
  audience: developers
  workflow: documentation
---

# Update Documentation Skill

This skill ensures all documentation stays up-to-date and in English when code changes are made.

## Language Policy

**IMPORTANT: All documentation and code comments must be in English.**

This includes:
- README.md
- CONTEXT.md
- AGENTS.md
- All ADR files in `docs/adr/`
- All code comments (JavaDoc, inline comments)
- Commit messages
- OpenAPI descriptions and annotations

## When to Use This Skill

- New feature is implemented
- Existing functionality is modified
- New configuration options are added
- API endpoints are changed (also use `update-api-docs` skill)
- Architecture decisions are made
- Bug fixes that change behavior

## Tasks

### 1. Update README.md

The README should always reflect the current state of the project:

**Sections to check:**
- Project description and features
- Prerequisites and installation
- Usage instructions
- Configuration options
- API documentation links

**Template for new features:**
```markdown
## Features

- **Feature Name**: Brief description of what it does
```

### 2. Update CONTEXT.md

This file provides context for AI agents and developers:

**Sections to update:**
- Repository structure (if files/folders changed)
- Existing endpoints (if API changed)
- Configuration (if new properties added)
- Known issues & solutions (if new workarounds discovered)
- Next steps (check off completed items, add new ones)

### 3. Update Code Comments

All comments must be in English:

**JavaDoc for public methods:**
```java
/**
 * Analyzes an image and returns the recognized content.
 * For diagrams, Mermaid code is generated; for text, OCR is performed.
 *
 * @param imageData The image as byte array
 * @param mimeType The MIME type of the image
 * @return Analysis result containing type and content
 */
public ImageAnalysisResult analyzeImage(byte[] imageData, String mimeType) {
```

**Inline comments:**
```java
// Convert Base64 to byte[]
byte[] imageBytes = Base64.getDecoder().decode(request.getImageData());

// Analyze image using Vision LLM
ImageAnalysisResult result = imageAnalysisService.analyzeImage(imageBytes, mimeType);
```

### 4. Update ADRs (Architecture Decision Records)

When making architecture decisions, create or update ADRs in `docs/adr/`:

**ADR Template:**
```markdown
# ADR-XXX: Title of Decision

**Status:** Accepted  
**Date:** YYYY-MM-DD

## Context

Why was a decision needed?

## Decision

What was decided?

## Rationale

Why this decision?

## Consequences

What follows from this?
```

**Also update `docs/adr/README.md` index table.**

### 5. Update AGENTS.md

When coding conventions or guidelines change:

**Sections:**
- Console/Terminal instructions
- Architecture Decisions table
- Coding Conventions
- Skills list

## Checklist

After making code changes, verify:

- [ ] README.md reflects new features/changes
- [ ] CONTEXT.md is updated with new structure/endpoints
- [ ] All code comments are in English
- [ ] JavaDoc is complete for public methods
- [ ] ADR created if architecture decision was made
- [ ] docs/adr/README.md index updated
- [ ] AGENTS.md updated if conventions changed
- [ ] No German text remains in documentation or comments

## Translation Guide

If you find German text, translate it:

| German | English |
|--------|---------|
| Beschreibung | Description |
| Konfiguration | Configuration |
| Endpunkt | Endpoint |
| Anfrage | Request |
| Antwort | Response |
| Fehler | Error |
| Beispiel | Example |
| Datei | File |
| Ordner | Directory/Folder |
| erstellen | create |
| aktualisieren | update |
| löschen | delete |
| abrufen | retrieve/get |

## Reference Files

- **README:** `README.md`
- **Context:** `CONTEXT.md`
- **Agent Guidelines:** `AGENTS.md`
- **ADR Index:** `docs/adr/README.md`
- **ADR Files:** `docs/adr/XXX-*.md`
- **Java Sources:** `src/main/java/com/mvolkert/note2mermaid/`

## Example: New Feature Documentation

When adding a "tag management" feature:

### 1. README.md
```markdown
## Features
- **Tag Management**: Organize notes with custom tags for easy filtering
```

### 2. CONTEXT.md
```markdown
## Existing Endpoints
| POST | `/api/notes/{id}/tags` | Add tags to a note |
| GET | `/api/notes/{id}/tags` | Get tags for a note |
| DELETE | `/api/notes/{id}/tags/{tag}` | Remove tag from note |
```

### 3. ADR (if applicable)
Create `docs/adr/009-tag-storage.md` for the decision on how tags are stored.

### 4. Code Comments
```java
/**
 * Adds tags to an existing note.
 *
 * @param id Note identifier
 * @param tags List of tags to add
 * @return Updated note with tags
 */
@PostMapping("/{id}/tags")
public ResponseEntity<Note> addTags(@PathVariable Long id, @RequestBody List<String> tags) {
    // Validate tags (no duplicates, max length)
    // ...
}
```
