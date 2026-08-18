package com.hei.course.controller;

import com.hei.course.model.Courses;
import com.hei.course.service.crud.CourseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

  private final CourseService courseService;

  @PostMapping
  public ResponseEntity<Courses> create(@RequestBody Courses course) {
    return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(course));
  }

  @GetMapping("/bonjour")
  public String bonjour(){
    return "Bonjour à tous";
  }
  @GetMapping
  public ResponseEntity<List<Courses>> findAll() {
    return ResponseEntity.ok(courseService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Courses> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(courseService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Courses> update(@PathVariable UUID id, @RequestBody Courses course) {
    return ResponseEntity.ok(courseService.update(id, course));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    courseService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
