# Agents Guidelines

## Console/Terminal

Für alle KI-Agenten gilt: **Immer Git Bash als Console verwenden.**

Dies stellt sicher, dass Unix-kompatible Befehle konsistent funktionieren und plattformübergreifende Kompatibilität gewährleistet ist.

---

## Architekturentscheidungen (ADRs)

### ADR-001: Spring Boot 4.0 mit Java 17

**Status:** Akzeptiert  
**Kontext:** Wahl des Backend-Frameworks und der Java-Version.  
**Entscheidung:** Spring Boot 4.0.3 mit Java 17 (LTS).  
**Begründung:**
- Spring Boot 4.0 bietet neueste Features und Sicherheitsupdates
- Java 17 ist LTS mit guter Balance zwischen neuen Features und Stabilität
- Breaking Changes in Spring Boot 4.0 erfordern Anpassungen (siehe ADR-006)

**Konsequenzen:**
- `@MockBean` → `@MockitoBean` (neues Package)
- Test-Module sind aufgeteilt und erfordern explizite Dependencies

---

### ADR-002: SQLite als Datenbank

**Status:** Akzeptiert  
**Kontext:** Wahl der Datenbank für eine lokale Desktop-Anwendung.  
**Entscheidung:** SQLite mit Hibernate/JPA.  
**Begründung:**
- Keine separate Datenbankinstallation nötig
- Datei-basiert, einfach zu sichern und zu transportieren
- Ausreichend für Single-User-Anwendung

**Konsequenzen:**
- `@Lob` Annotation funktioniert nicht → `@Basic(fetch = FetchType.LAZY)` für BLOBs verwenden
- Liquibase für Schema-Migrationen

---

### ADR-003: LangChain4j mit LM Studio

**Status:** Akzeptiert  
**Kontext:** Integration eines Vision-LLMs für Bildanalyse.  
**Entscheidung:** LangChain4j als Abstraktionsschicht, LM Studio als lokaler OpenAI-kompatibler Server.  
**Begründung:**
- LangChain4j bietet einheitliche API für verschiedene LLM-Provider
- LM Studio ermöglicht lokale Modellausführung ohne Cloud-Abhängigkeit
- OpenAI-kompatible API erleichtert zukünftigen Provider-Wechsel

**Konfiguration:**
```properties
lmstudio.base-url=http://127.0.0.1:1234/v1
lmstudio.model-name=ministral-3b
```

**Konsequenzen:**
- LM Studio muss vor App-Start laufen
- Vision-Modell (z.B. ministral-3b) muss geladen sein

---

### ADR-004: Statisches Frontend ohne Framework

**Status:** Akzeptiert  
**Kontext:** Frontend-Technologie für die Webapp.  
**Entscheidung:** Vanilla HTML/CSS/JavaScript mit Mermaid.js.  
**Begründung:**
- Einfachheit und schnelle Entwicklung
- Keine Build-Tools oder Node.js nötig
- Mermaid.js für Diagramm-Rendering direkt im Browser

**Konsequenzen:**
- Kein State-Management-Framework
- Manuelle DOM-Manipulation
- Statische Files in `src/main/resources/static/`

---

### ADR-005: SpringDoc OpenAPI für API-Dokumentation

**Status:** Akzeptiert  
**Kontext:** Automatische API-Dokumentation.  
**Entscheidung:** SpringDoc OpenAPI 3 mit Swagger UI.  
**Begründung:**
- Automatische Generierung aus Controller-Annotationen
- Interaktive Swagger UI zum Testen
- Standard-konform (OpenAPI 3.0)

**Endpoints:**
- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`
- OpenAPI YAML: `/v3/api-docs.yaml`

**Annotationen:**
- `@Tag` auf Controller-Ebene
- `@Operation` auf Methoden-Ebene
- `@ApiResponse` für Response-Dokumentation
- `@Schema` auf Entity/DTO-Felder

---

### ADR-006: Arazzo Specification für Workflows

**Status:** Akzeptiert  
**Kontext:** Dokumentation von API-Workflows und Sequenzen.  
**Entscheidung:** Arazzo 1.0.1 Specification in `static/arazzo.yaml`.  
**Begründung:**
- Ergänzt OpenAPI um Workflow-Beschreibungen
- Dokumentiert typische Nutzungssequenzen
- Maschinenlesbar für potenzielle Codegen

**Definierte Workflows:**
1. `createAndManageNote` - CRUD-Operationen
2. `captureImageAndCreateNote` - Kamera → AI → Notiz
3. `listAllNotes` - Alle Notizen abrufen
4. `updateNoteContent` - Notiz aktualisieren
5. `deleteNote` - Notiz löschen
6. `sketchToMermaid` - Skizze zu Mermaid-Diagramm

---

### ADR-007: Unit Tests mit reinem Mockito

**Status:** Akzeptiert  
**Kontext:** Test-Strategie für Spring Boot 4.0.  
**Entscheidung:** Reine Mockito Unit Tests statt `@WebMvcTest` oder `@SpringBootTest`.  
**Begründung:**
- Spring Boot 4.0 hat Test-Module stark aufgeteilt
- `@WebMvcTest` erfordert zusätzliche Dependencies die nicht alle verfügbar sind
- Mockito Unit Tests sind schneller und Spring-unabhängig

**Pattern:**
```java
@ExtendWith(MockitoExtension.class)
class NoteControllerTest {
    @Mock
    private NoteRepository noteRepository;
    
    @InjectMocks
    private NoteController noteController;
}
```

---

### ADR-008: Bild-Speicherung als BLOB in SQLite

**Status:** Akzeptiert  
**Kontext:** Speicherung von Kamerabildern.  
**Entscheidung:** Bilder als `byte[]` direkt in der Datenbank speichern.  
**Begründung:**
- Einfaches Backup (alles in einer Datei)
- Keine Dateisystem-Verwaltung nötig
- Transaktionale Konsistenz mit Notiz-Daten

**Entity-Mapping:**
```java
@Basic(fetch = FetchType.LAZY)
@Column(name = "image_data")
private byte[] imageData;

@Column(name = "image_type")
private String imageType; // z.B. "image/png"
```

**Konsequenzen:**
- Lazy-Loading für Performance
- Separater Endpoint `/api/notes/{id}/image` für Bild-Abruf

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
