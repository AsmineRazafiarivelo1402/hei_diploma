package com.hei.course.repository;

import com.hei.course.entity.JExam;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JExamRepository extends JpaRepository<JExam, UUID> {

  List<JExam> findByCourses_Id(UUID coursesId);

  List<JExam> findBySemester_Id(UUID semesterId);

  @Query("SELECT e FROM JExam e JOIN FETCH e.semester WHERE e.id = :id")
  Optional<JExam> findByIdWithSemester(@Param("id") UUID id);
}
