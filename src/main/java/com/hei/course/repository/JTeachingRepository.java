package com.hei.course.repository;
import com.hei.course.entity.JTeaching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JTeachingRepository extends JpaRepository<JTeaching, UUID> {

    boolean existsByCourses_IdAndTeacher_Id(UUID coursesId, UUID teacherId);

    List<JTeaching> findByTeacher_Id(UUID teacherId);
}
