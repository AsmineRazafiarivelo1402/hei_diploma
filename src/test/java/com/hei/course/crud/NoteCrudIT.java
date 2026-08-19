package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JExam;
import com.hei.course.entity.JStudent;
import com.hei.course.entity.JTeacher;
import com.hei.course.entity.JTeaching;
import com.hei.course.model.ExamTypeEnum;
import com.hei.course.model.Note;
import com.hei.course.repository.JExamRepository;
import com.hei.course.repository.JTeachingRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NoteCrudIT extends AbstractAdminCrudIT {

  @Autowired private JExamRepository examRepository;
  @Autowired private JTeachingRepository teachingRepository;

  private JExam persistExam(JCourses course) {
    JExam exam = new JExam();
    exam.setDate(Instant.parse("2024-10-15T08:00:00Z"));
    exam.setCoefficient(new BigDecimal("2.00"));
    exam.setExamType(ExamTypeEnum.UNIQUE);
    exam.setCourses(course);
    exam.setSemester(persistSemester());
    return examRepository.save(exam);
  }

  private void assignTeacherToCourse(JCourses course, JTeacher teacher) {
    JTeaching teaching = new JTeaching();
    teaching.setCourses(course);
    teaching.setTeacher(teacher);
    teachingRepository.save(teaching);
  }

  private TestRestTemplate asTeacher(JTeacher teacher) {
    return rawRestTemplate.withBasicAuth(teacher.getEmail(), RAW_PASSWORD);
  }

  private Note newNote() {
    return Note.builder().value(15.5).date(Instant.parse("2024-11-01T00:00:00Z")).build();
  }

  private String url(UUID examId, UUID studentId) {
    return "/notes?examId=" + examId + "&studentId=" + studentId;
  }

  @Test
  void create_by_teacher_teaching_the_course_persists_the_note() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();
    assignTeacherToCourse(course, teacher);
    JExam exam = persistExam(course);
    JStudent student = persistStudent();

    ResponseEntity<Note> response =
        asTeacher(teacher).postForEntity(url(exam.getId(), student.getId()), newNote(), Note.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Note created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getValue()).isEqualTo(15.5);
    assertThat(created.getStudent().getId()).isEqualTo(student.getId());
  }

  @Test
  void create_by_admin_is_allowed_regardless_of_teaching_assignment() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();

    ResponseEntity<Note> response =
        restTemplate.postForEntity(url(exam.getId(), student.getId()), newNote(), Note.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void create_by_teacher_not_teaching_the_course_is_forbidden() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();

    ResponseEntity<String> response =
        asTeacher(teacher)
            .postForEntity(url(exam.getId(), student.getId()), newNote(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void create_with_unknown_exam_returns_404() {
    JStudent student = persistStudent();

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            url(UUID.randomUUID(), student.getId()), newNote(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void create_with_unknown_student_returns_404() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);

    ResponseEntity<String> response =
        restTemplate.postForEntity(url(exam.getId(), UUID.randomUUID()), newNote(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void find_all_as_admin_returns_created_notes() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    Note created =
        restTemplate
            .postForEntity(url(exam.getId(), student.getId()), newNote(), Note.class)
            .getBody();

    ResponseEntity<Note[]> response = restTemplate.getForEntity("/notes", Note[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Note::getId).contains(created.getId());
  }

  @Test
  void find_all_as_teacher_only_returns_notes_for_own_courses() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();
    assignTeacherToCourse(course, teacher);
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    restTemplate.postForEntity(url(exam.getId(), student.getId()), newNote(), Note.class);

    JCourses otherCourse = persistCourse();
    JTeacher otherTeacher = persistTeacher();
    assignTeacherToCourse(otherCourse, otherTeacher);

    ResponseEntity<Note[]> response = asTeacher(otherTeacher).getForEntity("/notes", Note[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
  }

  @Test
  void find_by_id_returns_the_note_for_admin() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    Note created =
        restTemplate
            .postForEntity(url(exam.getId(), student.getId()), newNote(), Note.class)
            .getBody();

    ResponseEntity<Note> response =
        restTemplate.getForEntity("/notes/" + created.getId(), Note.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/notes/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void find_by_id_is_forbidden_for_teacher_not_teaching_the_course() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    Note created =
        restTemplate
            .postForEntity(url(exam.getId(), student.getId()), newNote(), Note.class)
            .getBody();

    JTeacher otherTeacher = persistTeacher();

    ResponseEntity<String> response =
        asTeacher(otherTeacher).getForEntity("/notes/" + created.getId(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void update_changes_the_note_value() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    Note created =
        restTemplate
            .postForEntity(url(exam.getId(), student.getId()), newNote(), Note.class)
            .getBody();

    Note updatePayload = newNote();
    updatePayload.setValue(18.0);

    ResponseEntity<Note> response =
        restTemplate.exchange(
            "/notes/"
                + created.getId()
                + "?examId="
                + exam.getId()
                + "&studentId="
                + student.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            Note.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getValue()).isEqualTo(18.0);
  }

  @Test
  void update_of_unknown_id_returns_404() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/notes/"
                + UUID.randomUUID()
                + "?examId="
                + exam.getId()
                + "&studentId="
                + student.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(newNote()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_note() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    Note created =
        restTemplate
            .postForEntity(url(exam.getId(), student.getId()), newNote(), Note.class)
            .getBody();

    restTemplate.delete("/notes/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/notes/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange("/notes/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
