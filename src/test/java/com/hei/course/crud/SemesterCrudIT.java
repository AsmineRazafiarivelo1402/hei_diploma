package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.conf.FacadeIT;
import com.hei.course.entity.JAdmin;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Semester;
import com.hei.course.model.SemesterEnum;
import com.hei.course.repository.JAdminRepository;
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

class SemesterCrudIT extends FacadeIT {

  private static final String RAW_PASSWORD = "P@ssw0rd!";

  @Autowired private TestRestTemplate rawRestTemplate;
  @Autowired private JAdminRepository adminRepository;
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

  private Semester newSemester() {
    return Semester.builder()
        .semestreEnum(SemesterEnum.S1)
        .startDate(Instant.parse("2024-09-01T00:00:00Z"))
        .endDate(Instant.parse("2024-12-31T00:00:00Z"))
        .build();
  }

  private Semester create(Semester payload) {
    return restTemplate.postForEntity("/semesters", payload, Semester.class).getBody();
  }

  @Test
  void create_persists_the_semester() {
    Semester payload = newSemester();

    ResponseEntity<Semester> response =
        restTemplate.postForEntity("/semesters", payload, Semester.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Semester created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getSemestreEnum()).isEqualTo(SemesterEnum.S1);
  }

  @Test
  void find_all_returns_created_semesters() {
    Semester created = create(newSemester());

    ResponseEntity<Semester[]> response = restTemplate.getForEntity("/semesters", Semester[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Semester::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_semester() {
    Semester created = create(newSemester());

    ResponseEntity<Semester> response =
        restTemplate.getForEntity("/semesters/" + created.getId(), Semester.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getSemestreEnum()).isEqualTo(SemesterEnum.S1);
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/semesters/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_changes_the_semester_fields() {
    Semester created = create(newSemester());

    Semester updatePayload = newSemester();
    updatePayload.setSemestreEnum(SemesterEnum.S2);

    ResponseEntity<Semester> response =
        restTemplate.exchange(
            "/semesters/" + created.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            Semester.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getSemestreEnum()).isEqualTo(SemesterEnum.S2);
  }

  @Test
  void update_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/semesters/" + UUID.randomUUID(),
            HttpMethod.PUT,
            new HttpEntity<>(newSemester()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_semester() {
    Semester created = create(newSemester());

    restTemplate.delete("/semesters/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/semesters/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/semesters/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
