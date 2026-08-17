package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.GroupExam;
import com.hei.course.service.crud.GroupExamService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group-exams")
@RequiredArgsConstructor
public class GroupExamController {

  private final GroupExamService groupExamService;

  @PostMapping
  public ResponseEntity<GroupExam> create(
      @RequestParam UUID groupId, @RequestParam UUID examId, @RequestBody GroupExam groupExam) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(groupExamService.create(groupExam, groupId, examId));
  }

  @GetMapping
  public ResponseEntity<List<GroupExam>> findAll() {

    return ResponseEntity.ok(groupExamService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<GroupExam> findById(@PathVariable UUID id) {

    return ResponseEntity.ok(groupExamService.findById(id));
  }

  @GetMapping("/group/{groupId}")
  public ResponseEntity<List<GroupExam>> findByGroup(@PathVariable UUID groupId) {

    return ResponseEntity.ok(groupExamService.findByGroup(groupId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<GroupExam> update(
      @PathVariable UUID id,
      @RequestParam UUID groupId,
      @RequestParam UUID examId,
      @RequestBody GroupExam groupExam) {

    return ResponseEntity.ok(groupExamService.update(id, groupExam, groupId, examId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {

    groupExamService.delete(id);

    return ResponseEntity.noContent().build();
  }
}
