# ADR-007: Unit Tests mit reinem Mockito

**Status:** Akzeptiert  
**Datum:** 2024

## Kontext

Test-Strategie für Spring Boot 4.0 wählen.

## Entscheidung

Reine Mockito Unit Tests statt `@WebMvcTest` oder `@SpringBootTest`.

## Begründung

- Spring Boot 4.0 hat Test-Module stark aufgeteilt
- `@WebMvcTest` erfordert zusätzliche Dependencies die nicht alle verfügbar sind
- Mockito Unit Tests sind schneller und Spring-unabhängig
- Einfacheres Setup, weniger Magie

## Konsequenzen

### Test-Pattern

```java
@ExtendWith(MockitoExtension.class)
class NoteControllerTest {
    
    @Mock
    private NoteRepository noteRepository;
    
    @Mock
    private ImageAnalysisService imageAnalysisService;
    
    @InjectMocks
    private NoteController noteController;
    
    @Test
    void getAllNotes_returnsNotesList() {
        // Given
        List<Note> notes = List.of(new Note(), new Note());
        when(noteRepository.findAll()).thenReturn(notes);
        
        // When
        List<Note> result = noteController.getAllNotes();
        
        // Then
        assertThat(result).hasSize(2);
        verify(noteRepository).findAll();
    }
}
```

### Dependencies

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

### Vorteile

- Schnelle Ausführung (kein Spring Context)
- Klare Abhängigkeiten durch explizite Mocks
- Keine Spring-Boot-Version-Abhängigkeiten in Tests

### Nachteile

- Kein Test der Spring-Integration (RequestMapping, etc.)
- Manuelle ResponseEntity-Prüfung statt MockMvc
