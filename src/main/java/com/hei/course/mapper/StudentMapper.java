package com.hei.course.mapper;

import com.hei.course.entity.JStudent;
import com.hei.course.model.Student;

public class StudentMapper {

  private StudentMapper() {}

  public static Student toModel(JStudent entity) {
    return Student.builder()
        .id(entity.getId())
        .reference(entity.getReference())
        .firstName(entity.getFirstName())
        .lastName(entity.getLastName())
        .birthdate(entity.getBirthdate())
        .email(entity.getEmail())
        .address(entity.getAddress())
        .phoneNumber(entity.getPhoneNumber())
        .role(entity.getRole())
        .password(entity.getPassword())
        .build();
  }

  public static JStudent toEntity(Student model) {
    return JStudent.builder()
        .id(model.getId())
        .reference(model.getReference())
        .firstName(model.getFirstName())
        .lastName(model.getLastName())
        .birthdate(model.getBirthdate())
        .email(model.getEmail())
        .address(model.getAddress())
        .phoneNumber(model.getPhoneNumber())
        .role(model.getRole())
        .password(model.getPassword())
        .build();
  }
}
