package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JSemester;
import com.hei.course.model.CourseSpeciality;
import com.hei.course.model.SpecialityEnum;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CourseSpecialityCrudIT extends AbstractAdminCrudIT {

  private CourseSpeciality newCourseSpeciality() {
    return CourseSpeciality.builder().speciality(SpecialityEnum.EL).build();
  }

  private String createUrl(UUID coursesId, UUID semesterId) {
    return "/course-specialities?coursesId=" + coursesId + "&semesterId=" + semesterId;
  }

  private CourseSpeciality create(JCourses course, JSemester semester) {
    return restTemplate
        .postForEntity(
            createUrl(course.getId(), semester.getId()),
            newCourseSpeciality(),
            CourseSpeciality.class)
        .getBody();
  }

  @Test
  void create_persists_the_course_speciality() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();

    ResponseEntity<CourseSpeciality> response =
        restTemplate.postForEntity(
            createUrl(course.getId(), semester.getId()),
            newCourseSpeciality(),
            CourseSpeciality.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    CourseSpeciality created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getSpeciality()).isEqualTo(SpecialityEnum.EL);
    assertThat(created.getCourses().getId()).isEqualTo(course.getId());
    assertThat(created.getSemester().getId()).isEqualTo(semester.getId());
  }

  @Test
  void create_with_unknown_course_returns_404() {
    JSemester semester = persistSemester();

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            createUrl(UUID.randomUUID(), semester.getId()), newCourseSpeciality(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void create_with_unknown_semester_returns_404() {
    JCourses course = persistCourse();

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            createUrl(course.getId(), UUID.randomUUID()), newCourseSpeciality(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void create_with_duplicate_speciality_for_same_course_and_semester_is_rejected_cleanly() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();
    create(course, semester);

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            createUrl(course.getId(), semester.getId()), newCourseSpeciality(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void find_all_returns_created_course_specialities() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();
    CourseSpeciality created = create(course, semester);

    ResponseEntity<CourseSpeciality[]> response =
        restTemplate.getForEntity("/course-specialities", CourseSpeciality[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(CourseSpeciality::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_course_speciality() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();
    CourseSpeciality created = create(course, semester);

    ResponseEntity<CourseSpeciality> response =
        restTemplate.getForEntity(
            "/course-specialities/" + created.getId(), CourseSpeciality.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/course-specialities/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void find_by_course_returns_matching_course_specialities() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();
    CourseSpeciality created = create(course, semester);

    ResponseEntity<CourseSpeciality[]> response =
        restTemplate.getForEntity(
            "/course-specialities/course/" + course.getId(), CourseSpeciality[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(CourseSpeciality::getId).contains(created.getId());
  }

  @Test
  void find_by_course_returns_404_for_unknown_course() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/course-specialities/course/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_changes_the_course_speciality_fields() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();
    CourseSpeciality created = create(course, semester);
    JCourses otherCourse = persistCourse();

    CourseSpeciality updatePayload =
        CourseSpeciality.builder().speciality(SpecialityEnum.TN).build();

    ResponseEntity<CourseSpeciality> response =
        restTemplate.exchange(
            "/course-specialities/"
                + created.getId()
                + "?coursesId="
                + otherCourse.getId()
                + "&semesterId="
                + semester.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            CourseSpeciality.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getSpeciality()).isEqualTo(SpecialityEnum.TN);
    assertThat(response.getBody().getCourses().getId()).isEqualTo(otherCourse.getId());
  }

  @Test
  void update_of_unknown_id_returns_404() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/course-specialities/"
                + UUID.randomUUID()
                + "?coursesId="
                + course.getId()
                + "&semesterId="
                + semester.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(newCourseSpeciality()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_course_speciality() {
    JCourses course = persistCourse();
    JSemester semester = persistSemester();
    CourseSpeciality created = create(course, semester);

    restTemplate.delete("/course-specialities/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/course-specialities/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/course-specialities/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
