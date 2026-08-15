package com.hei.course.repository;


import com.hei.course.entity.JNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JNoteRepository extends JpaRepository<JNote, UUID> {

    List<JNote> findByStudent_Id(UUID studentId);

    List<JNote> findByExam_Id(UUID examId);
}
