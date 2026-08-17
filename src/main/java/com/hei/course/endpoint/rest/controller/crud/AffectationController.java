package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.Affectation;
import com.hei.course.service.crud.AffectationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/affectations")
@RequiredArgsConstructor
public class AffectationController {

  private final AffectationService affectationService;

  @PostMapping
  public ResponseEntity<Affectation> create(
      @RequestParam UUID studentId,
      @RequestParam UUID groupId,
      @RequestParam UUID semesterId,
      @RequestBody Affectation affectation) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(affectationService.create(affectation, studentId, groupId, semesterId));
  }

  @GetMapping
  public ResponseEntity<List<Affectation>> findAll() {

    return ResponseEntity.ok(affectationService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Affectation> findById(@PathVariable UUID id) {

    return ResponseEntity.ok(affectationService.findById(id));
  }

  @GetMapping("/student/{studentId}")
  public ResponseEntity<List<Affectation>> findByStudent(@PathVariable UUID studentId) {

    return ResponseEntity.ok(affectationService.findByStudent(studentId));
  }

  @GetMapping("/promotion/{promotionId}")
  public ResponseEntity<List<Affectation>> findByPromotion(@PathVariable UUID promotionId) {

    return ResponseEntity.ok(affectationService.findByPromotion(promotionId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Affectation> update(
      @PathVariable UUID id,
      @RequestParam UUID studentId,
      @RequestParam UUID groupId,
      @RequestParam UUID semesterId,
      @RequestBody Affectation affectation) {

    return ResponseEntity.ok(
        affectationService.update(id, affectation, studentId, groupId, semesterId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {

    affectationService.delete(id);

    return ResponseEntity.noContent().build();
  }
}
