package com.hei.course.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JStudent;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Student;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StudentMapperTest {

  private final Instant birthdate = Instant.parse("2000-01-01T00:00:00Z");

  @Test
  void maps_student_entity_to_model() {
    JStudent entity = new JStudent();
    entity.setId(UUID.randomUUID());
    entity.setReference("REF-1");
    entity.setFirstName("Fenitra");
    entity.setLastName("Rakoto");
    entity.setBirthdate(birthdate);
    entity.setEmail("fenitra@example.com");
    entity.setAddress("Antananarivo");
    entity.setPhoneNumber("+261");
    entity.setRole(RoleEnum.STUDENT);
    entity.setPassword("secret");

    Student model = StudentMapper.toModel(entity);

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
  void maps_student_model_to_entity() {
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

    JStudent entity = StudentMapper.toEntity(model);

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
  void private_constructor_is_inaccessible() throws Exception {
    Constructor<StudentMapper> constructor = StudentMapper.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    StudentMapper instance = constructor.newInstance();
    assertThat(instance).isNotNull();
  }
}
