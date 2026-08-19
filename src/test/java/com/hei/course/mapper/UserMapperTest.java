package com.hei.course.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.hei.course.entity.JAdmin;
import com.hei.course.entity.JStudent;
import com.hei.course.entity.JTeacher;
import com.hei.course.entity.JUsers;
import com.hei.course.model.Admin;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Student;
import com.hei.course.model.Teacher;
import com.hei.course.model.Users;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserMapperTest {

  private final Instant birthdate = Instant.parse("2000-01-01T00:00:00Z");

  @Test
  void maps_student_entity_to_student_model() {
    JStudent entity = studentEntity();
    entity.setId(UUID.randomUUID());

    Users model = UserMapper.toModel(entity);

    assertThat(model).isInstanceOf(Student.class);
    assertThat(model.getId()).isEqualTo(entity.getId());
    assertThat(model.getReference()).isEqualTo("REF-1");
    assertThat(model.getFirstName()).isEqualTo("Fenitra");
    assertThat(model.getLastName()).isEqualTo("Rakoto");
    assertThat(model.getBirthdate()).isEqualTo(birthdate);
    assertThat(model.getEmail()).isEqualTo("fenitra@example.com");
    assertThat(model.getAddress()).isEqualTo("Antananarivo");
    assertThat(model.getPhoneNumber()).isEqualTo("+261");
    assertThat(model.getRole()).isEqualTo(RoleEnum.STUDENT);
    assertThat(model.getPassword()).isEqualTo("secret");
  }

  @Test
  void maps_teacher_entity_to_teacher_model() {
    JTeacher entity = new JTeacher();
    entity.setId(UUID.randomUUID());
    entity.setReference("REF-T");
    entity.setRole(RoleEnum.TEACHER);

    Users model = UserMapper.toModel(entity);

    assertThat(model).isInstanceOf(Teacher.class);
    assertThat(model.getReference()).isEqualTo("REF-T");
    assertThat(model.getRole()).isEqualTo(RoleEnum.TEACHER);
  }

  @Test
  void maps_admin_entity_to_admin_model() {
    JAdmin entity = new JAdmin();
    entity.setId(UUID.randomUUID());
    entity.setReference("REF-A");
    entity.setRole(RoleEnum.ADMIN);

    Users model = UserMapper.toModel(entity);

    assertThat(model).isInstanceOf(Admin.class);
    assertThat(model.getReference()).isEqualTo("REF-A");
    assertThat(model.getRole()).isEqualTo(RoleEnum.ADMIN);
  }

  @Test
  void throws_when_mapping_unknown_entity_type() {
    JUsers unknown = mock(JUsers.class);

    assertThatThrownBy(() -> UserMapper.toModel(unknown))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown users");
  }

  @Test
  void maps_student_model_to_student_entity() {
    Student model = new Student();
    model.setId(UUID.randomUUID());
    model.setReference("REF-1");
    model.setFirstName("Fenitra");
    model.setLastName("Rakoto");
    model.setBirthdate(birthdate);
    model.setEmail("fenitra@example.com");
    model.setAddress("Antananarivo");
    model.setPhoneNumber("+261");
    model.setRole(RoleEnum.STUDENT);
    model.setPassword("secret");

    JUsers entity = UserMapper.toEntity(model);

    assertThat(entity).isInstanceOf(JStudent.class);
    assertThat(entity.getId()).isEqualTo(model.getId());
    assertThat(entity.getReference()).isEqualTo("REF-1");
    assertThat(entity.getFirstName()).isEqualTo("Fenitra");
    assertThat(entity.getLastName()).isEqualTo("Rakoto");
    assertThat(entity.getBirthdate()).isEqualTo(birthdate);
    assertThat(entity.getEmail()).isEqualTo("fenitra@example.com");
    assertThat(entity.getAddress()).isEqualTo("Antananarivo");
    assertThat(entity.getPhoneNumber()).isEqualTo("+261");
    assertThat(entity.getRole()).isEqualTo(RoleEnum.STUDENT);
    assertThat(entity.getPassword()).isEqualTo("secret");
  }

  @Test
  void maps_teacher_model_to_teacher_entity() {
    Teacher model = new Teacher();
    model.setReference("REF-T");
    model.setRole(RoleEnum.TEACHER);

    JUsers entity = UserMapper.toEntity(model);

    assertThat(entity).isInstanceOf(JTeacher.class);
    assertThat(entity.getReference()).isEqualTo("REF-T");
  }

  @Test
  void maps_admin_model_to_admin_entity() {
    Admin model = new Admin();
    model.setReference("REF-A");
    model.setRole(RoleEnum.ADMIN);

    JUsers entity = UserMapper.toEntity(model);

    assertThat(entity).isInstanceOf(JAdmin.class);
    assertThat(entity.getReference()).isEqualTo("REF-A");
  }

  @Test
  void throws_when_mapping_unknown_model_type() {
    Users unknown = mock(Users.class);

    assertThatThrownBy(() -> UserMapper.toEntity(unknown))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown users");
  }

  @Test
  void private_constructor_is_inaccessible() throws Exception {
    Constructor<UserMapper> constructor = UserMapper.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    UserMapper instance = constructor.newInstance();
    assertThat(instance).isNotNull();
  }

  private JStudent studentEntity() {
    JStudent entity = new JStudent();
    entity.setReference("REF-1");
    entity.setFirstName("Fenitra");
    entity.setLastName("Rakoto");
    entity.setBirthdate(birthdate);
    entity.setEmail("fenitra@example.com");
    entity.setAddress("Antananarivo");
    entity.setPhoneNumber("+261");
    entity.setRole(RoleEnum.STUDENT);
    entity.setPassword("secret");
    return entity;
  }
}
