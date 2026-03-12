package com.mvolkert.note2mermaid.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for creating a note from an image")
public class ImageUploadRequest {
    
    @Schema(
        description = "Base64 encoded image data (without data URI prefix)",
        example = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String imageData;
    
    @Schema(
        description = "MIME type of the image",
        example = "image/png",
        allowableValues = {"image/png", "image/jpeg", "image/gif", "image/webp"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String mimeType;
    
    @Schema(
        description = "Optional custom title for the note. If not provided, a title will be auto-generated based on the content type.",
        example = "Flowchart Sketch"
    )
    private String title;

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
