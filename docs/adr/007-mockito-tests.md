# ADR-007: Unit Tests with Pure Mockito

**Status:** Accepted  
**Date:** 2024

## Context

Choosing a test strategy for Spring Boot 4.0.

## Decision

Pure Mockito unit tests instead of `@WebMvcTest` or `@SpringBootTest`.

## Rationale

- Spring Boot 4.0 has significantly split test modules
- `@WebMvcTest` requires additional dependencies that are not all available
- Mockito unit tests are faster and Spring-independent
- Simpler setup, less magic

## Consequences

### Test Pattern

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

### Advantages

- Fast execution (no Spring context)
- Clear dependencies through explicit mocks
- No Spring Boot version dependencies in tests

### Disadvantages

- No testing of Spring integration (RequestMapping, etc.)
- Manual ResponseEntity verification instead of MockMvc
