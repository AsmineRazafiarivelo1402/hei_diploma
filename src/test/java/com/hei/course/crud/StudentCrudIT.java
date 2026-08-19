package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.conf.FacadeIT;
import com.hei.course.entity.JAdmin;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Student;
import com.hei.course.repository.JAdminRepository;
import com.hei.course.repository.JStudentRepository;
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

class StudentCrudIT extends FacadeIT {

  private static final String RAW_PASSWORD = "P@ssw0rd!";

  @Autowired private TestRestTemplate rawRestTemplate;
  @Autowired private JAdminRepository adminRepository;
  @Autowired private JStudentRepository studentRepository;
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

  private Student newStudent() {
    String suffix = UUID.randomUUID().toString();
    return Student.builder()
        .reference("REF-" + suffix)
        .firstName("Jean")
        .lastName("Rakoto")
        .birthdate(Instant.parse("2000-01-01T00:00:00Z"))
        .email("student-" + suffix + "@hei.test")
        .address("Antananarivo")
        .phoneNumber("0340000000")
        .password(RAW_PASSWORD)
        .build();
  }

  private Student create(Student payload) {
    return restTemplate.postForEntity("/students", payload, Student.class).getBody();
  }

  @Test
  void create_persists_the_student_and_hashes_the_password() {
    Student payload = newStudent();

    ResponseEntity<Student> response =
        restTemplate.postForEntity("/students", payload, Student.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Student created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getReference()).isEqualTo(payload.getReference());
    assertThat(created.getEmail()).isEqualTo(payload.getEmail());
    assertThat(created.getRole()).isEqualTo(RoleEnum.STUDENT);

    String storedHash = studentRepository.findById(created.getId()).orElseThrow().getPassword();
    assertThat(storedHash).isNotEqualTo(RAW_PASSWORD);
    assertThat(passwordEncoder.matches(RAW_PASSWORD, storedHash)).isTrue();
  }

  @Test
  void create_with_duplicate_email_should_be_rejected_cleanly_not_with_a_500() {
    Student first = newStudent();
    create(first);

    Student duplicate = newStudent();
    duplicate.setEmail(first.getEmail());

    ResponseEntity<String> response =
        restTemplate.postForEntity("/students", duplicate, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void create_with_duplicate_reference_should_be_rejected_cleanly_not_with_a_500() {
    Student first = newStudent();
    create(first);

    Student duplicate = newStudent();
    duplicate.setReference(first.getReference());

    ResponseEntity<String> response =
        restTemplate.postForEntity("/students", duplicate, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void find_all_returns_created_students() {
    Student created = create(newStudent());

    ResponseEntity<Student[]> response = restTemplate.getForEntity("/students", Student[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Student::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_student() {
    Student created = create(newStudent());

    ResponseEntity<Student> response =
        restTemplate.getForEntity("/students/" + created.getId(), Student.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getEmail()).isEqualTo(created.getEmail());
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/students/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_changes_the_student_fields() {
    Student created = create(newStudent());

    Student updatePayload = newStudent();
    updatePayload.setFirstName("Updated");
    updatePayload.setPassword(null);

    restTemplate.put("/students/" + created.getId(), updatePayload);

    Student reloaded =
        restTemplate.getForEntity("/students/" + created.getId(), Student.class).getBody();
    assertThat(reloaded.getFirstName()).isEqualTo("Updated");
  }

  @Test
  void update_with_blank_password_keeps_the_previous_hash() {
    Student created = create(newStudent());
    String originalHash = studentRepository.findById(created.getId()).orElseThrow().getPassword();

    Student updatePayload = newStudent();
    updatePayload.setPassword("");

    restTemplate.put("/students/" + created.getId(), updatePayload);

    String hashAfterUpdate =
        studentRepository.findById(created.getId()).orElseThrow().getPassword();
    assertThat(hashAfterUpdate).isEqualTo(originalHash);
  }

  @Test
  void update_with_new_password_rehashes_it() {
    Student created = create(newStudent());
    String originalHash = studentRepository.findById(created.getId()).orElseThrow().getPassword();

    Student updatePayload = newStudent();
    updatePayload.setPassword("N3wP@ssword!");

    restTemplate.put("/students/" + created.getId(), updatePayload);

    String hashAfterUpdate =
        studentRepository.findById(created.getId()).orElseThrow().getPassword();
    assertThat(hashAfterUpdate).isNotEqualTo(originalHash);
    assertThat(passwordEncoder.matches("N3wP@ssword!", hashAfterUpdate)).isTrue();
  }

  @Test
  void update_with_email_taken_by_another_student_should_be_rejected_cleanly() {
    Student first = create(newStudent());
    Student second = create(newStudent());

    Student updatePayload = newStudent();
    updatePayload.setEmail(first.getEmail());
    updatePayload.setPassword(null);

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/students/" + second.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void update_keeping_the_same_email_is_allowed() {
    Student created = create(newStudent());

    Student updatePayload = newStudent();
    updatePayload.setEmail(created.getEmail());
    updatePayload.setReference(created.getReference());
    updatePayload.setFirstName("Updated");
    updatePayload.setPassword(null);

    ResponseEntity<Student> response =
        restTemplate.exchange(
            "/students/" + created.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            Student.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getFirstName()).isEqualTo("Updated");
  }

  @Test
  void update_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/students/" + UUID.randomUUID(),
            HttpMethod.PUT,
            new HttpEntity<>(newStudent()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_student() {
    Student created = create(newStudent());

    restTemplate.delete("/students/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/students/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/students/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
