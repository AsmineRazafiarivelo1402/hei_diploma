package com.hei.course.repository;

import com.hei.course.entity.JCourseSpeciality;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JCourseSpecialityRepository extends JpaRepository<JCourseSpeciality, UUID> {

  List<JCourseSpeciality> findByCourses_Id(UUID coursesId);
}
