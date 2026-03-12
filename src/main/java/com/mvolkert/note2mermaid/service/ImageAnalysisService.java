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
        
        String analysisPrompt = """
            Transkribiere den Inhalt dieses Bildes exakt.
            
            WICHTIGE REGELN:
            - Keine Interpretationen oder Beschreibungen hinzufügen
            - Nur den tatsächlichen Inhalt ausgeben
            - Rechtschreibfehler und Grammatikfehler korrigieren
            - Schlechte Wortwahl verbessern (z.B. umgangssprachlich → korrekt)
            - Die ursprüngliche Struktur beibehalten
            
            AUSGABEFORMAT:
            
            Bei Diagrammen (Flowchart, Sequenzdiagramm, Mindmap, etc.):
            - Antworte mit "TYPE: DIAGRAM"
            - Generiere gültigen Mermaid-Code zwischen ```mermaid und ```
            - Behalte die Struktur und Verbindungen exakt bei
            
            Bei strukturiertem Text (Überschriften, Listen, Tabellen):
            - Antworte mit "TYPE: MARKDOWN"
            - Gib den korrigierten Text als Markdown aus:
              - Überschriften: # ## ###
              - Aufzählungen: - oder *
              - Nummerierungen: 1. 2. 3.
            - Keine zusätzlichen Erklärungen
            
            Bei Fließtext (Absätze ohne Struktur):
            - Antworte mit "TYPE: TEXT"
            - Gib nur den korrigierten Text aus
            - Keine Einleitung wie "Der Text lautet:"
            
            Bei Fotos/Grafiken ohne Text:
            - Antworte mit "TYPE: IMAGE"
            - Kurze sachliche Beschreibung (max. 1 Satz)
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
        } else if (response.contains("TYPE: MARKDOWN")) {
            result.setType(ContentType.MARKDOWN);
            String markdownContent = response.substring(response.indexOf("TYPE: MARKDOWN") + 14).trim();
            // Escape-Sequenzen bereinigen (LLM gibt manchmal \n statt echte Newlines)
            markdownContent = cleanEscapeSequences(markdownContent);
            result.setContent(markdownContent);
        } else if (response.contains("TYPE: TEXT")) {
            result.setType(ContentType.TEXT);
            String textContent = response.substring(response.indexOf("TYPE: TEXT") + 10).trim();
            textContent = cleanEscapeSequences(textContent);
            result.setContent(textContent);
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
        return cleanEscapeSequences(code)
                // Doppelte Leerzeichen entfernen
                .replaceAll("  +", " ");
    }

    /**
     * Bereinigt Text von Escape-Sequenzen (z.B. \n, \", \\).
     */
    private String cleanEscapeSequences(String text) {
        return text
                // Escaped newlines zu echten Zeilenumbrüchen
                .replace("\\n", "\n")
                // Escaped Quotes zu normalen Quotes
                .replace("\\\"", "\"")
                // Escaped Backslashes
                .replace("\\\\", "\\")
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
        IMAGE,
        MARKDOWN
    }
}
