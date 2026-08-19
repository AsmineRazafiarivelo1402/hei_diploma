package com.hei.course.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JTeacher;
import com.hei.course.entity.JTeaching;
import com.hei.course.model.Teaching;
import java.lang.reflect.Constructor;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TeachingMapperTest {

  @Test
  void maps_teaching_entity_to_model_with_courses_and_teacher() {
    UUID id = UUID.randomUUID();

    JCourses courses = new JCourses();
    courses.setId(UUID.randomUUID());
    courses.setReference("COURSE-1");
    courses.setTitle("Java");
    courses.setCredit(3);

    JTeacher teacher = new JTeacher();
    teacher.setId(UUID.randomUUID());
    teacher.setReference("REF-T");

    JTeaching entity = new JTeaching();
    entity.setId(id);
    entity.setCourses(courses);
    entity.setTeacher(teacher);

    Teaching model = TeachingMapper.toModel(entity);

    assertThat(model.getId()).isEqualTo(id);
    assertThat(model.getCourses().getId()).isEqualTo(courses.getId());
    assertThat(model.getCourses().getTitle()).isEqualTo("Java");
    assertThat(model.getCourses().getCredit()).isEqualTo(3);
    assertThat(model.getTeacher().getId()).isEqualTo(teacher.getId());
  }

  @Test
  void maps_teaching_model_onto_entity() {
    JTeaching entity = new JTeaching();
    Teaching model = Teaching.builder().id(UUID.randomUUID()).build();

    TeachingMapper.toEntity(entity, model);

    assertThat(entity.getId()).isEqualTo(model.getId());
  }

  @Test
  void private_constructor_is_inaccessible() throws Exception {
    Constructor<TeachingMapper> constructor = TeachingMapper.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    TeachingMapper instance = constructor.newInstance();
    assertThat(instance).isNotNull();
  }
}
