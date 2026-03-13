package com.mvolkert.note2mermaid.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Note Entity Tests")
class NoteTest {

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("default constructor should set timestamps")
        void defaultConstructorShouldSetTimestamps() {
            // When
            Note note = new Note();

            // Then
            assertThat(note.getCreatedAt()).isNotNull();
            assertThat(note.getUpdatedAt()).isNotNull();
            assertThat(note.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("parameterized constructor should set title and content")
        void parameterizedConstructorShouldSetTitleAndContent() {
            // When
            Note note = new Note("Test Title", "Test Content");

            // Then
            assertThat(note.getTitle()).isEqualTo("Test Title");
            assertThat(note.getContent()).isEqualTo("Test Content");
            assertThat(note.getCreatedAt()).isNotNull();
            assertThat(note.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Getters and Setters")
    class GettersAndSetters {

        private Note note;

        @BeforeEach
        void setUp() {
            note = new Note();
        }

        @Test
        @DisplayName("should get and set id")
        void shouldGetAndSetId() {
            note.setId(42L);
            assertThat(note.getId()).isEqualTo(42L);
        }

        @Test
        @DisplayName("should get and set title")
        void shouldGetAndSetTitle() {
            note.setTitle("My Title");
            assertThat(note.getTitle()).isEqualTo("My Title");
        }

        @Test
        @DisplayName("should get and set content")
        void shouldGetAndSetContent() {
            note.setContent("My Content");
            assertThat(note.getContent()).isEqualTo("My Content");
        }

        @Test
        @DisplayName("should get and set createdAt")
        void shouldGetAndSetCreatedAt() {
            LocalDateTime time = LocalDateTime.of(2024, 1, 1, 12, 0);
            note.setCreatedAt(time);
            assertThat(note.getCreatedAt()).isEqualTo(time);
        }

        @Test
        @DisplayName("should get and set updatedAt")
        void shouldGetAndSetUpdatedAt() {
            LocalDateTime time = LocalDateTime.of(2024, 1, 1, 12, 0);
            note.setUpdatedAt(time);
            assertThat(note.getUpdatedAt()).isEqualTo(time);
        }

        @Test
        @DisplayName("should get and set imageData")
        void shouldGetAndSetImageData() {
            byte[] data = "image data".getBytes();
            note.setImageData(data);
            assertThat(note.getImageData()).isEqualTo(data);
        }

        @Test
        @DisplayName("should get and set imageType")
        void shouldGetAndSetImageType() {
            note.setImageType("image/png");
            assertThat(note.getImageType()).isEqualTo("image/png");
        }

        @Test
        @DisplayName("should get and set contentType")
        void shouldGetAndSetContentType() {
            note.setContentType("DIAGRAM");
            assertThat(note.getContentType()).isEqualTo("DIAGRAM");
        }
    }

    @Nested
    @DisplayName("Null handling")
    class NullHandling {

        @Test
        @DisplayName("should handle null imageData")
        void shouldHandleNullImageData() {
            Note note = new Note();
            note.setImageData(null);
            assertThat(note.getImageData()).isNull();
        }

        @Test
        @DisplayName("should handle null title")
        void shouldHandleNullTitle() {
            Note note = new Note();
            note.setTitle(null);
            assertThat(note.getTitle()).isNull();
        }
    }
}
