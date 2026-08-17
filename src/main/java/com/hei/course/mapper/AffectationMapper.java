package com.hei.course.mapper;

import com.hei.course.entity.JAffectation;
import com.hei.course.entity.JGroup;
import com.hei.course.entity.JSemester;
import com.hei.course.entity.JStudent;
import com.hei.course.model.Affectation;

public class AffectationMapper {

  private AffectationMapper() {}

  public static Affectation toModel(JAffectation entity) {
    return Affectation.builder()
        .id(entity.getId())
        .student(StudentMapper.toModel(entity.getStudent()))
        .group(GroupMapper.toModel(entity.getGroup()))
        .semestre(SemesterMapper.toModel(entity.getSemester()))
        .status(entity.getStatus())
        .build();
  }

  public static JAffectation toEntity(
      Affectation model, JStudent student, JGroup group, JSemester semester) {

    JAffectation entity = new JAffectation();

    entity.setId(model.getId());
    entity.setStudent(student);
    entity.setGroup(group);
    entity.setSemester(semester);
    entity.setStatus(model.getStatus());

    return entity;
  }
}
