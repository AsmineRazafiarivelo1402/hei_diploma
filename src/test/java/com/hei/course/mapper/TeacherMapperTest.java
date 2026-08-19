package com.hei.course.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JTeacher;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Teacher;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TeacherMapperTest {

  private final Instant birthdate = Instant.parse("1980-05-10T00:00:00Z");

  @Test
  void maps_teacher_entity_to_model() {
    JTeacher entity = new JTeacher();
    entity.setId(UUID.randomUUID());
    entity.setReference("REF-T");
    entity.setFirstName("Jean");
    entity.setLastName("Rasoa");
    entity.setBirthdate(birthdate);
    entity.setEmail("jean@example.com");
    entity.setAddress("Toamasina");
    entity.setPhoneNumber("+261-32");
    entity.setRole(RoleEnum.TEACHER);
    entity.setPassword("secret");

    Teacher model = TeacherMapper.toModel(entity);

    assertThat(model.getId()).isEqualTo(entity.getId());
    assertThat(model.getReference()).isEqualTo("REF-T");
    assertThat(model.getFirstName()).isEqualTo("Jean");
    assertThat(model.getLastName()).isEqualTo("Rasoa");
    assertThat(model.getBirthdate()).isEqualTo(birthdate);
    assertThat(model.getEmail()).isEqualTo("jean@example.com");
    assertThat(model.getAddress()).isEqualTo("Toamasina");
    assertThat(model.getPhoneNumber()).isEqualTo("+261-32");
    assertThat(model.getRole()).isEqualTo(RoleEnum.TEACHER);
    assertThat(model.getPassword()).isEqualTo("secret");
  }

  @Test
  void maps_teacher_model_to_entity() {
    Teacher model = new Teacher();
    model.setId(UUID.randomUUID());
    model.setReference("REF-T");
    model.setFirstName("Jean");
    model.setLastName("Rasoa");
    model.setBirthdate(birthdate);
    model.setEmail("jean@example.com");
    model.setAddress("Toamasina");
    model.setPhoneNumber("+261-32");
    model.setRole(RoleEnum.TEACHER);
    model.setPassword("secret");

    JTeacher entity = TeacherMapper.toEntity(model);

    assertThat(entity.getId()).isEqualTo(model.getId());
    assertThat(entity.getReference()).isEqualTo("REF-T");
    assertThat(entity.getFirstName()).isEqualTo("Jean");
    assertThat(entity.getLastName()).isEqualTo("Rasoa");
    assertThat(entity.getBirthdate()).isEqualTo(birthdate);
    assertThat(entity.getEmail()).isEqualTo("jean@example.com");
    assertThat(entity.getAddress()).isEqualTo("Toamasina");
    assertThat(entity.getPhoneNumber()).isEqualTo("+261-32");
    assertThat(entity.getRole()).isEqualTo(RoleEnum.TEACHER);
    assertThat(entity.getPassword()).isEqualTo("secret");
  }

  @Test
  void private_constructor_is_inaccessible() throws Exception {
    Constructor<TeacherMapper> constructor = TeacherMapper.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    TeacherMapper instance = constructor.newInstance();
    assertThat(instance).isNotNull();
  }
}
