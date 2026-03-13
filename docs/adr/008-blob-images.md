# ADR-008: Image Storage as BLOB in SQLite

**Status:** Accepted  
**Date:** 2024

## Context

Storage of camera images (photos for OCR/diagram analysis).

## Decision

Store images as `byte[]` directly in the SQLite database.

## Rationale

- Simple backup (everything in one file)
- No filesystem management required
- Transactional consistency with note data
- No orphaned files on deletion

## Consequences

### Entity Mapping

```java
@Entity
public class Note {
    
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data")
    private byte[] imageData;
    
    @Column(name = "image_type")
    private String imageType; // e.g., "image/png"
}
```

**Important:** Do not use `@Lob`! SQLite JDBC does not support it.

### Lazy Loading

- Images are only loaded on explicit access
- Separate endpoint for image retrieval: `GET /api/notes/{id}/image`
- Note list does not load image data

### Liquibase Migration

```yaml
- changeSet:
    id: 002-add-image-columns
    changes:
      - addColumn:
          tableName: notes
          columns:
            - column:
                name: image_data
                type: BLOB
            - column:
                name: image_type
                type: VARCHAR(50)
```

### Limitations

- Database size grows with images
- For very large images (>10MB) filesystem may be better
- No direct URL for images (must be loaded via API)
