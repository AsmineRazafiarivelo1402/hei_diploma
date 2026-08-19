package com.hei.course.endpoint.rest.controller.transcript;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hei.course.endpoint.event.EventProducer;
import com.hei.course.endpoint.event.model.TranscriptEmailRequested;
import com.hei.course.entity.JStudent;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Student;
import com.hei.course.repository.JStudentRepository;
import com.hei.course.security.UserPrincipal;
import com.hei.course.service.transcript.TranscriptComputationService;
import com.hei.course.service.transcript.TranscriptData.Transcript;
import com.hei.course.service.transcript.TranscriptPdfWriter;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TranscriptControllerTest {

  @Mock private EventProducer<TranscriptEmailRequested> eventProducer;
  @Mock private JStudentRepository studentRepository;
  @Mock private TranscriptComputationService transcriptComputationService;
  @Mock private TranscriptPdfWriter transcriptPdfWriter;

  private TranscriptController controller;
  private final UUID studentId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    controller =
        new TranscriptController(
            eventProducer, studentRepository, transcriptComputationService, transcriptPdfWriter);
  }

  @Test
  void student_requesting_own_transcript_by_email_is_accepted() {
    UserPrincipal principal = principal(RoleEnum.STUDENT, studentId);

    ResponseEntity<Void> response = controller.requestTranscriptByEmail(studentId, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    ArgumentCaptor<List<TranscriptEmailRequested>> captor = ArgumentCaptor.forClass(List.class);
    verify(eventProducer, times(1)).accept(captor.capture());
    assertThat(captor.getValue())
        .extracting(TranscriptEmailRequested::getStudentId)
        .containsExactly(studentId);
  }

  @Test
  void student_requesting_someone_else_transcript_by_email_is_forbidden() {
    UserPrincipal principal = principal(RoleEnum.STUDENT, studentId);

    ResponseEntity<Void> response =
        controller.requestTranscriptByEmail(UUID.randomUUID(), principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    verify(eventProducer, never()).accept(any());
  }

  @Test
  void non_student_requesting_transcript_by_email_is_accepted() {
    UserPrincipal principal = principal(RoleEnum.TEACHER, UUID.randomUUID());

    ResponseEntity<Void> response = controller.requestTranscriptByEmail(studentId, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void student_downloading_own_transcript_gets_pdf() throws Exception {
    UserPrincipal principal = principal(RoleEnum.STUDENT, studentId);
    JStudent student = new JStudent();
    student.setId(studentId);
    student.setReference("STU-001");

    File pdf = File.createTempFile("releve", ".pdf");
    Files.write(pdf.toPath(), "%PDF-1.4".getBytes(StandardCharsets.UTF_8));
    pdf.deleteOnExit();

    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
    when(transcriptComputationService.computeFor(studentId))
        .thenReturn(new Transcript(List.of(), 0, BigDecimal.ZERO));
    when(transcriptPdfWriter.toPdf(student, new Transcript(List.of(), 0, BigDecimal.ZERO)))
        .thenReturn(pdf);

    ResponseEntity<byte[]> response = controller.getTranscript(studentId, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
    assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
        .contains("releve-STU-001.pdf");
    assertThat(response.getBody()).isEqualTo("%PDF-1.4".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void student_downloading_someone_else_transcript_is_forbidden() {
    UserPrincipal principal = principal(RoleEnum.STUDENT, studentId);

    ResponseEntity<byte[]> response = controller.getTranscript(UUID.randomUUID(), principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void downloading_transcript_of_unknown_student_throws() {
    UserPrincipal principal = principal(RoleEnum.TEACHER, UUID.randomUUID());
    when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> controller.getTranscript(studentId, principal))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private UserPrincipal principal(RoleEnum role, UUID id) {
    Student user = new Student();
    user.setId(id);
    user.setRole(role);
    return new UserPrincipal(user);
  }
}
