package com.hei.course.mapper;

import com.hei.course.entity.JTeacher;
import com.hei.course.model.Teacher;

public class TeacherMapper {

  private TeacherMapper() {}

  public static Teacher toModel(JTeacher entity) {
    return Teacher.builder()
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

  public static JTeacher toEntity(Teacher model) {
    return JTeacher.builder()
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
