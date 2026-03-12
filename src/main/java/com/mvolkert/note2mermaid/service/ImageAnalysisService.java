package com.mvolkert.note2mermaid.service;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class ImageAnalysisService {

    @Value("${lmstudio.base-url}")
    private String baseUrl;

    @Value("${lmstudio.model-name}")
    private String modelName;

    private ChatLanguageModel chatModel;

    @PostConstruct
    public void init() {
        this.chatModel = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey("not-needed") // LM Studio benötigt keinen API Key
                .modelName(modelName)
                .maxTokens(2000)
                .temperature(0.3)
                .build();
    }

    /**
     * Analysiert ein Bild und gibt den erkannten Inhalt zurück.
     * Bei Diagrammen wird Mermaid-Code generiert, bei Text wird OCR durchgeführt.
     */
    public ImageAnalysisResult analyzeImage(byte[] imageData, String mimeType) {
        String base64Image = Base64.getEncoder().encodeToString(imageData);
        
        // Erst prüfen, ob es ein Diagramm ist
        String analysisPrompt = """
            Analysiere dieses Bild und bestimme, was es enthält.
            
            Wenn es ein Diagramm ist (Flowchart, Sequenzdiagramm, Klassendiagramm, etc.):
            - Antworte mit "TYPE: DIAGRAM"
            - Generiere danach gültigen Mermaid-Code, der das Diagramm repräsentiert
            - Der Mermaid-Code sollte zwischen ```mermaid und ``` stehen
            
            Wenn es Text enthält (Dokument, Whiteboard, Notizen, etc.):
            - Antworte mit "TYPE: TEXT"
            - Extrahiere den gesamten lesbaren Text
            
            Wenn es ein anderes Bild ist (Foto, Grafik, etc.):
            - Antworte mit "TYPE: IMAGE"
            - Beschreibe kurz den Bildinhalt
            """;

        UserMessage userMessage = UserMessage.from(
                TextContent.from(analysisPrompt),
                ImageContent.from(base64Image, mimeType)
        );

        String response = chatModel.generate(userMessage).content().text();
        
        return parseResponse(response);
    }

    private ImageAnalysisResult parseResponse(String response) {
        ImageAnalysisResult result = new ImageAnalysisResult();
        
        if (response.contains("TYPE: DIAGRAM")) {
            result.setType(ContentType.DIAGRAM);
            // Mermaid-Code extrahieren
            int start = response.indexOf("```mermaid");
            int end = response.indexOf("```", start + 10);
            String mermaidCode;
            if (start != -1 && end != -1) {
                mermaidCode = response.substring(start + 10, end).trim();
            } else {
                // Falls kein Code-Block, alles nach TYPE: DIAGRAM nehmen
                mermaidCode = response.substring(response.indexOf("TYPE: DIAGRAM") + 13).trim();
            }
            // Escape-Sequenzen bereinigen
            mermaidCode = cleanMermaidCode(mermaidCode);
            result.setContent(mermaidCode);
        } else if (response.contains("TYPE: TEXT")) {
            result.setType(ContentType.TEXT);
            result.setContent(response.substring(response.indexOf("TYPE: TEXT") + 10).trim());
        } else {
            result.setType(ContentType.IMAGE);
            result.setContent(response.replace("TYPE: IMAGE", "").trim());
        }
        
        return result;
    }

    /**
     * Bereinigt Mermaid-Code von Escape-Sequenzen und formatiert ihn korrekt.
     */
    private String cleanMermaidCode(String code) {
        return code
                // Escaped newlines zu echten Zeilenumbrüchen
                .replace("\\n", "\n")
                // Escaped Quotes zu normalen Quotes
                .replace("\\\"", "\"")
                // Escaped Backslashes
                .replace("\\\\", "\\")
                // Doppelte Leerzeichen entfernen
                .replaceAll("  +", " ")
                // Leere Zeilen am Anfang/Ende entfernen
                .trim();
    }

    public static class ImageAnalysisResult {
        private ContentType type;
        private String content;

        public ContentType getType() {
            return type;
        }

        public void setType(ContentType type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public enum ContentType {
        TEXT,
        DIAGRAM,
        IMAGE
    }
}
