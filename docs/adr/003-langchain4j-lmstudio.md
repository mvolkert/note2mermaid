# ADR-003: LangChain4j mit LM Studio

**Status:** Akzeptiert  
**Datum:** 2024

## Kontext

Integration eines Vision-LLMs für Bildanalyse (OCR und Diagramm-Erkennung).

## Entscheidung

LangChain4j als Abstraktionsschicht, LM Studio als lokaler OpenAI-kompatibler Server.

## Begründung

- LangChain4j bietet einheitliche API für verschiedene LLM-Provider
- LM Studio ermöglicht lokale Modellausführung ohne Cloud-Abhängigkeit
- OpenAI-kompatible API erleichtert zukünftigen Provider-Wechsel
- Keine API-Kosten, volle Datenkontrolle

## Konsequenzen

### Konfiguration

```properties
lmstudio.base-url=http://127.0.0.1:1234/v1
lmstudio.model-name=ministral-3b
```

### Voraussetzungen

- LM Studio muss vor App-Start laufen
- Vision-Modell (z.B. ministral-3b) muss geladen sein
- Port 1234 muss frei sein

### LangChain4j Dependency

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>${langchain4j.version}</version>
</dependency>
```

### Mermaid-Code Bereinigung

Das LLM gibt manchmal Escape-Sequenzen zurück:

```java
private String cleanMermaidCode(String code) {
    return code
        .replace("\\n", "\n")
        .replace("\\\"", "\"")
        .replace("\\\\", "\\")
        .replaceAll("  +", " ")
        .trim();
}
```
