package com.hei.course.repository;

import com.hei.course.entity.JStudent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JStudentRepository extends JpaRepository<JStudent, UUID> {}
