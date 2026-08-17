package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.Group;
import com.hei.course.service.crud.GroupService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

  private final GroupService groupService;

  @PostMapping
  public ResponseEntity<Group> create(@RequestParam UUID promotionId, @RequestBody Group group) {

    return ResponseEntity.status(HttpStatus.CREATED).body(groupService.create(group, promotionId));
  }

  @GetMapping
  public ResponseEntity<List<Group>> findAll() {
    return ResponseEntity.ok(groupService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Group> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(groupService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Group> update(
      @PathVariable UUID id, @RequestParam UUID promotionId, @RequestBody Group group) {

    return ResponseEntity.ok(groupService.update(id, group, promotionId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    groupService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
