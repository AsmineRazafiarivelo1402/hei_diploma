package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.Note;
import com.hei.course.model.Users;
import com.hei.course.security.UserPrincipal;
import com.hei.course.service.crud.NoteService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

  private final NoteService noteService;

  @PostMapping
  public ResponseEntity<Note> create(
      @RequestParam UUID examId,
      @RequestParam UUID studentId,
      @RequestBody Note note,
      Authentication authentication) {

    Users currentUser = getCurrentUser(authentication);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(noteService.create(note, examId, studentId, currentUser));
  }

  @GetMapping
  public ResponseEntity<List<Note>> findAll(Authentication authentication) {

    Users currentUser = getCurrentUser(authentication);

    return ResponseEntity.ok(noteService.findAll(currentUser));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Note> findById(@PathVariable UUID id, Authentication authentication) {

    Users currentUser = getCurrentUser(authentication);

    return ResponseEntity.ok(noteService.findById(id, currentUser));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Note> update(
      @PathVariable UUID id,
      @RequestParam UUID examId,
      @RequestParam UUID studentId,
      @RequestBody Note note,
      Authentication authentication) {

    Users currentUser = getCurrentUser(authentication);

    return ResponseEntity.ok(noteService.update(id, note, examId, studentId, currentUser));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {

    Users currentUser = getCurrentUser(authentication);

    noteService.delete(id, currentUser);

    return ResponseEntity.noContent().build();
  }

  private Users getCurrentUser(Authentication authentication) {

    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

    return principal.getUser();
  }
}
