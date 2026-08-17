package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.NoteHistory;
import com.hei.course.model.Users;
import com.hei.course.security.UserPrincipal;
import com.hei.course.service.crud.NoteHistoryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/note-histories")
@RequiredArgsConstructor
public class NoteHistoryController {

    private final NoteHistoryService noteHistoryService;

    @PostMapping
    public ResponseEntity<NoteHistory> create(
            @RequestParam UUID noteId,
            @RequestBody NoteHistory noteHistory,
            Authentication authentication) {

        Users currentUser = getCurrentUser(authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        noteHistoryService.create(
                                noteHistory,
                                noteId,
                                currentUser));
    }

    @GetMapping
    public ResponseEntity<List<NoteHistory>> findAll(
            Authentication authentication) {

        Users currentUser = getCurrentUser(authentication);

        return ResponseEntity.ok(
                noteHistoryService.findAll(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteHistory> findById(
            @PathVariable UUID id,
            Authentication authentication) {

        Users currentUser = getCurrentUser(authentication);

        return ResponseEntity.ok(
                noteHistoryService.findById(id, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteHistory> update(
            @PathVariable UUID id,
            @RequestParam UUID noteId,
            @RequestBody NoteHistory noteHistory,
            Authentication authentication) {

        Users currentUser = getCurrentUser(authentication);

        return ResponseEntity.ok(
                noteHistoryService.update(
                        id,
                        noteHistory,
                        noteId,
                        currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication authentication) {

        Users currentUser = getCurrentUser(authentication);

        noteHistoryService.delete(id, currentUser);

        return ResponseEntity.noContent().build();
    }

    private Users getCurrentUser(Authentication authentication) {

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        return principal.getUser();
    }
}