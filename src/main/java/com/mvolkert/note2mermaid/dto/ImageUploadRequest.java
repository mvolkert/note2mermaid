package com.mvolkert.note2mermaid.dto;

public class ImageUploadRequest {
    private String imageData; // Base64 encoded
    private String mimeType;  // z.B. "image/png", "image/jpeg"
    private String title;     // Optional: Titel für die Notiz

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
