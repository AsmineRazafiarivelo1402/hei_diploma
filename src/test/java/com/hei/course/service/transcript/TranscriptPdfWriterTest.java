package com.hei.course.service.transcript;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JStudent;
import com.hei.course.model.SemesterEnum;
import com.hei.course.service.transcript.TranscriptData.CourseAverage;
import com.hei.course.service.transcript.TranscriptData.SemesterTranscript;
import com.hei.course.service.transcript.TranscriptData.Status;
import com.hei.course.service.transcript.TranscriptData.Transcript;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class TranscriptPdfWriterTest {

  private final TranscriptPdfWriter writer = new TranscriptPdfWriter();

  @Test
  void generates_non_empty_pdf_file_with_student_and_semester_details() throws Exception {
    JStudent student = new JStudent();
    student.setReference("STU-001");
    student.setFirstName("Fenitra");
    student.setLastName("Rakoto");

    SemesterTranscript semester =
        new SemesterTranscript(
            SemesterEnum.S1,
            List.of(new CourseAverage("Java", 3, new BigDecimal("13.20"))),
            3,
            new BigDecimal("13.20"),
            Status.COMPLETE);
    Transcript transcript = new Transcript(List.of(semester), 3, new BigDecimal("13.20"));

    File pdf = writer.toPdf(student, transcript);

    assertThat(pdf).exists();
    assertThat(pdf).isNotEmpty();
    assertThat(pdf.getName()).startsWith("releve-STU-001").endsWith(".pdf");
    pdf.delete();
  }

  @Test
  void generates_pdf_when_transcript_has_no_semesters() {
    JStudent student = new JStudent();
    student.setReference("STU-002");
    student.setFirstName("John");
    student.setLastName("Doe");

    Transcript transcript = new Transcript(List.of(), 0, BigDecimal.ZERO);

    File pdf = writer.toPdf(student, transcript);

    assertThat(pdf).exists();
    assertThat(pdf).isNotEmpty();
    pdf.delete();
  }
}
