package com.hei.course.repository;

import com.hei.course.entity.JTeacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JTeacherRepository extends JpaRepository<JTeacher, UUID> {
}
