package com.hei.course.mapper;

import com.hei.course.entity.JCourseSpeciality;
import com.hei.course.entity.JCourses;
import com.hei.course.entity.JSemester;
import com.hei.course.model.CourseSpeciality;

public class CourseSpecialityMapper {

  private CourseSpecialityMapper() {}

  public static CourseSpeciality toModel(JCourseSpeciality entity) {

    return CourseSpeciality.builder()
        .id(entity.getId())
        .courses(CourseMapper.toModel(entity.getCourses()))
        .speciality(entity.getSpecialityEnum())
        .semester(SemesterMapper.toModel(entity.getSemester()))
        .build();
  }

  public static JCourseSpeciality toEntity(
      CourseSpeciality model, JCourses courses, JSemester semester) {

    JCourseSpeciality entity = new JCourseSpeciality();

    entity.setId(model.getId());
    entity.setCourses(courses);
    entity.setSpecialityEnum(model.getSpeciality());
    entity.setSemester(semester);

    return entity;
  }
}
