package com.hei.course.mapper;

import com.hei.course.entity.JCourses;
import com.hei.course.model.Courses;

public class CourseMapper {

  private CourseMapper() {}

  public static Courses toModel(JCourses entity) {
    return Courses.builder()
        .id(entity.getId())
        .reference(entity.getReference())
        .title(entity.getTitle())
        .credit(entity.getCredit())
        .build();
  }

  public static JCourses toEntity(Courses model) {
    JCourses entity = new JCourses();

    entity.setId(model.getId());
    entity.setReference(model.getReference());
    entity.setTitle(model.getTitle());
    entity.setCredit(model.getCredit());

    return entity;
  }
}
