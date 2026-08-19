package com.hei.course.mapper;

import com.hei.course.entity.JAdmin;
import com.hei.course.entity.JStudent;
import com.hei.course.entity.JTeacher;
import com.hei.course.entity.JUsers;
import com.hei.course.model.Admin;
import com.hei.course.model.Student;
import com.hei.course.model.Teacher;
import com.hei.course.model.Users;
import org.hibernate.Hibernate;

public class UserMapper {

  private UserMapper() {}

  public static Users toModel(JUsers entity) {
    JUsers resolved = (JUsers) Hibernate.unproxy(entity);
    Users user;

    if (resolved instanceof JStudent) {
      user = new Student();
    } else if (resolved instanceof JTeacher) {
      user = new Teacher();
    } else if (resolved instanceof JAdmin) {
      user = new Admin();
    } else {
      throw new IllegalArgumentException("Unknown users: " + resolved.getClass());
    }

    user.setId(resolved.getId());
    user.setReference(resolved.getReference());
    user.setFirstName(resolved.getFirstName());
    user.setLastName(resolved.getLastName());
    user.setBirthdate(resolved.getBirthdate());
    user.setEmail(resolved.getEmail());
    user.setPassword(resolved.getPassword());
    user.setAddress(resolved.getAddress());
    user.setPhoneNumber(resolved.getPhoneNumber());
    user.setRole(resolved.getRole());

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
