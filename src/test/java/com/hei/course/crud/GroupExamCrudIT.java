package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JExam;
import com.hei.course.entity.JGroup;
import com.hei.course.model.ExamTypeEnum;
import com.hei.course.model.GroupExam;
import com.hei.course.model.SpecialityEnum;
import com.hei.course.repository.JExamRepository;
import com.hei.course.repository.JGroupRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GroupExamCrudIT extends AbstractAdminCrudIT {

  @Autowired private JGroupRepository groupRepository;
  @Autowired private JExamRepository examRepository;

  private JGroup persistGroup() {
    JGroup group = new JGroup();
    group.setReference("K-" + UUID.randomUUID());
    group.setSpeciality(SpecialityEnum.COMMON);
    group.setPromotion(persistPromotion());
    return groupRepository.save(group);
  }

  private JExam persistExam() {
    JExam exam = new JExam();
    exam.setDate(Instant.parse("2024-10-15T08:00:00Z"));
    exam.setCoefficient(new BigDecimal("2.00"));
    exam.setExamType(ExamTypeEnum.UNIQUE);
    exam.setCourses(persistCourse());
    exam.setSemester(persistSemester());
    return examRepository.save(exam);
  }

  private GroupExam newGroupExam() {
    return GroupExam.builder().build();
  }

  private String url(UUID groupId, UUID examId) {
    return "/group-exams?groupId=" + groupId + "&examId=" + examId;
  }

  private GroupExam create(JGroup group, JExam exam) {
    return restTemplate
        .postForEntity(url(group.getId(), exam.getId()), newGroupExam(), GroupExam.class)
        .getBody();
  }

  @Test
  void create_persists_the_group_exam() {
    JGroup group = persistGroup();
    JExam exam = persistExam();

    ResponseEntity<GroupExam> response =
        restTemplate.postForEntity(
            url(group.getId(), exam.getId()), newGroupExam(), GroupExam.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    GroupExam created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getGroup().getId()).isEqualTo(group.getId());
    assertThat(created.getExam().getId()).isEqualTo(exam.getId());
  }

  @Test
  void create_with_unknown_group_returns_404() {
    JExam exam = persistExam();

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            url(UUID.randomUUID(), exam.getId()), newGroupExam(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void create_with_unknown_exam_returns_404() {
    JGroup group = persistGroup();

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            url(group.getId(), UUID.randomUUID()), newGroupExam(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void create_when_exam_already_assigned_to_group_is_rejected_cleanly() {
    JGroup group = persistGroup();
    JExam exam = persistExam();
    create(group, exam);

    ResponseEntity<String> response =
        restTemplate.postForEntity(url(group.getId(), exam.getId()), newGroupExam(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void find_all_returns_created_group_exams() {
    JGroup group = persistGroup();
    JExam exam = persistExam();
    GroupExam created = create(group, exam);

    ResponseEntity<GroupExam[]> response =
        restTemplate.getForEntity("/group-exams", GroupExam[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(GroupExam::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_group_exam() {
    JGroup group = persistGroup();
    JExam exam = persistExam();
    GroupExam created = create(group, exam);

    ResponseEntity<GroupExam> response =
        restTemplate.getForEntity("/group-exams/" + created.getId(), GroupExam.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/group-exams/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void find_by_group_returns_matching_group_exams() {
    JGroup group = persistGroup();
    JExam exam = persistExam();
    GroupExam created = create(group, exam);

    ResponseEntity<GroupExam[]> response =
        restTemplate.getForEntity("/group-exams/group/" + group.getId(), GroupExam[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(GroupExam::getId).contains(created.getId());
  }

  @Test
  void find_by_group_returns_404_for_unknown_group() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/group-exams/group/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_changes_the_group_exam_assignment() {
    JGroup group = persistGroup();
    JExam exam = persistExam();
    GroupExam created = create(group, exam);
    JGroup otherGroup = persistGroup();

    ResponseEntity<GroupExam> response =
        restTemplate.exchange(
            "/group-exams/"
                + created.getId()
                + "?groupId="
                + otherGroup.getId()
                + "&examId="
                + exam.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(newGroupExam()),
            GroupExam.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getGroup().getId()).isEqualTo(otherGroup.getId());
  }

  @Test
  void update_of_unknown_id_returns_404() {
    JGroup group = persistGroup();
    JExam exam = persistExam();

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/group-exams/"
                + UUID.randomUUID()
                + "?groupId="
                + group.getId()
                + "&examId="
                + exam.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(newGroupExam()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_group_exam() {
    JGroup group = persistGroup();
    JExam exam = persistExam();
    GroupExam created = create(group, exam);

    restTemplate.delete("/group-exams/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/group-exams/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/group-exams/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
