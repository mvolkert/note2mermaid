package com.mvolkert.note2mermaid.controller;

import com.mvolkert.note2mermaid.dto.ImageUploadRequest;
import com.mvolkert.note2mermaid.entity.Note;
import com.mvolkert.note2mermaid.repository.NoteRepository;
import com.mvolkert.note2mermaid.service.ImageAnalysisService;
import com.mvolkert.note2mermaid.service.ImageAnalysisService.ImageAnalysisResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final ImageAnalysisService imageAnalysisService;

    public NoteController(NoteRepository noteRepository, ImageAnalysisService imageAnalysisService) {
        this.noteRepository = noteRepository;
        this.imageAnalysisService = imageAnalysisService;
    }

    @GetMapping
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        Note savedNote = noteRepository.save(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note noteDetails) {
        return noteRepository.findById(id)
                .map(existingNote -> {
                    existingNote.setTitle(noteDetails.getTitle());
                    existingNote.setContent(noteDetails.getContent());
                    Note updatedNote = noteRepository.save(existingNote);
                    return ResponseEntity.ok(updatedNote);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> {
                    noteRepository.delete(note);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/from-image")
    public ResponseEntity<Note> createNoteFromImage(@RequestBody ImageUploadRequest request) {
        try {
            // Base64 zu byte[] konvertieren
            byte[] imageBytes = Base64.getDecoder().decode(request.getImageData());
            
            // Bild analysieren
            ImageAnalysisResult result = imageAnalysisService.analyzeImage(imageBytes, request.getMimeType());
            
            // Notiz erstellen
            Note note = new Note();
            note.setTitle(request.getTitle() != null ? request.getTitle() : generateTitle(result));
            note.setContent(result.getContent());
            note.setContentType(result.getType().name());
            note.setImageData(imageBytes);
            note.setImageType(request.getMimeType());
            
            Note savedNote = noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getNoteImage(@PathVariable Long id) {
        return noteRepository.findById(id)
                .filter(note -> note.getImageData() != null)
                .map(note -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(note.getImageType()))
                        .body(note.getImageData()))
                .orElse(ResponseEntity.notFound().build());
    }

    private String generateTitle(ImageAnalysisResult result) {
        return switch (result.getType()) {
            case DIAGRAM -> "Diagramm vom " + java.time.LocalDate.now();
            case TEXT -> "Text vom " + java.time.LocalDate.now();
            case IMAGE -> "Bild vom " + java.time.LocalDate.now();
        };
    }
}
