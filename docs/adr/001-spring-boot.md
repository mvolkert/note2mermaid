# ADR-001: Spring Boot 4.0 mit Java 17

**Status:** Akzeptiert  
**Datum:** 2024

## Kontext

Wahl des Backend-Frameworks und der Java-Version für eine lokale Desktop-Webapp.

## Entscheidung

Spring Boot 4.0.3 mit Java 17 (LTS).

## Begründung

- Spring Boot 4.0 bietet neueste Features und Sicherheitsupdates
- Java 17 ist LTS mit guter Balance zwischen neuen Features und Stabilität
- Große Community und gute Dokumentation

## Konsequenzen

### Breaking Changes in Spring Boot 4.0

**Test-Annotationen:**
- `@MockBean` → `@MockitoBean`
- Neues Package: `org.springframework.test.context.bean.override.mockito`

**Test-Module aufgeteilt:**
- `@WebMvcTest` liegt in `spring-boot-webmvc-test-autoconfigure`
- `TestRestTemplate` in separatem Modul
- Nicht alle Artifacts sind verfügbar → Pure Mockito Tests verwenden (siehe ADR-007)

### Dependencies

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.3</version>
</parent>

<properties>
    <java.version>17</java.version>
</properties>
```
