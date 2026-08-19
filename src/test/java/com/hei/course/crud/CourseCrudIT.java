package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.conf.FacadeIT;
import com.hei.course.entity.JAdmin;
import com.hei.course.model.Courses;
import com.hei.course.model.RoleEnum;
import com.hei.course.repository.JAdminRepository;
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

class CourseCrudIT extends FacadeIT {

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

  private Courses newCourse() {
    String suffix = UUID.randomUUID().toString();
    return Courses.builder().reference("CRS-" + suffix).title("Algorithmique").credit(5).build();
  }

  private Courses create(Courses payload) {
    return restTemplate.postForEntity("/courses", payload, Courses.class).getBody();
  }

  @Test
  void create_persists_the_course() {
    Courses payload = newCourse();

    ResponseEntity<Courses> response =
        restTemplate.postForEntity("/courses", payload, Courses.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Courses created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getReference()).isEqualTo(payload.getReference());
    assertThat(created.getTitle()).isEqualTo(payload.getTitle());
    assertThat(created.getCredit()).isEqualTo(payload.getCredit());
  }

  @Test
  void find_all_returns_created_courses() {
    Courses created = create(newCourse());

    ResponseEntity<Courses[]> response = restTemplate.getForEntity("/courses", Courses[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Courses::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_course() {
    Courses created = create(newCourse());

    ResponseEntity<Courses> response =
        restTemplate.getForEntity("/courses/" + created.getId(), Courses.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getTitle()).isEqualTo(created.getTitle());
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/courses/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_changes_the_course_fields() {
    Courses created = create(newCourse());

    Courses updatePayload = newCourse();
    updatePayload.setTitle("Structures de données");

    ResponseEntity<Courses> response =
        restTemplate.exchange(
            "/courses/" + created.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            Courses.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getTitle()).isEqualTo("Structures de données");
  }

  @Test
  void update_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/courses/" + UUID.randomUUID(),
            HttpMethod.PUT,
            new HttpEntity<>(newCourse()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_course() {
    Courses created = create(newCourse());

    restTemplate.delete("/courses/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/courses/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/courses/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void bonjour_endpoint_is_public_and_does_not_require_authentication() {
    ResponseEntity<String> response =
        rawRestTemplate.getForEntity("/courses/bonjour", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Bonjour à tous");
  }
}
