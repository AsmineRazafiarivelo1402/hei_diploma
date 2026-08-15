package com.hei.course.repository;

import com.hei.course.entity.JExam;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JExamRepository extends JpaRepository<JExam, UUID> {

  List<JExam> findByCourses_Id(UUID coursesId);

  List<JExam> findBySemester_Id(UUID semesterId);
}
