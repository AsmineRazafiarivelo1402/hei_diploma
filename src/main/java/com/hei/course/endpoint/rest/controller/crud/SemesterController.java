package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.Semester;
import com.hei.course.service.crud.SemesterService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/semesters")
@RequiredArgsConstructor
public class SemesterController {

  private final SemesterService semesterService;

  @PostMapping
  public ResponseEntity<Semester> create(@RequestBody Semester semester) {

    return ResponseEntity.status(HttpStatus.CREATED).body(semesterService.create(semester));
  }

  @GetMapping
  public ResponseEntity<List<Semester>> findAll() {

    return ResponseEntity.ok(semesterService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Semester> findById(@PathVariable UUID id) {

    return ResponseEntity.ok(semesterService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Semester> update(@PathVariable UUID id, @RequestBody Semester semester) {

    return ResponseEntity.ok(semesterService.update(id, semester));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {

    semesterService.delete(id);

    return ResponseEntity.noContent().build();
  }
}
