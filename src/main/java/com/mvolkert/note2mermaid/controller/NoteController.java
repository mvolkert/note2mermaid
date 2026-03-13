package com.mvolkert.note2mermaid.controller;

import com.mvolkert.note2mermaid.dto.ImageUploadRequest;
import com.mvolkert.note2mermaid.entity.Note;
import com.mvolkert.note2mermaid.repository.NoteRepository;
import com.mvolkert.note2mermaid.service.ImageAnalysisService;
import com.mvolkert.note2mermaid.service.ImageAnalysisService.ImageAnalysisResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "API for managing notes with optional AI-powered image analysis")
public class NoteController {

    private final NoteRepository noteRepository;
    private final ImageAnalysisService imageAnalysisService;

    public NoteController(NoteRepository noteRepository, ImageAnalysisService imageAnalysisService) {
        this.noteRepository = noteRepository;
        this.imageAnalysisService = imageAnalysisService;
    }

    @Operation(
        summary = "Get all notes",
        description = "Retrieves a list of all notes. Note: Image data (imageData) is excluded from the response for performance reasons."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of notes retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Note.class))
    )
    @GetMapping
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @Operation(
        summary = "Get note by ID",
        description = "Retrieves a single note by its unique identifier. Use GET /api/notes/{id}/image to retrieve the associated image separately."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Note found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Note.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Note not found",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(
            @Parameter(description = "Unique identifier of the note", required = true, example = "1")
            @PathVariable Long id) {
        return noteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Create a new note",
        description = "Creates a new note with title and content. For creating notes from images, use POST /api/notes/from-image instead."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Note created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Note.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request body",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<Note> createNote(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Note to create",
                required = true,
                content = @Content(schema = @Schema(implementation = Note.class))
            )
            @RequestBody Note note) {
        Note savedNote = noteRepository.save(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }

    @Operation(
        summary = "Update an existing note",
        description = "Updates the title and content of an existing note. The updatedAt timestamp is automatically set."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Note updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Note.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Note not found",
            content = @Content
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(
            @Parameter(description = "Unique identifier of the note to update", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated note data",
                required = true,
                content = @Content(schema = @Schema(implementation = Note.class))
            )
            @RequestBody Note noteDetails) {
        return noteRepository.findById(id)
                .map(existingNote -> {
                    existingNote.setTitle(noteDetails.getTitle());
                    existingNote.setContent(noteDetails.getContent());
                    Note updatedNote = noteRepository.save(existingNote);
                    return ResponseEntity.ok(updatedNote);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Delete a note",
        description = "Permanently deletes a note and its associated image data."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Note deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Note not found"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @Parameter(description = "Unique identifier of the note to delete", required = true, example = "1")
            @PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> {
                    noteRepository.delete(note);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Create note from image",
        description = """
            Analyzes an image using a Vision LLM and creates a note based on the content.
            
            The AI will automatically detect:
            - **Text content**: Performs OCR and extracts the text
            - **Diagrams/Flowcharts**: Generates Mermaid code that can be rendered
            - **Other images**: Provides a description
            
            The original image is stored with the note and can be retrieved via GET /api/notes/{id}/image.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Note created successfully from image analysis",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Note.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Image analysis failed (e.g., LM Studio not running)",
            content = @Content
        )
    })
    @PostMapping("/from-image")
    public ResponseEntity<Note> createNoteFromImage(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Image data for analysis",
                required = true,
                content = @Content(schema = @Schema(implementation = ImageUploadRequest.class))
            )
            @RequestBody ImageUploadRequest request) {
        try {
            // Convert Base64 to byte[]
            byte[] imageBytes = Base64.getDecoder().decode(request.getImageData());
            
            // Analyze image
            ImageAnalysisResult result = imageAnalysisService.analyzeImage(imageBytes, request.getMimeType());
            
            // Create note
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

    @Operation(
        summary = "Get note image",
        description = "Retrieves the original image associated with a note. Returns the image in its original format (e.g., image/png, image/jpeg)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Image retrieved successfully",
            content = @Content(mediaType = "image/*")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Note not found or note has no image"
        )
    })
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getNoteImage(
            @Parameter(description = "Unique identifier of the note", required = true, example = "1")
            @PathVariable Long id) {
        return noteRepository.findById(id)
                .filter(note -> note.getImageData() != null)
                .map(note -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(note.getImageType()))
                        .body(note.getImageData()))
                .orElse(ResponseEntity.notFound().build());
    }

    private String generateTitle(ImageAnalysisResult result) {
        return switch (result.getType()) {
            case DIAGRAM -> "Diagram from " + java.time.LocalDate.now();
            case TEXT -> "Text from " + java.time.LocalDate.now();
            case IMAGE -> "Image from " + java.time.LocalDate.now();
            case MARKDOWN -> "Note from " + java.time.LocalDate.now();
        };
    }
}
