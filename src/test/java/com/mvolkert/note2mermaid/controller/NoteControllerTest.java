package com.mvolkert.note2mermaid.controller;

import com.mvolkert.note2mermaid.dto.ImageUploadRequest;
import com.mvolkert.note2mermaid.entity.Note;
import com.mvolkert.note2mermaid.repository.NoteRepository;
import com.mvolkert.note2mermaid.service.ImageAnalysisService;
import com.mvolkert.note2mermaid.service.ImageAnalysisService.ContentType;
import com.mvolkert.note2mermaid.service.ImageAnalysisService.ImageAnalysisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NoteController Unit Tests")
class NoteControllerTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private ImageAnalysisService imageAnalysisService;

    @InjectMocks
    private NoteController noteController;

    private Note testNote;

    @BeforeEach
    void setUp() {
        testNote = new Note("Test Title", "Test Content");
        testNote.setId(1L);
        testNote.setContentType("TEXT");
    }

    @Nested
    @DisplayName("GET /api/notes")
    class GetAllNotes {

        @Test
        @DisplayName("should return empty list when no notes exist")
        void shouldReturnEmptyList() {
            when(noteRepository.findAll()).thenReturn(List.of());

            List<Note> result = noteController.getAllNotes();

            assertThat(result).isEmpty();
            verify(noteRepository).findAll();
        }

        @Test
        @DisplayName("should return all notes")
        void shouldReturnAllNotes() {
            Note note2 = new Note("Second Note", "Second Content");
            note2.setId(2L);

            when(noteRepository.findAll()).thenReturn(Arrays.asList(testNote, note2));

            List<Note> result = noteController.getAllNotes();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitle()).isEqualTo("Test Title");
            assertThat(result.get(1).getTitle()).isEqualTo("Second Note");
            verify(noteRepository).findAll();
        }
    }

    @Nested
    @DisplayName("GET /api/notes/{id}")
    class GetNoteById {

        @Test
        @DisplayName("should return note when found")
        void shouldReturnNoteWhenFound() {
            when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));

            ResponseEntity<Note> response = noteController.getNoteById(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Test Title");
            verify(noteRepository).findById(1L);
        }

        @Test
        @DisplayName("should return 404 when note not found")
        void shouldReturn404WhenNotFound() {
            when(noteRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResponseEntity<Note> response = noteController.getNoteById(999L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            verify(noteRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("POST /api/notes")
    class CreateNote {

        @Test
        @DisplayName("should create note and return 201")
        void shouldCreateNote() {
            when(noteRepository.save(any(Note.class))).thenReturn(testNote);

            Note newNote = new Note("Test Title", "Test Content");

            ResponseEntity<Note> response = noteController.createNote(newNote);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(1L);
            assertThat(response.getBody().getTitle()).isEqualTo("Test Title");
            verify(noteRepository).save(any(Note.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/notes/{id}")
    class UpdateNote {

        @Test
        @DisplayName("should update note when found")
        void shouldUpdateNote() {
            Note updatedNote = new Note("Updated Title", "Updated Content");
            updatedNote.setId(1L);

            when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));
            when(noteRepository.save(any(Note.class))).thenReturn(updatedNote);

            ResponseEntity<Note> response = noteController.updateNote(1L, updatedNote);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
            verify(noteRepository).findById(1L);
            verify(noteRepository).save(any(Note.class));
        }

        @Test
        @DisplayName("should return 404 when note to update not found")
        void shouldReturn404WhenUpdatingNonexistentNote() {
            when(noteRepository.findById(anyLong())).thenReturn(Optional.empty());

            Note updatedNote = new Note("Updated Title", "Updated Content");

            ResponseEntity<Note> response = noteController.updateNote(999L, updatedNote);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            verify(noteRepository).findById(999L);
            verify(noteRepository, never()).save(any(Note.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/notes/{id}")
    class DeleteNote {

        @Test
        @DisplayName("should delete note and return 204")
        void shouldDeleteNote() {
            when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));
            doNothing().when(noteRepository).delete(any(Note.class));

            ResponseEntity<Void> response = noteController.deleteNote(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(noteRepository).findById(1L);
            verify(noteRepository).delete(testNote);
        }

        @Test
        @DisplayName("should return 404 when note to delete not found")
        void shouldReturn404WhenDeletingNonexistentNote() {
            when(noteRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResponseEntity<Void> response = noteController.deleteNote(999L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            verify(noteRepository).findById(999L);
            verify(noteRepository, never()).delete(any(Note.class));
        }
    }

    @Nested
    @DisplayName("POST /api/notes/from-image")
    class CreateNoteFromImage {

        @Test
        @DisplayName("should create note from image with text content")
        void shouldCreateNoteFromImageWithText() {
            ImageAnalysisResult result = new ImageAnalysisResult();
            result.setType(ContentType.TEXT);
            result.setContent("Extracted text from image");

            when(imageAnalysisService.analyzeImage(any(byte[].class), anyString()))
                    .thenReturn(result);
            
            Note savedNote = new Note();
            savedNote.setId(1L);
            savedNote.setTitle("Text vom " + java.time.LocalDate.now());
            savedNote.setContent("Extracted text from image");
            savedNote.setContentType("TEXT");
            
            when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

            ImageUploadRequest request = new ImageUploadRequest();
            request.setImageData(Base64.getEncoder().encodeToString("fake image".getBytes()));
            request.setMimeType("image/png");

            ResponseEntity<Note> response = noteController.createNoteFromImage(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContentType()).isEqualTo("TEXT");
            assertThat(response.getBody().getContent()).isEqualTo("Extracted text from image");
            verify(imageAnalysisService).analyzeImage(any(byte[].class), eq("image/png"));
            verify(noteRepository).save(any(Note.class));
        }

        @Test
        @DisplayName("should create note from image with diagram content")
        void shouldCreateNoteFromImageWithDiagram() {
            ImageAnalysisResult result = new ImageAnalysisResult();
            result.setType(ContentType.DIAGRAM);
            result.setContent("graph TD\n    A[Start] --> B[End]");

            when(imageAnalysisService.analyzeImage(any(byte[].class), anyString()))
                    .thenReturn(result);
            
            Note savedNote = new Note();
            savedNote.setId(1L);
            savedNote.setTitle("Diagramm vom " + java.time.LocalDate.now());
            savedNote.setContent("graph TD\n    A[Start] --> B[End]");
            savedNote.setContentType("DIAGRAM");
            
            when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

            ImageUploadRequest request = new ImageUploadRequest();
            request.setImageData(Base64.getEncoder().encodeToString("fake image".getBytes()));
            request.setMimeType("image/png");

            ResponseEntity<Note> response = noteController.createNoteFromImage(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContentType()).isEqualTo("DIAGRAM");
            assertThat(response.getBody().getContent()).contains("graph TD");
            verify(imageAnalysisService).analyzeImage(any(byte[].class), eq("image/png"));
        }

        @Test
        @DisplayName("should use custom title when provided")
        void shouldUseCustomTitle() {
            ImageAnalysisResult result = new ImageAnalysisResult();
            result.setType(ContentType.TEXT);
            result.setContent("Some content");

            when(imageAnalysisService.analyzeImage(any(byte[].class), anyString()))
                    .thenReturn(result);
            
            Note savedNote = new Note();
            savedNote.setId(1L);
            savedNote.setTitle("My Custom Title");
            savedNote.setContent("Some content");
            savedNote.setContentType("TEXT");
            
            when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

            ImageUploadRequest request = new ImageUploadRequest();
            request.setImageData(Base64.getEncoder().encodeToString("fake image".getBytes()));
            request.setMimeType("image/png");
            request.setTitle("My Custom Title");

            ResponseEntity<Note> response = noteController.createNoteFromImage(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("My Custom Title");
        }

        @Test
        @DisplayName("should return 500 when image analysis fails")
        void shouldReturn500WhenAnalysisFails() {
            when(imageAnalysisService.analyzeImage(any(byte[].class), anyString()))
                    .thenThrow(new RuntimeException("LM Studio not running"));

            ImageUploadRequest request = new ImageUploadRequest();
            request.setImageData(Base64.getEncoder().encodeToString("fake image".getBytes()));
            request.setMimeType("image/png");

            ResponseEntity<Note> response = noteController.createNoteFromImage(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Nested
    @DisplayName("GET /api/notes/{id}/image")
    class GetNoteImage {

        @Test
        @DisplayName("should return image when note has image data")
        void shouldReturnImage() {
            byte[] imageData = "fake image data".getBytes();
            testNote.setImageData(imageData);
            testNote.setImageType("image/png");

            when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));

            ResponseEntity<byte[]> response = noteController.getNoteImage(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(imageData);
            verify(noteRepository).findById(1L);
        }

        @Test
        @DisplayName("should return 404 when note has no image")
        void shouldReturn404WhenNoImage() {
            testNote.setImageData(null);

            when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));

            ResponseEntity<byte[]> response = noteController.getNoteImage(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("should return 404 when note not found")
        void shouldReturn404WhenNoteNotFound() {
            when(noteRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResponseEntity<byte[]> response = noteController.getNoteImage(999L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
