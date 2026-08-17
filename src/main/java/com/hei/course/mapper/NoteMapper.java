package com.hei.course.mapper;

import com.hei.course.entity.JExam;
import com.hei.course.entity.JNote;
import com.hei.course.entity.JStudent;
import com.hei.course.model.Exam;
import com.hei.course.model.Note;
import com.hei.course.model.Student;

public class NoteMapper {

  private NoteMapper() {}

  public static Note toModel(JNote entity) {

    Exam exam = null;
    if (entity.getExam() != null) {
      exam =
          Exam.builder()
              .id(entity.getExam().getId())
              .date(entity.getExam().getDate())
              .coefficient(entity.getExam().getCoefficient())
              .examType(entity.getExam().getExamType())
              .build();
    }

    Student student = null;
    if (entity.getStudent() != null) {
      student =
          Student.builder()
              .id(entity.getStudent().getId())
              .reference(entity.getStudent().getReference())
              .firstName(entity.getStudent().getFirstName())
              .lastName(entity.getStudent().getLastName())
              .birthdate(entity.getStudent().getBirthdate())
              .email(entity.getStudent().getEmail())
              .address(entity.getStudent().getAddress())
              .phoneNumber(entity.getStudent().getPhoneNumber())
              .role(entity.getStudent().getRole())
              .build();
    }

    return Note.builder()
        .id(entity.getId())
        .value(entity.getValue())
        .date(entity.getDate())
        .exam(exam)
        .student(student)
        .build();
  }

  public static JNote toEntity(Note model, JExam exam, JStudent student) {

    JNote entity = new JNote();

    entity.setId(model.getId());
    entity.setValue(model.getValue());
    entity.setDate(model.getDate());
    entity.setExam(exam);
    entity.setStudent(student);

    return entity;
  }
}
