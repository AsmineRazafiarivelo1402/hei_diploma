package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.CourseSpeciality;
import com.hei.course.service.crud.CourseSpecialityService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course-specialities")
@RequiredArgsConstructor
public class CourseSpecialityController {

  private final CourseSpecialityService courseSpecialityService;

  @PostMapping
  public ResponseEntity<CourseSpeciality> create(
      @RequestParam UUID coursesId,
      @RequestParam UUID semesterId,
      @RequestBody CourseSpeciality courseSpeciality) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(courseSpecialityService.create(courseSpeciality, coursesId, semesterId));
  }

  @GetMapping
  public ResponseEntity<List<CourseSpeciality>> findAll() {

    return ResponseEntity.ok(courseSpecialityService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CourseSpeciality> findById(@PathVariable UUID id) {

    return ResponseEntity.ok(courseSpecialityService.findById(id));
  }

  @GetMapping("/course/{coursesId}")
  public ResponseEntity<List<CourseSpeciality>> findByCourse(@PathVariable UUID coursesId) {

    return ResponseEntity.ok(courseSpecialityService.findByCourse(coursesId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CourseSpeciality> update(
      @PathVariable UUID id,
      @RequestParam UUID coursesId,
      @RequestParam UUID semesterId,
      @RequestBody CourseSpeciality courseSpeciality) {

    return ResponseEntity.ok(
        courseSpecialityService.update(id, courseSpeciality, coursesId, semesterId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {

    courseSpecialityService.delete(id);

    return ResponseEntity.noContent().build();
  }
}
