# ADR-002: SQLite als Datenbank

**Status:** Akzeptiert  
**Datum:** 2024

## Kontext

Wahl der Datenbank für eine lokale Desktop-Anwendung mit Single-User-Betrieb.

## Entscheidung

SQLite mit Hibernate/JPA und Liquibase für Schema-Migrationen.

## Begründung

- Keine separate Datenbankinstallation nötig
- Datei-basiert, einfach zu sichern und zu transportieren
- Ausreichend für Single-User-Anwendung
- Zero-Configuration

## Konsequenzen

### SQLite JDBC Einschränkungen

**`@Lob` funktioniert nicht:**
```java
// FALSCH - wirft SQLFeatureNotSupportedException
@Lob
private byte[] imageData;

// RICHTIG
@Basic(fetch = FetchType.LAZY)
@Column(name = "image_data")
private byte[] imageData;
```

### Konfiguration

```properties
spring.datasource.url=jdbc:sqlite:note2mermaid.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
```

### Liquibase Migrationen

Pfad: `src/main/resources/db/changelog/`

```yaml
# db.changelog-master.yaml
databaseChangeLog:
  - include:
      file: changes/001-create-notes-table.yaml
      relativeToChangelogFile: true
```
