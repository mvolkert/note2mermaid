# ADR-004: Statisches Frontend ohne Framework

**Status:** Akzeptiert  
**Datum:** 2024

## Kontext

Frontend-Technologie für die Webapp wählen.

## Entscheidung

Vanilla HTML/CSS/JavaScript mit Mermaid.js für Diagramm-Rendering.

## Begründung

- Einfachheit und schnelle Entwicklung
- Keine Build-Tools oder Node.js nötig
- Mermaid.js für Diagramm-Rendering direkt im Browser
- Kein Framework-Overhead für eine einfache UI

## Konsequenzen

### Projektstruktur

```
src/main/resources/static/
├── index.html          # Startseite mit Tabs
├── css/
│   └── style.css       # Styling
└── js/                 # (optional) JavaScript Module
```

### Kein State-Management

- Manuelle DOM-Manipulation
- Fetch API für Backend-Kommunikation
- Einfache Event-Handler

### Mermaid.js Integration

```html
<script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
<script>mermaid.initialize({ startOnLoad: true });</script>
```

### Features

- **Tabs:** Text-Eingabe oder Kamera-Aufnahme
- **Kamera:** WebRTC/getUserMedia für Laptop-Kamera
- **Preview:** Foto-Vorschau vor Upload
- **Notizliste:** Karten-Ansicht mit Löschen-Button
