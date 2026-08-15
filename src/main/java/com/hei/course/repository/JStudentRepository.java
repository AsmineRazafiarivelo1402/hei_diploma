package com.hei.course.repository;


import com.hei.course.entity.JStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JStudentRepository extends JpaRepository<JStudent, UUID> {
}