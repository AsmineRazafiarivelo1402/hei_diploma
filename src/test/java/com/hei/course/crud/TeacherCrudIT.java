package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.conf.FacadeIT;
import com.hei.course.entity.JAdmin;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Teacher;
import com.hei.course.repository.JAdminRepository;
import com.hei.course.repository.JTeacherRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

class TeacherCrudIT extends FacadeIT {

  private static final String RAW_PASSWORD = "P@ssw0rd!";

  @Autowired private TestRestTemplate rawRestTemplate;
  @Autowired private JAdminRepository adminRepository;
  @Autowired private JTeacherRepository teacherRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private TestRestTemplate restTemplate;

  @BeforeEach
  void authenticateAsAdmin() {
    String adminEmail = "admin-" + UUID.randomUUID() + "@hei.test";

    JAdmin admin = new JAdmin();
    admin.setReference(UUID.randomUUID().toString());
    admin.setFirstName("Test");
    admin.setLastName("Admin");
    admin.setEmail(adminEmail);
    admin.setRole(RoleEnum.ADMIN);
    admin.setPassword(passwordEncoder.encode(RAW_PASSWORD));
    adminRepository.save(admin);

    restTemplate = rawRestTemplate.withBasicAuth(adminEmail, RAW_PASSWORD);
  }

  private Teacher newTeacher() {
    String suffix = UUID.randomUUID().toString();
    return Teacher.builder()
        .reference("REF-" + suffix)
        .firstName("Hery")
        .lastName("Andria")
        .birthdate(Instant.parse("1985-01-01T00:00:00Z"))
        .email("teacher-" + suffix + "@hei.test")
        .address("Antananarivo")
        .phoneNumber("0330000000")
        .password(RAW_PASSWORD)
        .build();
  }

  private Teacher create(Teacher payload) {
    return restTemplate.postForEntity("/teachers", payload, Teacher.class).getBody();
  }

  @Test
  void create_persists_the_teacher_and_hashes_the_password() {
    Teacher payload = newTeacher();

    ResponseEntity<Teacher> response =
        restTemplate.postForEntity("/teachers", payload, Teacher.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Teacher created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getEmail()).isEqualTo(payload.getEmail());
    assertThat(created.getRole()).isEqualTo(RoleEnum.TEACHER);

    String storedHash = teacherRepository.findById(created.getId()).orElseThrow().getPassword();
    assertThat(storedHash).isNotEqualTo(RAW_PASSWORD);
    assertThat(passwordEncoder.matches(RAW_PASSWORD, storedHash)).isTrue();
  }

  @Test
  void create_with_duplicate_email_should_be_rejected_cleanly_not_with_a_500() {
    Teacher first = newTeacher();
    create(first);

    Teacher duplicate = newTeacher();
    duplicate.setEmail(first.getEmail());

    ResponseEntity<String> response =
        restTemplate.postForEntity("/teachers", duplicate, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void create_with_duplicate_reference_should_be_rejected_cleanly_not_with_a_500() {
    Teacher first = newTeacher();
    create(first);

    Teacher duplicate = newTeacher();
    duplicate.setReference(first.getReference());

    ResponseEntity<String> response =
        restTemplate.postForEntity("/teachers", duplicate, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void find_all_returns_created_teachers() {
    Teacher created = create(newTeacher());

    ResponseEntity<Teacher[]> response = restTemplate.getForEntity("/teachers", Teacher[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Teacher::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_teacher() {
    Teacher created = create(newTeacher());

    ResponseEntity<Teacher> response =
        restTemplate.getForEntity("/teachers/" + created.getId(), Teacher.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getEmail()).isEqualTo(created.getEmail());
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/teachers/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_changes_the_teacher_fields() {
    Teacher created = create(newTeacher());

    Teacher updatePayload = newTeacher();
    updatePayload.setFirstName("Updated");
    updatePayload.setPassword(null);

    restTemplate.put("/teachers/" + created.getId(), updatePayload);

    Teacher reloaded =
        restTemplate.getForEntity("/teachers/" + created.getId(), Teacher.class).getBody();
    assertThat(reloaded.getFirstName()).isEqualTo("Updated");
  }

  @Test
  void update_with_blank_password_keeps_the_previous_hash() {
    Teacher created = create(newTeacher());
    String originalHash = teacherRepository.findById(created.getId()).orElseThrow().getPassword();

    Teacher updatePayload = newTeacher();
    updatePayload.setPassword("");

    restTemplate.put("/teachers/" + created.getId(), updatePayload);

    String hashAfterUpdate =
        teacherRepository.findById(created.getId()).orElseThrow().getPassword();
    assertThat(hashAfterUpdate).isEqualTo(originalHash);
  }

  @Test
  void update_with_new_password_rehashes_it() {
    Teacher created = create(newTeacher());
    String originalHash = teacherRepository.findById(created.getId()).orElseThrow().getPassword();

    Teacher updatePayload = newTeacher();
    updatePayload.setPassword("N3wP@ssword!");

    restTemplate.put("/teachers/" + created.getId(), updatePayload);

    String hashAfterUpdate =
        teacherRepository.findById(created.getId()).orElseThrow().getPassword();
    assertThat(hashAfterUpdate).isNotEqualTo(originalHash);
    assertThat(passwordEncoder.matches("N3wP@ssword!", hashAfterUpdate)).isTrue();
  }

  @Test
  void update_with_email_taken_by_another_teacher_should_be_rejected_cleanly() {
    Teacher first = create(newTeacher());
    Teacher second = create(newTeacher());

    Teacher updatePayload = newTeacher();
    updatePayload.setEmail(first.getEmail());
    updatePayload.setPassword(null);

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/teachers/" + second.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void update_with_reference_taken_by_another_teacher_should_be_rejected_cleanly() {
    Teacher first = create(newTeacher());
    Teacher second = create(newTeacher());

    Teacher updatePayload = newTeacher();
    updatePayload.setReference(first.getReference());
    updatePayload.setPassword(null);

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/teachers/" + second.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void update_keeping_the_same_email_and_reference_is_allowed() {
    Teacher created = create(newTeacher());

    Teacher updatePayload = newTeacher();
    updatePayload.setEmail(created.getEmail());
    updatePayload.setReference(created.getReference());
    updatePayload.setFirstName("Updated");
    updatePayload.setPassword(null);

    ResponseEntity<Teacher> response =
        restTemplate.exchange(
            "/teachers/" + created.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            Teacher.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getFirstName()).isEqualTo("Updated");
  }

  @Test
  void update_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/teachers/" + UUID.randomUUID(),
            HttpMethod.PUT,
            new HttpEntity<>(newTeacher()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_teacher() {
    Teacher created = create(newTeacher());

    restTemplate.delete("/teachers/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/teachers/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/teachers/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
