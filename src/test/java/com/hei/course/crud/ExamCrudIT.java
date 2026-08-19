package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JSemester;
import com.hei.course.model.Exam;
import com.hei.course.model.ExamTypeEnum;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ExamCrudIT extends AbstractAdminCrudIT {

  private Exam newExam() {
    return Exam.builder()
        .date(Instant.parse("2024-10-15T08:00:00Z"))
        .coefficient(new BigDecimal("2.00"))
        .examType(ExamTypeEnum.UNIQUE)
        .build();
  }

  private String url(UUID courseId, UUID semesterId) {
    return "/exams?courseId=" + courseId + "&semesterId=" + semesterId;
  }

  private Exam create(JCourses course, JSemester semester) {
    return restTemplate
        .postForEntity(url(course.getId(), semester.getId()), newExam(), Exam.class)
        .getBody();
  }

  @Test
  void create_persists_the_exam() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();

    ResponseEntity<Exam> response =
        restTemplate.postForEntity(url(course.getId(), semester.getId()), newExam(), Exam.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Exam created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getExamType()).isEqualTo(ExamTypeEnum.UNIQUE);
    assertThat(created.getCoefficient()).isEqualByComparingTo(new BigDecimal("2.00"));
  }

  @Test
  void create_with_unknown_course_returns_404() {
    JSemester semester = persistSemester();

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            url(UUID.randomUUID(), semester.getId()), newExam(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void create_with_unknown_semester_returns_404() {
    JCourses course = persistCourse();

    ResponseEntity<String> response =
        restTemplate.postForEntity(url(course.getId(), UUID.randomUUID()), newExam(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void find_all_returns_created_exams() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();
    Exam created = create(course, semester);

    ResponseEntity<Exam[]> response = restTemplate.getForEntity("/exams", Exam[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Exam::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_exam() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();
    Exam created = create(course, semester);

    ResponseEntity<Exam> response =
        restTemplate.getForEntity("/exams/" + created.getId(), Exam.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/exams/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_changes_the_exam_fields() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();
    Exam created = create(course, semester);
    JCourses otherCourse = persistCourse();

    Exam updatePayload = newExam();
    updatePayload.setExamType(ExamTypeEnum.CONTINUOUS);

    ResponseEntity<Exam> response =
        restTemplate.exchange(
            "/exams/"
                + created.getId()
                + "?courseId="
                + otherCourse.getId()
                + "&semesterId="
                + semester.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            Exam.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getExamType()).isEqualTo(ExamTypeEnum.CONTINUOUS);
  }

  @Test
  void update_of_unknown_id_returns_404() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/exams/"
                + UUID.randomUUID()
                + "?courseId="
                + course.getId()
                + "&semesterId="
                + semester.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(newExam()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_exam() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();
    Exam created = create(course, semester);

    restTemplate.delete("/exams/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/exams/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange("/exams/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
