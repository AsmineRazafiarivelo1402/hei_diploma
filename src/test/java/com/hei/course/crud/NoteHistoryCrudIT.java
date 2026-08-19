package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JExam;
import com.hei.course.entity.JNote;
import com.hei.course.entity.JStudent;
import com.hei.course.entity.JTeacher;
import com.hei.course.entity.JTeaching;
import com.hei.course.model.ExamTypeEnum;
import com.hei.course.model.NoteHistory;
import com.hei.course.repository.JExamRepository;
import com.hei.course.repository.JNoteRepository;
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

class NoteHistoryCrudIT extends AbstractAdminCrudIT {

  @Autowired private JExamRepository examRepository;
  @Autowired private JNoteRepository noteRepository;
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

  private JNote persistNote(JExam exam, JStudent student) {
    JNote note = new JNote();
    note.setValue(12.0);
    note.setDate(Instant.parse("2024-11-01T00:00:00Z"));
    note.setExam(exam);
    note.setStudent(student);
    return noteRepository.save(note);
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

  private NoteHistory newNoteHistory() {
    return NoteHistory.builder().oldValue(10.0).newValue(12.0).reason("Erreur de saisie").build();
  }

  private String url(UUID noteId) {
    return "/note-histories?noteId=" + noteId;
  }

  @Test
  void create_by_admin_persists_the_history() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    JNote note = persistNote(exam, student);

    ResponseEntity<NoteHistory> response =
        restTemplate.postForEntity(url(note.getId()), newNoteHistory(), NoteHistory.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    NoteHistory created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getReason()).isEqualTo("Erreur de saisie");
    assertThat(created.getUpdatedBy()).isNotNull();
  }

  @Test
  void create_by_teacher_teaching_the_course_is_allowed() {
    JCourses course = persistCourse();
    JTeacher teacher = persistTeacher();
    assignTeacherToCourse(course, teacher);
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    JNote note = persistNote(exam, student);

    ResponseEntity<NoteHistory> response =
        asTeacher(teacher).postForEntity(url(note.getId()), newNoteHistory(), NoteHistory.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void create_by_teacher_not_teaching_the_course_is_forbidden() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    JNote note = persistNote(exam, student);
    JTeacher otherTeacher = persistTeacher();

    ResponseEntity<String> response =
        asTeacher(otherTeacher).postForEntity(url(note.getId()), newNoteHistory(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void create_with_unknown_note_returns_404() {
    ResponseEntity<String> response =
        restTemplate.postForEntity(url(UUID.randomUUID()), newNoteHistory(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void find_all_as_admin_returns_created_histories() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    JNote note = persistNote(exam, student);
    NoteHistory created =
        restTemplate
            .postForEntity(url(note.getId()), newNoteHistory(), NoteHistory.class)
            .getBody();

    ResponseEntity<NoteHistory[]> response =
        restTemplate.getForEntity("/note-histories", NoteHistory[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(NoteHistory::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_history_for_admin() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    JNote note = persistNote(exam, student);
    NoteHistory created =
        restTemplate
            .postForEntity(url(note.getId()), newNoteHistory(), NoteHistory.class)
            .getBody();

    ResponseEntity<NoteHistory> response =
        restTemplate.getForEntity("/note-histories/" + created.getId(), NoteHistory.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/note-histories/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void find_by_id_is_forbidden_for_teacher_not_teaching_the_course() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    JNote note = persistNote(exam, student);
    NoteHistory created =
        restTemplate
            .postForEntity(url(note.getId()), newNoteHistory(), NoteHistory.class)
            .getBody();

    JTeacher otherTeacher = persistTeacher();

    ResponseEntity<String> response =
        asTeacher(otherTeacher).getForEntity("/note-histories/" + created.getId(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void update_changes_the_history_fields() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    JNote note = persistNote(exam, student);
    NoteHistory created =
        restTemplate
            .postForEntity(url(note.getId()), newNoteHistory(), NoteHistory.class)
            .getBody();

    NoteHistory updatePayload = newNoteHistory();
    updatePayload.setReason("Correction");

    ResponseEntity<NoteHistory> response =
        restTemplate.exchange(
            "/note-histories/" + created.getId() + "?noteId=" + note.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            NoteHistory.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getReason()).isEqualTo("Correction");
  }

  @Test
  void update_of_unknown_id_returns_404() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    JNote note = persistNote(exam, student);

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/note-histories/" + UUID.randomUUID() + "?noteId=" + note.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(newNoteHistory()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_history() {
    JCourses course = persistCourse();
    JExam exam = persistExam(course);
    JStudent student = persistStudent();
    JNote note = persistNote(exam, student);
    NoteHistory created =
        restTemplate
            .postForEntity(url(note.getId()), newNoteHistory(), NoteHistory.class)
            .getBody();

    restTemplate.delete("/note-histories/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/note-histories/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/note-histories/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
