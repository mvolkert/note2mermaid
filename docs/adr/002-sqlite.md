# ADR-002: SQLite as Database

**Status:** Accepted  
**Date:** 2024

## Context

Choosing a database for a local desktop application with single-user operation.

## Decision

SQLite with Hibernate/JPA and Liquibase for schema migrations.

## Rationale

- No separate database installation required
- File-based, easy to backup and transport
- Sufficient for single-user application
- Zero configuration

## Consequences

### SQLite JDBC Limitations

**`@Lob` does not work:**
```java
// WRONG - throws SQLFeatureNotSupportedException
@Lob
private byte[] imageData;

// CORRECT
@Basic(fetch = FetchType.LAZY)
@Column(name = "image_data")
private byte[] imageData;
```

### Configuration

```properties
spring.datasource.url=jdbc:sqlite:note2mermaid.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
```

### Liquibase Migrations

Path: `src/main/resources/db/changelog/`

```yaml
# db.changelog-master.yaml
databaseChangeLog:
  - include:
      file: changes/001-create-notes-table.yaml
      relativeToChangelogFile: true
```
