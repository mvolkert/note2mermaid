package com.mvolkert.note2mermaid.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
@Schema(description = "A note entity that can contain text, Mermaid diagrams, or image descriptions")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the note", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Title of the note", example = "Meeting Notes", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Column(columnDefinition = "TEXT")
    @Schema(
        description = "Content of the note. For DIAGRAM type, this contains Mermaid syntax code.",
        example = "graph TD\\n    A[Start] --> B[End]"
    )
    private String content;

    @Column(nullable = false)
    @Schema(description = "Timestamp when the note was created", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Schema(description = "Timestamp when the note was last updated", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data")
    @Schema(
        description = "Original image data as byte array (Base64 encoded in JSON). Excluded from list responses for performance.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private byte[] imageData;

    @Column
    @Schema(description = "MIME type of the stored image", example = "image/png")
    private String imageType;

    @Column
    @Schema(
        description = "Type of content in this note",
        allowableValues = {"TEXT", "DIAGRAM", "IMAGE", "MARKDOWN"},
        example = "DIAGRAM"
    )
    private String contentType;

    public Note() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Note(String title, String content) {
        this();
        this.title = title;
        this.content = content;
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
