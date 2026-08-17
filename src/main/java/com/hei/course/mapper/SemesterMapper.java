package com.hei.course.mapper;

import com.hei.course.entity.JSemester;
import com.hei.course.model.Semester;

public class SemesterMapper {

  private SemesterMapper() {}

  public static Semester toModel(JSemester entity) {
    return Semester.builder()
        .id(entity.getId())
        .semestreEnum(entity.getSemesterEnum())
        .startDate(entity.getStartDate())
        .endDate(entity.getEndDate())
        .build();
  }

  public static JSemester toEntity(Semester model) {
    JSemester entity = new JSemester();

    entity.setId(model.getId());
    entity.setSemesterEnum(model.getSemestreEnum());
    entity.setStartDate(model.getStartDate());
    entity.setEndDate(model.getEndDate());

    return entity;
  }
}
