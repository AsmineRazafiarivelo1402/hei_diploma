package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.Promotion;
import com.hei.course.service.crud.PromotionService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

  private final PromotionService promotionService;

  @PostMapping
  public ResponseEntity<Promotion> create(@RequestBody Promotion promotion) {

    return ResponseEntity.status(HttpStatus.CREATED).body(promotionService.create(promotion));
  }

  @GetMapping
  public ResponseEntity<List<Promotion>> findAll() {

    return ResponseEntity.ok(promotionService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Promotion> findById(@PathVariable UUID id) {

    return ResponseEntity.ok(promotionService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Promotion> update(@PathVariable UUID id, @RequestBody Promotion promotion) {

    return ResponseEntity.ok(promotionService.update(id, promotion));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {

    promotionService.delete(id);

    return ResponseEntity.noContent().build();
  }
}
