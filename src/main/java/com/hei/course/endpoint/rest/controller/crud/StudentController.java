package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.Student;
import com.hei.course.service.crud.StudentService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/students")
public class StudentController {

  private final StudentService studentService;

  @PostMapping
  public ResponseEntity<Student> create(@RequestBody Student student) {
    return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(student));
  }

  @GetMapping
  public ResponseEntity<List<Student>> findAll() {
    return ResponseEntity.ok(studentService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Student> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(studentService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Student> update(@PathVariable UUID id, @RequestBody Student student) {
    return ResponseEntity.ok(studentService.update(id, student));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    studentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
