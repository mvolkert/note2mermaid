# Note2Mermaid

Eine Spring Boot Webapp, die handgeschriebene Notizen und Diagramme mit KI analysiert und in strukturierte digitale Formate umwandelt.

## Features

- **Kamera-Aufnahme**: Fotografiere handgeschriebene Notizen oder Skizzen direkt mit der Laptop-Kamera
- **KI-Bildanalyse**: Lokales Vision-LLM erkennt und transkribiert Inhalte
- **Automatische Korrektur**: Rechtschreibung und Grammatik werden korrigiert
- **Strukturerkennung**: Überschriften, Listen und Aufzählungen bleiben erhalten
- **Mermaid-Diagramme**: Handgezeichnete Flowcharts werden in Mermaid-Code umgewandelt
- **Markdown-Rendering**: Strukturierte Texte werden als formatiertes Markdown angezeigt
- **Offline-fähig**: Läuft komplett lokal ohne Cloud-Abhängigkeit

## Screenshots

```
┌─────────────────────────────────────────┐
│  Note2Mermaid                           │
├─────────────────────────────────────────┤
│  [Text eingeben] [Kamera verwenden]     │
│                                         │
│  ┌─────────────────────────────────┐    │
│  │      📷 Kamera-Vorschau         │    │
│  └─────────────────────────────────┘    │
│                                         │
│  [Foto aufnehmen]  [Analysieren]        │
└─────────────────────────────────────────┘
```

## Technologie-Stack

| Komponente | Technologie |
|------------|-------------|
| Backend | Spring Boot 4.0.3, Java 17 |
| Datenbank | SQLite + Liquibase |
| Frontend | Vanilla HTML/CSS/JS, Mermaid.js, marked.js |
| KI | LangChain4j + LM Studio (lokal) |
| API-Docs | SpringDoc OpenAPI, Swagger UI |

## Voraussetzungen

- Java 17+
- Maven 3.8+
- [LM Studio](https://lmstudio.ai/) mit einem Vision-Modell (z.B. `ministral-3b`)

## Installation

### 1. Repository klonen

```bash
git clone https://github.com/mvolkert/note2mermaid.git
cd note2mermaid
```

### 2. LM Studio einrichten

1. LM Studio installieren und starten
2. Ein Vision-Modell laden (z.B. `ministral-3b`)
3. Server starten auf Port 1234

### 3. App starten

```bash
./mvnw spring-boot:run
```

Die App ist erreichbar unter: **http://localhost:8080**

## Konfiguration

Die Konfiguration erfolgt in `src/main/resources/application.properties`:

```properties
# LM Studio Server
lmstudio.base-url=http://127.0.0.1:1234/v1
lmstudio.model-name=ministral-3b

# SQLite Datenbank
spring.datasource.url=jdbc:sqlite:note2mermaid.db
```

## API-Dokumentation

| Endpoint | Beschreibung |
|----------|--------------|
| `GET /api/notes` | Alle Notizen abrufen |
| `GET /api/notes/{id}` | Einzelne Notiz abrufen |
| `POST /api/notes` | Neue Notiz erstellen |
| `PUT /api/notes/{id}` | Notiz aktualisieren |
| `DELETE /api/notes/{id}` | Notiz löschen |
| `POST /api/notes/from-image` | Bild analysieren und Notiz erstellen |
| `GET /api/notes/{id}/image` | Originalbild einer Notiz abrufen |

### Swagger UI

Interaktive API-Dokumentation: **http://localhost:8080/swagger-ui.html**

### OpenAPI Spec

- JSON: `http://localhost:8080/v3/api-docs`
- YAML: `http://localhost:8080/v3/api-docs.yaml`

## Content-Typen

Die KI erkennt automatisch den Inhaltstyp:

| Typ | Beschreibung | Beispiel |
|-----|--------------|----------|
| `TEXT` | Einfacher Fließtext | Absätze, Notizen |
| `MARKDOWN` | Strukturierter Text | Listen, Überschriften |
| `DIAGRAM` | Diagramme | Flowcharts, Mindmaps |
| `IMAGE` | Fotos ohne Text | Grafiken, Bilder |

## Entwicklung

### Tests ausführen

```bash
./mvnw test
```

### Mit DevTools (Live-Reload)

Die App enthält Spring Boot DevTools. Nach dem Start werden Änderungen an statischen Dateien (HTML, CSS, JS) automatisch übernommen - einfach Browser refreshen.

### Projektstruktur

```
src/
├── main/
│   ├── java/com/mvolkert/note2mermaid/
│   │   ├── controller/     # REST Controller
│   │   ├── service/        # Business Logic + KI
│   │   ├── entity/         # JPA Entities
│   │   ├── repository/     # Data Access
│   │   └── dto/            # Data Transfer Objects
│   └── resources/
│       ├── static/         # Frontend (HTML, CSS, JS)
│       ├── db/changelog/   # Liquibase Migrations
│       └── application.properties
└── test/                   # Unit Tests
```

## Architekturentscheidungen

Die wichtigsten Architekturentscheidungen sind in [`docs/adr/`](docs/adr/README.md) dokumentiert:

- ADR-001: Spring Boot 4.0 + Java 17
- ADR-002: SQLite als Datenbank
- ADR-003: LangChain4j + LM Studio
- ADR-007: Pure Mockito Tests

## Lizenz

MIT

## Autor

Marco Volkert
