package com.hei.course.repository;

import com.hei.course.entity.JCourses;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JCourseRepository extends JpaRepository<JCourses, UUID> {

  Optional<JCourses> findByReference(String reference);
}
