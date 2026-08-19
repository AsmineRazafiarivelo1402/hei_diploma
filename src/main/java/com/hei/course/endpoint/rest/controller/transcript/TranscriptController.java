package com.hei.course.endpoint.rest.controller.transcript;

import com.hei.course.endpoint.event.EventProducer;
import com.hei.course.endpoint.event.model.TranscriptEmailRequested;
import com.hei.course.entity.JStudent;
import com.hei.course.repository.JStudentRepository;
import com.hei.course.security.UserPrincipal;
import com.hei.course.service.transcript.TranscriptComputationService;
import com.hei.course.service.transcript.TranscriptPdfWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TranscriptController {

  private final EventProducer<TranscriptEmailRequested> eventProducer;
  private final JStudentRepository studentRepository;
  private final TranscriptComputationService transcriptComputationService;
  private final TranscriptPdfWriter transcriptPdfWriter;

  @PostMapping("/releves/{studentId}/email")
  public ResponseEntity<Void> requestTranscriptByEmail(
      @PathVariable UUID studentId, @AuthenticationPrincipal UserPrincipal principal) {
    if (isStudentRequestingSomeoneElse(studentId, principal)) {
      return ResponseEntity.status(403).build();
    }

    eventProducer.accept(List.of(TranscriptEmailRequested.builder().studentId(studentId).build()));
    return ResponseEntity.accepted().build();
  }

  @GetMapping("/releves/{studentId}")
  public ResponseEntity<byte[]> getTranscript(
      @PathVariable UUID studentId, @AuthenticationPrincipal UserPrincipal principal) {
    if (isStudentRequestingSomeoneElse(studentId, principal)) {
      return ResponseEntity.status(403).build();
    }

    JStudent student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown student: " + studentId));

    var transcript = transcriptComputationService.computeFor(studentId);
    File pdfFile = transcriptPdfWriter.toPdf(student, transcript);

    byte[] pdfBytes = readFile(pdfFile);

    var contentDisposition =
        ContentDisposition.attachment()
            .filename("releve-" + student.getReference() + ".pdf")
            .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdfBytes);
  }

  private byte[] readFile(File file) {
    try {
      return Files.readAllBytes(file.toPath());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read generated transcript PDF", e);
    }
  }

  private boolean isStudentRequestingSomeoneElse(UUID studentId, UserPrincipal principal) {
    boolean isStudent =
        principal.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"));
    return isStudent && !principal.getUser().getId().equals(studentId);
  }
}
