# ADR-004: Static Frontend without Framework

**Status:** Accepted  
**Date:** 2024

## Context

Choosing frontend technology for the webapp.

## Decision

Vanilla HTML/CSS/JavaScript with Mermaid.js for diagram rendering.

## Rationale

- Simplicity and fast development
- No build tools or Node.js required
- Mermaid.js for diagram rendering directly in browser
- No framework overhead for a simple UI

## Consequences

### Project Structure

```
src/main/resources/static/
├── index.html          # Start page with tabs
├── css/
│   └── style.css       # Styling
└── js/                 # (optional) JavaScript modules
```

### No State Management

- Manual DOM manipulation
- Fetch API for backend communication
- Simple event handlers

### Mermaid.js Integration

```html
<script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
<script>mermaid.initialize({ startOnLoad: true });</script>
```

### Features

- **Tabs:** Text input or camera capture
- **Camera:** WebRTC/getUserMedia for laptop camera
- **Preview:** Photo preview before upload
- **Note List:** Card view with delete button
