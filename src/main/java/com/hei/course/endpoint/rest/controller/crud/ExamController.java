package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.Exam;
import com.hei.course.service.crud.ExamService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {

  private final ExamService examService;

  @PostMapping
  public ResponseEntity<Exam> create(
      @RequestParam UUID courseId, @RequestParam UUID semesterId, @RequestBody Exam exam) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(examService.create(exam, courseId, semesterId));
  }

  @GetMapping
  public ResponseEntity<List<Exam>> findAll() {
    return ResponseEntity.ok(examService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Exam> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(examService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Exam> update(
      @PathVariable UUID id,
      @RequestParam UUID courseId,
      @RequestParam UUID semesterId,
      @RequestBody Exam exam) {

    return ResponseEntity.ok(examService.update(id, exam, courseId, semesterId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    examService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
