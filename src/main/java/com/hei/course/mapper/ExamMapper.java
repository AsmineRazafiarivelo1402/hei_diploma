package com.hei.course.mapper;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JExam;
import com.hei.course.entity.JSemester;
import com.hei.course.model.Exam;

public class ExamMapper {

  private ExamMapper() {}

  public static Exam toModel(JExam entity) {
    return Exam.builder()
        .id(entity.getId())
        .date(entity.getDate())
        .coefficient(entity.getCoefficient())
        .examType(entity.getExamType())
        .build();
  }

  public static JExam toEntity(Exam model, JCourses courses, JSemester semester) {

    JExam entity = new JExam();

    entity.setId(model.getId());
    entity.setDate(model.getDate());
    entity.setCoefficient(model.getCoefficient());
    entity.setExamType(model.getExamType());
    entity.setCourses(courses);
    entity.setSemester(semester);

    return entity;
  }
}
