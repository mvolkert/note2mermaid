# Project Context: note2mermaid

## Technology Stack

- **Backend:** Spring Boot 4.0.3 (Java 17)
- **Database:** SQLite with Liquibase Migrations
- **Frontend:** Static HTML/CSS/JS + Mermaid.js
- **Build:** Maven
- **AI:** LangChain4j with LM Studio (OpenAI-compatible)

---

## Repository Structure

```
note2mermaid/
├── src/main/java/com/mvolkert/note2mermaid/
│   ├── Note2mermaidApplication.java
│   ├── controller/
│   │   ├── NoteController.java       # CRUD + /api/notes/from-image (with OpenAPI annotations)
│   │   └── HelloController.java      # Test endpoint
│   ├── entity/
│   │   └── Note.java                 # JPA Entity with image columns (with @Schema)
│   ├── repository/
│   │   └── NoteRepository.java
│   ├── service/
│   │   └── ImageAnalysisService.java # LangChain4j Vision analysis
│   └── dto/
│       └── ImageUploadRequest.java   # DTO for image upload (with @Schema)
├── src/test/java/com/mvolkert/note2mermaid/
│   └── controller/
│       └── NoteControllerTest.java   # Unit tests with Mockito
├── src/main/resources/
│   ├── application.properties        # DB + LM Studio config
│   ├── db/changelog/
│   │   ├── db.changelog-master.yaml
│   │   └── changes/
│   │       ├── 001-create-notes-table.yaml
│   │       └── 002-add-image-columns.yaml
│   └── static/
│       ├── index.html                # Start page with tabs (Text/Camera)
│       ├── arazzo.yaml               # Arazzo workflow specification
│       └── css/style.css
└── pom.xml
```

---

## Existing Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/notes` | Get all notes |
| GET | `/api/notes/{id}` | Get single note |
| POST | `/api/notes` | Create note |
| PUT | `/api/notes/{id}` | Update note |
| DELETE | `/api/notes/{id}` | Delete note |
| POST | `/api/notes/from-image` | Analyze image and create note |
| GET | `/hello` | Test endpoint ("hi") |
| GET | `/swagger-ui.html` | Swagger UI (API documentation) |
| GET | `/v3/api-docs` | OpenAPI 3.0 JSON |

---

## LM Studio Configuration

- **Server URL:** `http://127.0.0.1:1234/v1`
- **Model:** `ministral-3b` (with Vision support)
- **Configuration in:** `application.properties`

```properties
lmstudio.base-url=http://127.0.0.1:1234/v1
lmstudio.model-name=ministral-3b
```

---

## Note Entity

```java
@Entity
public class Note {
    Long id;
    String title;
    String content;
    ContentType contentType;  // TEXT or DIAGRAM
    byte[] imageData;         // Original image as BLOB (without @Lob!)
    String imageType;         // e.g., "image/png"
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
```

**Important:** SQLite JDBC does not support `@Lob`! Use `@Basic(fetch = FetchType.LAZY)` instead.

---

## ImageAnalysisService

The service analyzes images with the Vision LLM:

1. **Input:** Base64-encoded image
2. **Prompt:** Asks the LLM if text or diagram
3. **Output:** 
   - For text: OCR result
   - For diagram: Mermaid code (cleaned from escape sequences)

### cleanMermaidCode()

The LLM sometimes returns escape sequences (`\n`, `\"`), which must be cleaned:

```java
private String cleanMermaidCode(String code) {
    return code
        .replace("\\n", "\n")
        .replace("\\\"", "\"")
        .replace("\\\\", "\\")
        .replaceAll("  +", " ")
        .trim();
}
```

---

## Frontend Features

- **Tabs:** Text input or camera capture
- **Camera:** WebRTC/getUserMedia for laptop camera
- **Preview:** Photo preview before upload
- **Note List:** Card view with delete button
- **Mermaid.js:** Renders diagrams automatically

---

## Known Issues & Solutions

| Problem | Solution |
|---------|----------|
| SQLite throws `SQLFeatureNotSupportedException` for BLOB | Remove `@Lob`, use `byte[]` with `@Basic` |
| LLM returns escaped Mermaid code | `cleanMermaidCode()` method cleans escape sequences |
| Ollama vs. LM Studio | Both installed, LM Studio is used (Port 1234) |
| Spring Boot 4.0 Breaking Changes | `@MockBean` → `@MockitoBean`, test modules split |
| @WebMvcTest not available | Use pure Mockito unit tests |

---

## Git Log (recent commits)

```
a35f1cd Add OpenAPI annotations and unit tests for NoteController
1864a89 Add Arazzo Specification for API workflows
29c1f76 Add SpringDoc OpenAPI for automatic API documentation
e543ec1 Add ollama-installer.exe to .gitignore
ed82d0d Update CONTEXT.md with current project state
e947b19 Add cleanMermaidCode() to fix escape sequences from LLM response
```

---

## Starting the App

```bash
# Start LM Studio Server (Port 1234)
# Then:
./mvnw spring-boot:run
```

App is available at: `http://localhost:8080`

---

## Next Steps

- [x] Add SpringDoc OpenAPI
- [x] Add OpenAPI annotations to Controller/Entity/DTO
- [x] Create Arazzo workflow specification
- [x] Write unit tests for NoteController
- [ ] Test app with real image (Camera -> Analysis -> Note)
- [ ] Improve error handling (e.g., when LM Studio is not running)
- [ ] Note detail view with image display
- [ ] Mermaid diagram export as PNG/SVG
- [ ] Add integration tests

---

## Resuming Work

When continuing, simply ask:

> "What have we done so far?" or "Continue"
