package com.mvolkert.note2mermaid.repository;

import com.mvolkert.note2mermaid.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
}

