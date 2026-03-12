# Projekt-Kontext: note2mermaid

## Technologie-Stack

- **Backend:** Spring Boot 4.0.3 (Java)
- **Datenbank:** SQLite mit Liquibase Migrations
- **Frontend:** Statisches HTML/CSS/JS
- **Build:** Maven

---

## Repository-Struktur

```
note2mermaid/
├── src/main/java/com/mvolkert/note2mermaid/
│   ├── Note2mermaidApplication.java
│   ├── controller/
│   │   ├── NoteController.java    # CRUD REST API für Notes
│   │   └── HelloController.java   # Test-Endpoint
│   ├── entity/
│   │   └── Note.java              # JPA Entity (id, title, content, createdAt, updatedAt)
│   └── repository/
│       └── NoteRepository.java
├── src/main/resources/
│   ├── application.properties
│   ├── db/changelog/              # Liquibase Migrations
│   └── static/
│       ├── index.html             # Startseite mit Formular + Notizliste
│       └── css/style.css
└── pom.xml
```

---

## Bestehende Endpoints

| Methode | URL | Beschreibung |
|---------|-----|--------------|
| GET | `/api/notes` | Alle Notizen abrufen |
| GET | `/api/notes/{id}` | Einzelne Notiz |
| POST | `/api/notes` | Notiz erstellen |
| PUT | `/api/notes/{id}` | Notiz aktualisieren |
| DELETE | `/api/notes/{id}` | Notiz löschen |
| GET | `/hello` | Test-Endpoint ("hi") |

---

## Frontend

- Startseite mit Hero, Features-Sektion
- Formular zum Erstellen von Notizen
- Notizliste mit Karten-Ansicht und Löschen-Button
- Responsive CSS Design

---

## Nächstes Feature: Kamera-Bild zu Notiz

### Ziel

Bild mit Laptop-Kamera aufnehmen → Lokal analysieren → Notiz erstellen

### Entscheidungen

- **Bildanalyse:** OCR für Text, Mermaid-Code generieren für Diagramme
- **KI-Provider:** Lokales Modell (Llama Vision)
- **Tool:** LM Studio (bereits installiert)
- **Java-Integration:** LangChain4j
- **Bild speichern:** Ja, als BLOB in der Datenbank

---

## Implementierungsplan

### Phase 1: LM Studio Server einrichten

- Vision-Modell in LM Studio laden (llava oder bakllava)
- Lokalen Server starten (Port 1234)
- Endpoint: `http://localhost:1234/v1` (OpenAI-kompatibel)

### Phase 2: Backend - LangChain4j Integration

- Dependencies in pom.xml:
  - `langchain4j-core`
  - `langchain4j-open-ai` (für LM Studio Kompatibilität)
- `ImageAnalysisService` erstellen:
  - Bild (Base64) empfangen
  - LangChain4j ChatModel mit Vision aufrufen
  - Prompt unterscheidet: Text (OCR) vs. Diagramm (Mermaid-Code)
- `NoteController` erweitern:
  - `POST /api/notes/from-image` Endpoint
- Konfiguration in `application.properties`:
  - LM Studio URL

### Phase 3: Datenbank erweitern

- Note-Entity erweitern:
  - `imageData` (BLOB/byte[])
  - `imageType` (String, z.B. "image/png")
- Liquibase Migration für neue Spalten

### Phase 4: Frontend - Kamera-Integration

- Kamera-Button zur Startseite hinzufügen
- WebRTC/getUserMedia für Kamera-Stream
- Canvas-Snapshot für Foto
- Preview + Bestätigung vor Upload
- Base64-Upload an `/api/notes/from-image`
- Mermaid.js Library für Diagramm-Rendering

---

## Git Log (letzte Commits)

```
9f56702 Fix Liquibase changelog include path
f323ecc Add notes list view with delete functionality
c315a30 Add note creation form to homepage
5ecd6fd Add HelloController for testing
d725d27 Add NoteController with full CRUD REST API
ebd3357 Add Agents.md guidelines for AI agents
1fb4334 Add SQLite database files to .gitignore
994f36c Add Note entity with JPA repository and Liquibase migrations
2314f43 Add static homepage for webapp
064ac01 Initial commit: Add Maven project structure for note2mermaid
```

---

## Wiederaufsetzen

Beim Fortsetzen diesen Kontext bereitstellen und sagen:

> "Ich möchte das Kamera-zu-Notiz Feature implementieren. LM Studio ist bereit mit [Modellname]. Starte mit Phase [1/2/3/4]."
