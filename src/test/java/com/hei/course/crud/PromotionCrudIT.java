package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.conf.FacadeIT;
import com.hei.course.entity.JAdmin;
import com.hei.course.model.Promotion;
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

class PromotionCrudIT extends FacadeIT {

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

  private Promotion newPromotion() {
    int start = 2000 + Math.abs(UUID.randomUUID().hashCode() % 500);
    return Promotion.builder().startYear(start).endYear(start + 3).build();
  }

  private Promotion create(Promotion payload) {
    return restTemplate.postForEntity("/promotions", payload, Promotion.class).getBody();
  }

  @Test
  void create_persists_the_promotion() {
    Promotion payload = newPromotion();

    ResponseEntity<Promotion> response =
        restTemplate.postForEntity("/promotions", payload, Promotion.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Promotion created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getStartYear()).isEqualTo(payload.getStartYear());
    assertThat(created.getEndYear()).isEqualTo(payload.getEndYear());
  }

  @Test
  void create_with_duplicate_years_should_be_rejected_cleanly_not_with_a_500() {
    Promotion first = newPromotion();
    create(first);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/promotions", first, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void find_all_returns_created_promotions() {
    Promotion created = create(newPromotion());

    ResponseEntity<Promotion[]> response =
        restTemplate.getForEntity("/promotions", Promotion[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Promotion::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_promotion() {
    Promotion created = create(newPromotion());

    ResponseEntity<Promotion> response =
        restTemplate.getForEntity("/promotions/" + created.getId(), Promotion.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getStartYear()).isEqualTo(created.getStartYear());
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/promotions/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_changes_the_promotion_fields() {
    Promotion created = create(newPromotion());

    Promotion updatePayload = newPromotion();

    ResponseEntity<Promotion> response =
        restTemplate.exchange(
            "/promotions/" + created.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            Promotion.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getStartYear()).isEqualTo(updatePayload.getStartYear());
    assertThat(response.getBody().getEndYear()).isEqualTo(updatePayload.getEndYear());
  }

  @Test
  void update_with_years_taken_by_another_promotion_should_be_rejected_cleanly() {
    Promotion first = create(newPromotion());
    Promotion second = create(newPromotion());

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/promotions/" + second.getId(), HttpMethod.PUT, new HttpEntity<>(first), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void update_keeping_the_same_years_is_allowed() {
    Promotion created = create(newPromotion());

    ResponseEntity<Promotion> response =
        restTemplate.exchange(
            "/promotions/" + created.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(created),
            Promotion.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void update_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/promotions/" + UUID.randomUUID(),
            HttpMethod.PUT,
            new HttpEntity<>(newPromotion()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_promotion() {
    Promotion created = create(newPromotion());

    restTemplate.delete("/promotions/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/promotions/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/promotions/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
