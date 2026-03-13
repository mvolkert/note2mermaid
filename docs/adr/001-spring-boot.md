# ADR-001: Spring Boot 4.0 with Java 17

**Status:** Accepted  
**Date:** 2024

## Context

Choosing the backend framework and Java version for a local desktop webapp.

## Decision

Spring Boot 4.0.3 with Java 17 (LTS).

## Rationale

- Spring Boot 4.0 offers the latest features and security updates
- Java 17 is LTS with a good balance between new features and stability
- Large community and good documentation

## Consequences

### Breaking Changes in Spring Boot 4.0

**Test Annotations:**
- `@MockBean` → `@MockitoBean`
- New package: `org.springframework.test.context.bean.override.mockito`

**Test Modules Split:**
- `@WebMvcTest` is in `spring-boot-webmvc-test-autoconfigure`
- `TestRestTemplate` in separate module
- Not all artifacts are available → Use pure Mockito tests (see ADR-007)

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
