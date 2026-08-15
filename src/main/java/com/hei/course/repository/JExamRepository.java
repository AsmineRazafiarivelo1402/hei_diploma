package com.hei.course.repository;

import com.hei.course.entity.JExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JExamRepository extends JpaRepository<JExam, UUID> {

    List<JExam> findByCourses_Id(UUID coursesId);

    List<JExam> findBySemestre_Id(UUID semestreId);
}
