# Agents Guidelines

## Console/Terminal

For all AI agents: **Always use Git Bash as console.**

This ensures that Unix-compatible commands work consistently and cross-platform compatibility is guaranteed.

---

## Language Policy

**All documentation and code must be in English.** This includes:

- README.md, CONTEXT.md, AGENTS.md
- All ADR files in `docs/adr/`
- Code comments (JavaDoc, inline comments)
- Commit messages
- OpenAPI descriptions

---

## Architecture Decisions (ADRs)

Complete ADRs are located in [`docs/adr/`](docs/adr/README.md).

| ADR | Decision | Key Consequence |
|-----|----------|-----------------|
| [001](docs/adr/001-spring-boot.md) | Spring Boot 4.0 + Java 17 | `@MockBean` → `@MockitoBean` |
| [002](docs/adr/002-sqlite.md) | SQLite + Liquibase | Do not use `@Lob` |
| [003](docs/adr/003-langchain4j-lmstudio.md) | LangChain4j + LM Studio | Port 1234, load Vision model |
| [004](docs/adr/004-static-frontend.md) | Vanilla HTML/JS + Mermaid.js | No build tools |
| [005](docs/adr/005-springdoc-openapi.md) | SpringDoc OpenAPI | `/swagger-ui.html` |
| [006](docs/adr/006-arazzo-workflows.md) | Arazzo Workflows | `static/arazzo.yaml` |
| [007](docs/adr/007-mockito-tests.md) | Pure Mockito Tests | No `@WebMvcTest` |
| [008](docs/adr/008-blob-images.md) | Images as BLOB | Use lazy loading |

---

## Coding Conventions

### Java
- Package: `com.mvolkert.note2mermaid`
- Constructor injection instead of `@Autowired`
- Records for DTOs where possible
- All comments in English

### REST API
- Base path: `/api/notes`
- ResponseEntity for all endpoints
- Use correct HTTP status codes (201 for Create, 204 for Delete)

### Git
- Commits after each completed change
- English commit messages
- Conventional Commit format recommended

---

## Skills

This folder contains skills for automated tasks:

- **update-api-docs**: Updates OpenAPI annotations and Arazzo workflows when API changes
- **finalize**: Updates documentation and commits changes - only triggered by "finalize" or "commit"
