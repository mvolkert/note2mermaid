package com.mvolkert.note2mermaid.service;

import com.mvolkert.note2mermaid.service.ImageAnalysisService.ContentType;
import com.mvolkert.note2mermaid.service.ImageAnalysisService.ImageAnalysisResult;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImageAnalysisService Unit Tests")
class ImageAnalysisServiceTest {

    @Mock
    private ChatLanguageModel chatModel;

    private ImageAnalysisService imageAnalysisService;

    @BeforeEach
    void setUp() throws Exception {
        imageAnalysisService = new ImageAnalysisService();
        
        // Inject mock chatModel via reflection
        Field chatModelField = ImageAnalysisService.class.getDeclaredField("chatModel");
        chatModelField.setAccessible(true);
        chatModelField.set(imageAnalysisService, chatModel);
    }

    private void mockLlmResponse(String response) {
        AiMessage aiMessage = AiMessage.from(response);
        Response<AiMessage> llmResponse = Response.from(aiMessage);
        when(chatModel.generate(any(UserMessage.class))).thenReturn(llmResponse);
    }

    @Nested
    @DisplayName("analyzeImage")
    class AnalyzeImage {

        @Test
        @DisplayName("should detect TEXT content type")
        void shouldDetectTextContent() {
            // Given
            mockLlmResponse("TYPE: TEXT\nThis is the extracted text from the image.");
            byte[] imageData = "fake image".getBytes();

            // When
            ImageAnalysisResult result = imageAnalysisService.analyzeImage(imageData, "image/png");

            // Then
            assertThat(result.getType()).isEqualTo(ContentType.TEXT);
            assertThat(result.getContent()).isEqualTo("This is the extracted text from the image.");
        }

        @Test
        @DisplayName("should detect DIAGRAM content type with mermaid code block")
        void shouldDetectDiagramWithCodeBlock() {
            // Given
            String llmResponse = """
                TYPE: DIAGRAM
                ```mermaid
                graph TD
                    A[Start] --> B[End]
                ```
                """;
            mockLlmResponse(llmResponse);
            byte[] imageData = "fake image".getBytes();

            // When
            ImageAnalysisResult result = imageAnalysisService.analyzeImage(imageData, "image/png");

            // Then
            assertThat(result.getType()).isEqualTo(ContentType.DIAGRAM);
            assertThat(result.getContent()).contains("graph TD");
            assertThat(result.getContent()).contains("A[Start] --> B[End]");
        }

        @Test
        @DisplayName("should detect DIAGRAM content type without code block")
        void shouldDetectDiagramWithoutCodeBlock() {
            // Given
            mockLlmResponse("TYPE: DIAGRAM\ngraph LR\n    A --> B");
            byte[] imageData = "fake image".getBytes();

            // When
            ImageAnalysisResult result = imageAnalysisService.analyzeImage(imageData, "image/png");

            // Then
            assertThat(result.getType()).isEqualTo(ContentType.DIAGRAM);
            assertThat(result.getContent()).contains("graph LR");
        }

        @Test
        @DisplayName("should detect MARKDOWN content type")
        void shouldDetectMarkdownContent() {
            // Given
            mockLlmResponse("TYPE: MARKDOWN\n# Heading\n- Item 1\n- Item 2");
            byte[] imageData = "fake image".getBytes();

            // When
            ImageAnalysisResult result = imageAnalysisService.analyzeImage(imageData, "image/png");

            // Then
            assertThat(result.getType()).isEqualTo(ContentType.MARKDOWN);
            assertThat(result.getContent()).contains("# Heading");
            assertThat(result.getContent()).contains("- Item 1");
        }

        @Test
        @DisplayName("should detect IMAGE content type for photos")
        void shouldDetectImageContent() {
            // Given
            mockLlmResponse("TYPE: IMAGE\nA photo of a sunset over the ocean.");
            byte[] imageData = "fake image".getBytes();

            // When
            ImageAnalysisResult result = imageAnalysisService.analyzeImage(imageData, "image/jpeg");

            // Then
            assertThat(result.getType()).isEqualTo(ContentType.IMAGE);
            assertThat(result.getContent()).isEqualTo("A photo of a sunset over the ocean.");
        }

