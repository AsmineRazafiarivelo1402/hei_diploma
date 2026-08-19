package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JGroup;
import com.hei.course.entity.JPromotion;
import com.hei.course.entity.JSemester;
import com.hei.course.entity.JStudent;
import com.hei.course.model.Affectation;
import com.hei.course.model.SpecialityEnum;
import com.hei.course.model.StatusAffectationEnum;
import com.hei.course.repository.JGroupRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AffectationCrudIT extends AbstractAdminCrudIT {

  @Autowired private JGroupRepository groupRepository;

  private JGroup persistGroup(JPromotion promotion) {
    JGroup group = new JGroup();
    group.setReference("K-" + UUID.randomUUID());
    group.setSpeciality(SpecialityEnum.COMMON);
    group.setPromotion(promotion);
    return groupRepository.save(group);
  }

  private Affectation newAffectation() {
    return Affectation.builder().status(StatusAffectationEnum.PROVISIONAL).build();
  }

  private String url(UUID studentId, UUID groupId, UUID semesterId) {
    return "/affectations?studentId="
        + studentId
        + "&groupId="
        + groupId
        + "&semesterId="
        + semesterId;
  }

  private Affectation create(JStudent student, JGroup group, JSemester semester) {
    return restTemplate
        .postForEntity(
            url(student.getId(), group.getId(), semester.getId()),
            newAffectation(),
            Affectation.class)
        .getBody();
  }

  @Test
  void create_persists_the_affectation() {
    JStudent student = persistStudent();
    JGroup group = persistGroup(persistPromotion());
    JSemester semester = persistSemester();

    ResponseEntity<Affectation> response =
        restTemplate.postForEntity(
            url(student.getId(), group.getId(), semester.getId()),
            newAffectation(),
            Affectation.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Affectation created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getStudent().getId()).isEqualTo(student.getId());
    assertThat(created.getGroup().getId()).isEqualTo(group.getId());
    assertThat(created.getStatus()).isEqualTo(StatusAffectationEnum.PROVISIONAL);
  }

  @Test
  void create_with_unknown_student_returns_404() {
    JGroup group = persistGroup(persistPromotion());
    JSemester semester = persistSemester();

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            url(UUID.randomUUID(), group.getId(), semester.getId()),
            newAffectation(),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void create_with_unknown_group_returns_404() {
    JStudent student = persistStudent();
    JSemester semester = persistSemester();

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            url(student.getId(), UUID.randomUUID(), semester.getId()),
            newAffectation(),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void create_with_unknown_semester_returns_404() {
    JStudent student = persistStudent();
    JGroup group = persistGroup(persistPromotion());

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            url(student.getId(), group.getId(), UUID.randomUUID()), newAffectation(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void create_when_student_already_affected_for_semester_is_rejected_cleanly() {
    JStudent student = persistStudent();
    JGroup group = persistGroup(persistPromotion());
    JSemester semester = persistSemester();
    create(student, group, semester);

    JGroup otherGroup = persistGroup(persistPromotion());

    ResponseEntity<String> response =
        restTemplate.postForEntity(
            url(student.getId(), otherGroup.getId(), semester.getId()),
            newAffectation(),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void find_all_returns_created_affectations() {
    JStudent student = persistStudent();
    JGroup group = persistGroup(persistPromotion());
    JSemester semester = persistSemester();
    Affectation created = create(student, group, semester);

    ResponseEntity<Affectation[]> response =
        restTemplate.getForEntity("/affectations", Affectation[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Affectation::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_affectation() {
    JStudent student = persistStudent();
    JGroup group = persistGroup(persistPromotion());
    JSemester semester = persistSemester();
    Affectation created = create(student, group, semester);

    ResponseEntity<Affectation> response =
        restTemplate.getForEntity("/affectations/" + created.getId(), Affectation.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/affectations/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void find_by_student_returns_matching_affectations() {
    JStudent student = persistStudent();
    JGroup group = persistGroup(persistPromotion());
    JSemester semester = persistSemester();
    Affectation created = create(student, group, semester);

    ResponseEntity<Affectation[]> response =
        restTemplate.getForEntity("/affectations/student/" + student.getId(), Affectation[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Affectation::getId).contains(created.getId());
  }

  @Test
  void find_by_student_returns_404_for_unknown_student() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/affectations/student/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void find_by_promotion_returns_matching_affectations() {
    JPromotion promotion = persistPromotion();
    JStudent student = persistStudent();
    JGroup group = persistGroup(promotion);
    JSemester semester = persistSemester();
    Affectation created = create(student, group, semester);

    ResponseEntity<Affectation[]> response =
        restTemplate.getForEntity(
            "/affectations/promotion/" + promotion.getId(), Affectation[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Affectation::getId).contains(created.getId());
  }

  @Test
  void update_changes_the_affectation_status() {
    JStudent student = persistStudent();
    JGroup group = persistGroup(persistPromotion());
    JSemester semester = persistSemester();
    Affectation created = create(student, group, semester);

    Affectation updatePayload =
        Affectation.builder().status(StatusAffectationEnum.DEFINITIVE).build();

    ResponseEntity<Affectation> response =
        restTemplate.exchange(
            "/affectations/"
                + created.getId()
                + "?studentId="
                + student.getId()
                + "&groupId="
                + group.getId()
                + "&semesterId="
                + semester.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            Affectation.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getStatus()).isEqualTo(StatusAffectationEnum.DEFINITIVE);
  }

  @Test
  void update_of_unknown_id_returns_404() {
    JStudent student = persistStudent();
    JGroup group = persistGroup(persistPromotion());
    JSemester semester = persistSemester();

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/affectations/"
                + UUID.randomUUID()
                + "?studentId="
                + student.getId()
                + "&groupId="
                + group.getId()
                + "&semesterId="
                + semester.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(newAffectation()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_affectation() {
    JStudent student = persistStudent();
    JGroup group = persistGroup(persistPromotion());
    JSemester semester = persistSemester();
    Affectation created = create(student, group, semester);

    restTemplate.delete("/affectations/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/affectations/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/affectations/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
