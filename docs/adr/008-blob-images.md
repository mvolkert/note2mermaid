# ADR-008: Bild-Speicherung als BLOB in SQLite

**Status:** Akzeptiert  
**Datum:** 2024

## Kontext

Speicherung von Kamerabildern (Fotos für OCR/Diagramm-Analyse).

## Entscheidung

Bilder als `byte[]` direkt in der SQLite-Datenbank speichern.

## Begründung

- Einfaches Backup (alles in einer Datei)
- Keine Dateisystem-Verwaltung nötig
- Transaktionale Konsistenz mit Notiz-Daten
- Keine verwaisten Dateien bei Löschung

## Konsequenzen

### Entity-Mapping

```java
@Entity
public class Note {
    
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data")
    private byte[] imageData;
    
    @Column(name = "image_type")
    private String imageType; // z.B. "image/png"
}
```

**Wichtig:** `@Lob` nicht verwenden! SQLite JDBC unterstützt es nicht.

### Lazy-Loading

- Bilder werden nur bei explizitem Zugriff geladen
- Separate Endpoint für Bild-Abruf: `GET /api/notes/{id}/image`
- Liste der Notizen lädt keine Bilddaten

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

### Limitierungen

- Datenbankgröße wächst mit Bildern
- Für sehr große Bilder (>10MB) evtl. Dateisystem besser
- Keine direkte URL für Bilder (muss über API geladen werden)
