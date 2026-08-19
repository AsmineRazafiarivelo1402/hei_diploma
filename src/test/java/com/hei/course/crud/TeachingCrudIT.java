package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JTeacher;
import com.hei.course.model.Teaching;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TeachingCrudIT extends AbstractAdminCrudIT {

  private String createUrl(UUID courseId, UUID teacherId) {
    return "/teachings?courseId=" + courseId + "&teacherId=" + teacherId;
  }

  private Teaching create(JCourses course, JTeacher teacher) {
    return restTemplate
        .postForEntity(createUrl(course.getId(), teacher.getId()), null, Teaching.class)
        .getBody();
  }

  @Test
  void create_persists_the_teaching_assignment() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();

    ResponseEntity<Teaching> response =
        restTemplate.postForEntity(
            createUrl(course.getId(), teacher.getId()), null, Teaching.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Teaching created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getCourses().getId()).isEqualTo(course.getId());
    assertThat(created.getTeacher().getId()).isEqualTo(teacher.getId());
  }

  @Test
  void create_with_unknown_course_returns_404() {
    JTeacher teacher = persistTeacher();

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            createUrl(UUID.randomUUID(), teacher.getId()), null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void create_with_unknown_teacher_returns_404() {
    JCourses course = persistCourse();

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            createUrl(course.getId(), UUID.randomUUID()), null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void create_with_teacher_already_assigned_to_course_is_rejected_cleanly() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();
    create(course, teacher);

    ResponseEntity<String> response =
        restTemplate.postForEntity(createUrl(course.getId(), teacher.getId()), null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void find_all_returns_created_teachings() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();
    Teaching created = create(course, teacher);

    ResponseEntity<Teaching[]> response = restTemplate.getForEntity("/teachings", Teaching[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Teaching::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_teaching() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();
    Teaching created = create(course, teacher);

    ResponseEntity<Teaching> response =
        restTemplate.getForEntity("/teachings/" + created.getId(), Teaching.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/teachings/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void find_by_teacher_returns_matching_teachings() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();
    Teaching created = create(course, teacher);

    ResponseEntity<Teaching[]> response =
        restTemplate.getForEntity("/teachings/teacher/" + teacher.getId(), Teaching[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Teaching::getId).contains(created.getId());
  }

  @Test
  void find_by_teacher_returns_404_for_unknown_teacher() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/teachings/teacher/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_changes_the_teaching_assignment() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();
    Teaching created = create(course, teacher);
    JTeacher otherTeacher = persistTeacher();

    ResponseEntity<Teaching> response =
        restTemplate.exchange(
            "/teachings/"
                + created.getId()
                + "?courseId="
                + course.getId()
                + "&teacherId="
                + otherTeacher.getId(),
            HttpMethod.PUT,
            null,
            Teaching.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getTeacher().getId()).isEqualTo(otherTeacher.getId());
  }

  @Test
  void update_of_unknown_id_returns_404() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/teachings/"
                + UUID.randomUUID()
                + "?courseId="
                + course.getId()
                + "&teacherId="
                + teacher.getId(),
            HttpMethod.PUT,
            null,
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_teaching() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();
    Teaching created = create(course, teacher);

    restTemplate.delete("/teachings/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/teachings/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/teachings/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
