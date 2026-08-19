package com.hei.course.crud;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JPromotion;
import com.hei.course.model.Group;
import com.hei.course.model.SpecialityEnum;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GroupCrudIT extends AbstractAdminCrudIT {

  private Group newGroup() {
    String suffix = UUID.randomUUID().toString();
    return Group.builder().reference("K-" + suffix).speciality(SpecialityEnum.COMMON).build();
  }

  private Group create(Group payload, UUID promotionId) {
    return restTemplate
        .postForEntity("/groups?promotionId=" + promotionId, payload, Group.class)
        .getBody();
  }

  @Test
  void create_persists_the_group_with_its_promotion() {
    JPromotion promotion = persistPromotion();
    Group payload = newGroup();

    ResponseEntity<Group> response =
        restTemplate.postForEntity(
            "/groups?promotionId=" + promotion.getId(), payload, Group.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Group created = response.getBody();
    assertThat(created).isNotNull();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getReference()).isEqualTo(payload.getReference());
    assertThat(created.getPromotion().getId()).isEqualTo(promotion.getId());
  }

  @Test
  void create_with_unknown_promotion_returns_404() {
    ResponseEntity<String> response =
        restTemplate.postForEntity(
            "/groups?promotionId=" + UUID.randomUUID(), newGroup(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void find_all_returns_created_groups() {
    JPromotion promotion = persistPromotion();
    Group created = create(newGroup(), promotion.getId());

    ResponseEntity<Group[]> response = restTemplate.getForEntity("/groups", Group[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).extracting(Group::getId).contains(created.getId());
  }

  @Test
  void find_by_id_returns_the_group() {
    JPromotion promotion = persistPromotion();
    Group created = create(newGroup(), promotion.getId());

    ResponseEntity<Group> response =
        restTemplate.getForEntity("/groups/" + created.getId(), Group.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getReference()).isEqualTo(created.getReference());
  }

  @Test
  void find_by_id_returns_404_for_unknown_id() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/groups/" + UUID.randomUUID(), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_changes_the_group_fields() {
    JPromotion promotion = persistPromotion();
    Group created = create(newGroup(), promotion.getId());
    JPromotion otherPromotion = persistPromotion();

    Group updatePayload = newGroup();
    updatePayload.setSpeciality(SpecialityEnum.EL);

    ResponseEntity<Group> response =
        restTemplate.exchange(
            "/groups/" + created.getId() + "?promotionId=" + otherPromotion.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updatePayload),
            Group.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getSpeciality()).isEqualTo(SpecialityEnum.EL);
    assertThat(response.getBody().getPromotion().getId()).isEqualTo(otherPromotion.getId());
  }

  @Test
  void update_with_unknown_promotion_returns_404() {
    JPromotion promotion = persistPromotion();
    Group created = create(newGroup(), promotion.getId());

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/groups/" + created.getId() + "?promotionId=" + UUID.randomUUID(),
            HttpMethod.PUT,
            new HttpEntity<>(newGroup()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void update_of_unknown_id_returns_404() {
    JPromotion promotion = persistPromotion();

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/groups/" + UUID.randomUUID() + "?promotionId=" + promotion.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(newGroup()),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_removes_the_group() {
    JPromotion promotion = persistPromotion();
    Group created = create(newGroup(), promotion.getId());

    restTemplate.delete("/groups/" + created.getId());

    ResponseEntity<String> response =
        restTemplate.getForEntity("/groups/" + created.getId(), String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_of_unknown_id_returns_404() {
    ResponseEntity<String> response =
        restTemplate.exchange(
            "/groups/" + UUID.randomUUID(), HttpMethod.DELETE, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
