package com.hei.course.repository;

import com.hei.course.entity.JNoteHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JNoteHistoryRepository extends JpaRepository<JNoteHistory, UUID> {

  List<JNoteHistory> findByNote_Id(UUID noteId);
}
