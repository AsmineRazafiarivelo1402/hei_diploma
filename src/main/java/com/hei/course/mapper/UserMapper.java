package com.hei.course.mapper;

import com.hei.course.entity.JAdmin;
import com.hei.course.entity.JStudent;
import com.hei.course.entity.JTeacher;
import com.hei.course.entity.JUsers;
import com.hei.course.model.Admin;
import com.hei.course.model.Student;
import com.hei.course.model.Teacher;
import com.hei.course.model.Users;

public class UserMapper {

  private UserMapper() {}

  public static Users toModel(JUsers entity) {
    Users user;

    if (entity instanceof JStudent) {
      user = new Student();
    } else if (entity instanceof JTeacher) {
      user = new Teacher();
    } else if (entity instanceof JAdmin) {
      user = new Admin();
    } else {
      throw new IllegalArgumentException("Unknown users: " + entity.getClass());
    }

    user.setId(entity.getId());
    user.setReference(entity.getReference());
    user.setFirstName(entity.getFirstName());
    user.setLastName(entity.getLastName());
    user.setBirthdate(entity.getBirthdate());
    user.setEmail(entity.getEmail());
    user.setPassword(entity.getPassword());
    user.setAddress(entity.getAddress());
    user.setPhoneNumber(entity.getPhoneNumber());
    user.setRole(entity.getRole());

    return user;
  }

  public static JUsers toEntity(Users user) {
    JUsers entity;

    if (user instanceof Student) {
      entity = new JStudent();
    } else if (user instanceof Teacher) {
      entity = new JTeacher();
    } else if (user instanceof Admin) {
      entity = new JAdmin();
    } else {
      throw new IllegalArgumentException("Unknown users: " + user.getClass());
    }

    entity.setId(user.getId());
    entity.setReference(user.getReference());
    entity.setFirstName(user.getFirstName());
    entity.setLastName(user.getLastName());
    entity.setBirthdate(user.getBirthdate());
    entity.setEmail(user.getEmail());
    entity.setPassword(user.getPassword());
    entity.setAddress(user.getAddress());
    entity.setPhoneNumber(user.getPhoneNumber());
    entity.setRole(user.getRole());

    return entity;
  }
}
