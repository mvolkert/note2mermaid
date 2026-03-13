# ADR-003: LangChain4j with LM Studio

**Status:** Accepted  
**Date:** 2024

## Context

Integration of a Vision LLM for image analysis (OCR and diagram recognition).

## Decision

LangChain4j as abstraction layer, LM Studio as local OpenAI-compatible server.

## Rationale

- LangChain4j provides unified API for different LLM providers
- LM Studio enables local model execution without cloud dependency
- OpenAI-compatible API facilitates future provider changes
- No API costs, full data control

## Consequences

### Configuration

```properties
lmstudio.base-url=http://127.0.0.1:1234/v1
lmstudio.model-name=ministral-3b
```

### Prerequisites

- LM Studio must be running before app start
- Vision model (e.g., ministral-3b) must be loaded
- Port 1234 must be available

### LangChain4j Dependency

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>${langchain4j.version}</version>
</dependency>
```

### Mermaid Code Cleanup

The LLM sometimes returns escape sequences:

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
