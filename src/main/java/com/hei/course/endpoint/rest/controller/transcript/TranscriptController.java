package com.hei.course.endpoint.rest.controller.transcript;

import com.hei.course.endpoint.event.EventProducer;
import com.hei.course.endpoint.event.model.TranscriptEmailRequested;
import com.hei.course.security.UserPrincipal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TranscriptController {

  private final EventProducer<TranscriptEmailRequested> eventProducer;

  @PostMapping("/releves/{studentId}/email")
  public ResponseEntity<Void> requestTranscriptByEmail(
      @PathVariable UUID studentId, @AuthenticationPrincipal UserPrincipal principal) {
    if (isStudentRequestingSomeoneElse(studentId, principal)) {
      return ResponseEntity.status(403).build();
    }

    eventProducer.accept(List.of(TranscriptEmailRequested.builder().studentId(studentId).build()));
    return ResponseEntity.accepted().build();
  }

  private boolean isStudentRequestingSomeoneElse(UUID studentId, UserPrincipal principal) {
    boolean isStudent =
        principal.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"));
    return isStudent && !principal.getUser().getId().equals(studentId);
  }
}
