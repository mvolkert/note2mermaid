# Agents Guidelines

## Console/Terminal

Für alle KI-Agenten gilt: **Immer Git Bash als Console verwenden.**

Dies stellt sicher, dass Unix-kompatible Befehle konsistent funktionieren und plattformübergreifende Kompatibilität gewährleistet ist.

---

## Architekturentscheidungen (ADRs)

Vollständige ADRs befinden sich in [`docs/adr/`](docs/adr/README.md).

| ADR | Entscheidung | Wichtigste Konsequenz |
|-----|--------------|----------------------|
| [001](docs/adr/001-spring-boot.md) | Spring Boot 4.0 + Java 17 | `@MockBean` → `@MockitoBean` |
| [002](docs/adr/002-sqlite.md) | SQLite + Liquibase | `@Lob` nicht verwenden |
| [003](docs/adr/003-langchain4j-lmstudio.md) | LangChain4j + LM Studio | Port 1234, Vision-Modell laden |
| [004](docs/adr/004-static-frontend.md) | Vanilla HTML/JS + Mermaid.js | Keine Build-Tools |
| [005](docs/adr/005-springdoc-openapi.md) | SpringDoc OpenAPI | `/swagger-ui.html` |
| [006](docs/adr/006-arazzo-workflows.md) | Arazzo Workflows | `static/arazzo.yaml` |
| [007](docs/adr/007-mockito-tests.md) | Pure Mockito Tests | Kein `@WebMvcTest` |
| [008](docs/adr/008-blob-images.md) | Bilder als BLOB | Lazy-Loading verwenden |

---

## Coding-Konventionen

### Java
- Package: `com.mvolkert.note2mermaid`
- Konstruktor-Injection statt `@Autowired`
- Records für DTOs wo möglich
- Deutsche Kommentare erlaubt

### REST API
- Base-Path: `/api/notes`
- ResponseEntity für alle Endpoints
- HTTP Status Codes korrekt verwenden (201 für Create, 204 für Delete)

### Git
- Commits nach jeder abgeschlossenen Änderung
- Englische Commit-Messages
- Conventional Commit Format empfohlen

---

## Skills

Dieser Ordner enthält Skills für automatisierte Aufgaben:

- **update-api-docs**: Aktualisiert OpenAPI-Annotationen und Arazzo-Workflows bei API-Änderungen
