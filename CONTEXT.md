# Projekt-Kontext: note2mermaid

## Technologie-Stack

- **Backend:** Spring Boot 3.4.3 (Java 21)
- **Datenbank:** SQLite mit Liquibase Migrations
- **Frontend:** Statisches HTML/CSS/JS + Mermaid.js
- **Build:** Maven
- **KI:** LangChain4j mit LM Studio (OpenAI-kompatibel)

---

## Repository-Struktur

```
note2mermaid/
├── src/main/java/com/mvolkert/note2mermaid/
│   ├── Note2mermaidApplication.java
│   ├── controller/
│   │   ├── NoteController.java       # CRUD + /api/notes/from-image
│   │   └── HelloController.java      # Test-Endpoint
│   ├── entity/
│   │   └── Note.java                 # JPA Entity mit Bild-Spalten
│   ├── repository/
│   │   └── NoteRepository.java
│   ├── service/
│   │   └── ImageAnalysisService.java # LangChain4j Vision-Analyse
│   └── dto/
│       └── ImageUploadRequest.java   # DTO für Bild-Upload
├── src/main/resources/
│   ├── application.properties        # DB + LM Studio Config
│   ├── db/changelog/
│   │   ├── db.changelog-master.yaml
│   │   └── changes/
│   │       ├── 001-create-notes-table.yaml
│   │       └── 002-add-image-columns.yaml
│   └── static/
│       ├── index.html                # Startseite mit Tabs (Text/Kamera)
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
| POST | `/api/notes/from-image` | Bild analysieren und Notiz erstellen |
| GET | `/hello` | Test-Endpoint ("hi") |

---

## LM Studio Konfiguration

- **Server URL:** `http://127.0.0.1:1234/v1`
- **Modell:** `ministral-3b` (mit Vision-Support)
- **Konfiguration in:** `application.properties`

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
    ContentType contentType;  // TEXT oder DIAGRAM
    byte[] imageData;         // Original-Bild als BLOB (ohne @Lob!)
    String imageType;         // z.B. "image/png"
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
```

**Wichtig:** SQLite JDBC unterstützt `@Lob` nicht! Stattdessen `@Basic(fetch = FetchType.LAZY)` verwenden.

---

## ImageAnalysisService

Der Service analysiert Bilder mit dem Vision-LLM:

1. **Input:** Base64-codiertes Bild
2. **Prompt:** Fragt das LLM ob Text oder Diagramm
3. **Output:** 
   - Bei Text: OCR-Ergebnis
   - Bei Diagramm: Mermaid-Code (bereinigt von Escape-Sequenzen)

### cleanMermaidCode()

Das LLM gibt manchmal Escape-Sequenzen zurück (`\n`, `\"`), die bereinigt werden müssen:

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

- **Tabs:** Text-Eingabe oder Kamera-Aufnahme
- **Kamera:** WebRTC/getUserMedia für Laptop-Kamera
- **Preview:** Foto-Vorschau vor Upload
- **Notizliste:** Karten-Ansicht mit Löschen-Button
- **Mermaid.js:** Rendert Diagramme automatisch

---

## Bekannte Probleme & Lösungen

| Problem | Lösung |
|---------|--------|
| SQLite wirft `SQLFeatureNotSupportedException` bei BLOB | `@Lob` entfernen, `byte[]` mit `@Basic` verwenden |
| LLM gibt escaped Mermaid-Code zurück | `cleanMermaidCode()` Methode bereinigt Escape-Sequenzen |
| Ollama vs. LM Studio | Beides installiert, LM Studio wird verwendet (Port 1234) |

---

## Git Log (letzte Commits)

```
e947b19 Add cleanMermaidCode() to fix escape sequences from LLM response
51bcef2 Fix SQLite BLOB handling - remove @Lob annotation
d990a42 Update LM Studio config to use ministral-3b vision model
233dfee Add camera-to-note feature with LangChain4j and Llama Vision
3e02d47 Add CONTEXT.md for session continuity
9f56702 Fix Liquibase changelog include path
```

---

## App starten

```bash
# LM Studio Server starten (Port 1234)
# Dann:
./mvnw spring-boot:run
```

App ist erreichbar unter: `http://localhost:8080`

---

## Nächste Schritte

- [ ] App testen mit echtem Bild (Kamera -> Analyse -> Notiz)
- [ ] Fehlerbehandlung verbessern (z.B. wenn LM Studio nicht läuft)
- [ ] Notiz-Detail-Ansicht mit Bild-Anzeige
- [ ] Mermaid-Diagramm Export als PNG/SVG

---

## Wiederaufsetzen

Beim Fortsetzen einfach fragen:

> "Was haben wir bisher gemacht?" oder "Continue"
