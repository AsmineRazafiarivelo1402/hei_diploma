package com.hei.course.service.transcript;

import com.hei.course.entity.JStudent;
import com.hei.course.file.bucket.BucketComponent;
import com.hei.course.mail.Email;
import com.hei.course.mail.Mailer;
import com.hei.course.repository.JNoteRepository;
import com.hei.course.repository.JStudentRepository;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TranscriptService {

  private final JStudentRepository studentRepository;
  private final JNoteRepository noteRepository;
  private final TranscriptPdfWriter transcriptPdfWriter;
  private final BucketComponent bucketComponent;
  private final Mailer mailer;

  public void sendTranscriptByEmail(UUID studentId) {
    JStudent student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown student: " + studentId));

    var notes = noteRepository.findByStudent_Id(studentId);
    var pdfFile = transcriptPdfWriter.toPdf(student, notes);

    var bucketKey = "transcripts/" + studentId + "/" + UUID.randomUUID() + ".pdf";
    bucketComponent.upload(pdfFile, bucketKey);

    mailer.accept(toEmail(student, pdfFile));
  }

  private Email toEmail(JStudent student, java.io.File pdfFile) {
    try {
      var to = new InternetAddress(student.getEmail());
      return new Email(
          to,
          List.of(),
          List.of(),
          "Votre relevé de notes",
          "Bonjour "
              + student.getFirstName()
              + ", veuillez trouver votre relevé de notes ci-joint.",
          List.of(pdfFile));
    } catch (AddressException e) {
      throw new RuntimeException("Invalid student email: " + student.getEmail(), e);
    }
  }
}
