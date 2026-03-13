# Note2Mermaid

A Spring Boot web application that analyzes handwritten notes and diagrams using AI and converts them into structured digital formats.

> **AI Disclaimer**: This project was developed with the assistance of AI (Claude/OpenCode). The code, documentation, and architecture decisions were created through human-AI collaboration.

## Features

- **Camera Capture**: Photograph handwritten notes or sketches directly with your laptop camera
- **AI Image Analysis**: Local Vision LLM recognizes and transcribes content
- **Auto-Correction**: Spelling and grammar are automatically corrected
- **Structure Recognition**: Headings, lists, and enumerations are preserved
- **Mermaid Diagrams**: Hand-drawn flowcharts are converted to Mermaid code
- **Markdown Rendering**: Structured text is displayed as formatted Markdown
- **Offline-Capable**: Runs completely locally without cloud dependency

## Technology Stack

| Component | Technology |
|-----------|------------|
| Backend | Spring Boot 4.0.3, Java 17 |
| Database | SQLite + Liquibase |
| Frontend | Vanilla HTML/CSS/JS, Mermaid.js, marked.js |
| AI | LangChain4j + LM Studio (local) |
| API Docs | SpringDoc OpenAPI, Swagger UI |

## Prerequisites

- Java 17+
- Maven 3.8+
- [LM Studio](https://lmstudio.ai/) with a Vision model (e.g., `ministral-3b`)

## Installation

### 1. Clone Repository

```bash
git clone https://github.com/mvolkert/note2mermaid.git
cd note2mermaid
```

### 2. Set Up LM Studio

1. Install and start LM Studio
2. Load a Vision model (e.g., `ministral-3b`)
3. Start the server on port 1234

### 3. Start the App

```bash
./mvnw spring-boot:run
```

The app is available at: **http://localhost:8080**

## Configuration

Configuration is done in `src/main/resources/application.properties`:

```properties
# LM Studio Server
lmstudio.base-url=http://127.0.0.1:1234/v1
lmstudio.model-name=ministral-3b

# SQLite Database
spring.datasource.url=jdbc:sqlite:note2mermaid.db
```

## API Documentation

| Endpoint | Description |
|----------|-------------|
| `GET /api/notes` | Get all notes |
| `GET /api/notes/{id}` | Get single note |
| `POST /api/notes` | Create new note |
| `PUT /api/notes/{id}` | Update note |
| `DELETE /api/notes/{id}` | Delete note |
| `POST /api/notes/from-image` | Analyze image and create note |
| `GET /api/notes/{id}/image` | Get original image of a note |

### Swagger UI

Interactive API documentation: **http://localhost:8080/swagger-ui.html**

### OpenAPI Spec

- JSON: `http://localhost:8080/v3/api-docs`
- YAML: `http://localhost:8080/v3/api-docs.yaml`

## Content Types

The AI automatically recognizes the content type:

| Type | Description | Example |
|------|-------------|---------|
| `TEXT` | Plain text | Paragraphs, notes |
| `MARKDOWN` | Structured text | Lists, headings |
| `DIAGRAM` | Diagrams | Flowcharts, mindmaps |
| `IMAGE` | Photos without text | Graphics, images |

## Development

### Run Tests

```bash
./mvnw test
```

### DevTools (Live Reload)

The app includes Spring Boot DevTools. After starting, changes to static files (HTML, CSS, JS) are automatically picked up - just refresh the browser.

### Project Structure

```
src/
├── main/
│   ├── java/com/mvolkert/note2mermaid/
│   │   ├── controller/     # REST Controllers
│   │   ├── service/        # Business Logic + AI
│   │   ├── entity/         # JPA Entities
│   │   ├── repository/     # Data Access
│   │   └── dto/            # Data Transfer Objects
│   └── resources/
│       ├── static/         # Frontend (HTML, CSS, JS)
│       ├── db/changelog/   # Liquibase Migrations
│       └── application.properties
└── test/                   # Unit Tests
```

## Architecture Decisions

Key architecture decisions are documented in [`docs/adr/`](docs/adr/README.md):

- ADR-001: Spring Boot 4.0 + Java 17
- ADR-002: SQLite as Database
- ADR-003: LangChain4j + LM Studio
- ADR-007: Pure Mockito Tests

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

Marco Volkert
