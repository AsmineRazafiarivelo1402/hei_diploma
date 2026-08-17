package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.Teaching;
import com.hei.course.service.crud.TeachingService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teachings")
@RequiredArgsConstructor
public class TeachingController {

    private final TeachingService teachingService;

    @PostMapping
    public ResponseEntity<Teaching> create(
            @RequestParam UUID courseId,
            @RequestParam UUID teacherId) {

        Teaching teaching = teachingService.create(courseId, teacherId);

        return ResponseEntity.status(HttpStatus.CREATED).body(teaching);
    }

    @GetMapping
    public ResponseEntity<List<Teaching>> findAll() {

        return ResponseEntity.ok(teachingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teaching> findById(@PathVariable UUID id) {

        return ResponseEntity.ok(teachingService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teaching> update(
            @PathVariable UUID id,
            @RequestParam UUID courseId,
            @RequestParam UUID teacherId) {

        return ResponseEntity.ok(
                teachingService.update(id, courseId, teacherId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        teachingService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Teaching>> findByTeacher(
            @PathVariable UUID teacherId) {

        return ResponseEntity.ok(
                teachingService.findByTeacher(teacherId));
    }
}