package com.hei.course.service.transcript;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hei.course.entity.JStudent;
import com.hei.course.file.bucket.BucketComponent;
import com.hei.course.mail.Email;
import com.hei.course.mail.Mailer;
import com.hei.course.repository.JNoteRepository;
import com.hei.course.repository.JStudentRepository;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranscriptServiceTest {

  @Mock private JStudentRepository studentRepository;
  @Mock private JNoteRepository noteRepository;
  @Mock private TranscriptPdfWriter transcriptPdfWriter;
  @Mock private BucketComponent bucketComponent;
  @Mock private Mailer mailer;

  private TranscriptService service;

  @BeforeEach
  void setUp() {
    service =
        new TranscriptService(
            studentRepository, noteRepository, transcriptPdfWriter, bucketComponent, mailer);
  }

  @Test
  void sends_email_with_generated_pdf_and_uploads_it_to_s3() throws Exception {
    UUID studentId = UUID.randomUUID();
    JStudent student = new JStudent();
    student.setId(studentId);
    student.setFirstName("Fenitra");
    student.setLastName("Rakoto");
    student.setEmail("fenitra@example.com");

    File pdfFile = File.createTempFile("releve-test", ".pdf");
    pdfFile.deleteOnExit();

    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
    when(noteRepository.findByStudent_Id(studentId)).thenReturn(List.of());
    when(transcriptPdfWriter.toPdf(student, List.of())).thenReturn(pdfFile);

    service.sendTranscriptByEmail(studentId);

    verify(bucketComponent, times(1)).upload(any(File.class), any(String.class));

    var emailCaptor = org.mockito.ArgumentCaptor.forClass(Email.class);
    verify(mailer, times(1)).accept(emailCaptor.capture());

    Email sentEmail = emailCaptor.getValue();
    assertThat(sentEmail.to().getAddress()).isEqualTo("fenitra@example.com");
    assertThat(sentEmail.attachments()).containsExactly(pdfFile);
  }

  @Test
  void throws_when_student_does_not_exist() {
    UUID studentId = UUID.randomUUID();
    when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.sendTranscriptByEmail(studentId))
        .isInstanceOf(IllegalArgumentException.class);

    verify(mailer, never()).accept(any());
    verify(bucketComponent, never()).upload(any(), any());
  }
}
