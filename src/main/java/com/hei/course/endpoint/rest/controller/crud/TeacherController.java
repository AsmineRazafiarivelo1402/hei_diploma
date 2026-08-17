package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.Teacher;
import com.hei.course.service.crud.TeacherService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

  private final TeacherService teacherService;

  @PostMapping
  public ResponseEntity<Teacher> create(@RequestBody Teacher teacher) {
    return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.create(teacher));
  }

  @GetMapping
  public ResponseEntity<List<Teacher>> findAll() {
    return ResponseEntity.ok(teacherService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Teacher> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(teacherService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Teacher> update(@PathVariable UUID id, @RequestBody Teacher teacher) {

    return ResponseEntity.ok(teacherService.update(id, teacher));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    teacherService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
