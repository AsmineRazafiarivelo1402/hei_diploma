package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.endpoint.rest.controller.crud.UserController.CreateAdminRequest;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Users;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserCrudIT extends AbstractAdminCrudIT {

  private CreateAdminRequest newAdminRequest() {
    String suffix = UUID.randomUUID().toString();
    return new CreateAdminRequest(
        "REF-" + suffix,
        "Admin",
        "Two",
        Instant.parse("1990-01-01T00:00:00Z"),
        "admin2-" + suffix + "@hei.test",
        "P@ssw0rd!",
        "Antananarivo",
        "0320000000");
  }

  @Test
  void create_persists_the_admin() {
    CreateAdminRequest payload = newAdminRequest();

    ResponseEntity<Users> response = restTemplate.postForEntity("/users", payload, Users.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Users created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getEmail()).isEqualTo(payload.email());
    assertThat(created.getRole()).isEqualTo(RoleEnum.ADMIN);
  }

  @Test
  void create_with_duplicate_email_should_be_rejected_cleanly_not_with_a_500() {
    CreateAdminRequest first = newAdminRequest();
    restTemplate.postForEntity("/users", first, Users.class);

    CreateAdminRequest duplicate =
        new CreateAdminRequest(
            "REF-" + UUID.randomUUID(),
            "Admin",
            "Three",
            Instant.parse("1990-01-01T00:00:00Z"),
            first.email(),
            "P@ssw0rd!",
            "Antananarivo",
            "0320000000");

    ResponseEntity<String> response = restTemplate.postForEntity("/users", duplicate, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }
}