        @Test
        @DisplayName("should default to IMAGE when no type marker found")
        void shouldDefaultToImageWhenNoTypeMarker() {
            // Given
            mockLlmResponse("This is just a description without a type marker.");
            byte[] imageData = "fake image".getBytes();

            // When
            ImageAnalysisResult result = imageAnalysisService.analyzeImage(imageData, "image/png");

            // Then
            assertThat(result.getType()).isEqualTo(ContentType.IMAGE);
        }
    }

    @Nested
    @DisplayName("cleanEscapeSequences")
    class CleanEscapeSequences {

        @Test
        @DisplayName("should convert escaped newlines to actual newlines")
        void shouldConvertEscapedNewlines() throws Exception {
            // Given
            String input = "Line 1\\nLine 2\\nLine 3";

            // When
            String result = invokeCleanEscapeSequences(input);

            // Then
            assertThat(result).isEqualTo("Line 1\nLine 2\nLine 3");
        }

        @Test
        @DisplayName("should convert escaped quotes to actual quotes")
        void shouldConvertEscapedQuotes() throws Exception {
            // Given
            String input = "He said \\\"Hello\\\"";

            // When
            String result = invokeCleanEscapeSequences(input);

            // Then
            assertThat(result).isEqualTo("He said \"Hello\"");
        }

        @Test
        @DisplayName("should convert escaped backslashes")
        void shouldConvertEscapedBackslashes() throws Exception {
            // Given
            String input = "Path: C:\\\\Users\\\\test";

            // When
            String result = invokeCleanEscapeSequences(input);

            // Then
            assertThat(result).isEqualTo("Path: C:\\Users\\test");
        }

        @Test
        @DisplayName("should trim whitespace")
        void shouldTrimWhitespace() throws Exception {
            // Given
            String input = "  content with spaces  ";

            // When
            String result = invokeCleanEscapeSequences(input);

            // Then
            assertThat(result).isEqualTo("content with spaces");
        }

        private String invokeCleanEscapeSequences(String input) throws Exception {
            Method method = ImageAnalysisService.class.getDeclaredMethod("cleanEscapeSequences", String.class);
            method.setAccessible(true);
            return (String) method.invoke(imageAnalysisService, input);
        }
    }

    @Nested
    @DisplayName("cleanMermaidCode")
    class CleanMermaidCode {

        @Test
        @DisplayName("should remove double spaces")
        void shouldRemoveDoubleSpaces() throws Exception {
            // Given
            String input = "graph TD\n    A  -->  B";

            // When
            String result = invokeCleanMermaidCode(input);

            // Then
            assertThat(result).isEqualTo("graph TD\n A --> B");
        }

        @Test
        @DisplayName("should clean escape sequences in mermaid code")
        void shouldCleanEscapeSequencesInMermaid() throws Exception {
            // Given
            String input = "graph TD\\n    A --> B";

            // When
            String result = invokeCleanMermaidCode(input);

            // Then
            assertThat(result).isEqualTo("graph TD\n A --> B");
        }

        private String invokeCleanMermaidCode(String input) throws Exception {
            Method method = ImageAnalysisService.class.getDeclaredMethod("cleanMermaidCode", String.class);
            method.setAccessible(true);
            return (String) method.invoke(imageAnalysisService, input);
        }
    }

    @Nested
    @DisplayName("ImageAnalysisResult")
    class ImageAnalysisResultTests {

        @Test
        @DisplayName("should get and set type")
        void shouldGetAndSetType() {
            ImageAnalysisResult result = new ImageAnalysisResult();
            
            result.setType(ContentType.DIAGRAM);
            
            assertThat(result.getType()).isEqualTo(ContentType.DIAGRAM);
        }

        @Test
        @DisplayName("should get and set content")
        void shouldGetAndSetContent() {
            ImageAnalysisResult result = new ImageAnalysisResult();
            
            result.setContent("test content");
            
            assertThat(result.getContent()).isEqualTo("test content");
        }
    }
}
