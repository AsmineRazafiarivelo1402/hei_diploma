package com.hei.course.service.transcript;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JExam;
import com.hei.course.entity.JNote;
import com.hei.course.entity.JStudent;
import com.hei.course.model.SemesterEnum;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TranscriptPdfWriter {

  public File toPdf(JStudent student, List<JNote> notes) {
    try {
      File file = Files.createTempFile("releve-" + student.getReference(), ".pdf").toFile();

      try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(file));
          Document document = new Document(pdfDocument)) {
        document.add(new Paragraph("Relevé de notes"));
        document.add(
            new Paragraph(
                student.getFirstName()
                    + " "
                    + student.getLastName()
                    + " ("
                    + student.getReference()
                    + ")"));

        Map<SemesterEnum, Map<JCourses, List<JNote>>> notesBySemesterAndCourse =
            groupBySemesterAndCourse(notes);

        for (var semesterEntry : new TreeMap<>(notesBySemesterAndCourse).entrySet()) {
          document.add(new Paragraph(semesterEntry.getKey().name()).setBold());

          Table table = new Table(2);
          table.addHeaderCell("Matière");
          table.addHeaderCell("Moyenne");

          for (var courseEntry : semesterEntry.getValue().entrySet()) {
            table.addCell(courseEntry.getKey().getTitle());
            table.addCell(String.valueOf(weightedAverage(courseEntry.getValue())));
          }
          document.add(table);
        }
      }

      return file;
    } catch (IOException e) {
      throw new RuntimeException("Failed to generate transcript PDF", e);
    }
  }

  private Map<SemesterEnum, Map<JCourses, List<JNote>>> groupBySemesterAndCourse(
      List<JNote> notes) {
    return notes.stream()
        .collect(
            Collectors.groupingBy(
                note -> note.getExam().getSemester().getSemesterEnum(),
                Collectors.groupingBy(note -> note.getExam().getCourses())));
  }

  private BigDecimal weightedAverage(List<JNote> notes) {
    BigDecimal weightedSum = BigDecimal.ZERO;
    BigDecimal totalCoefficient = BigDecimal.ZERO;

    for (JNote note : notes) {
      JExam exam = note.getExam();
      weightedSum =
          weightedSum.add(exam.getCoefficient().multiply(BigDecimal.valueOf(note.getValue())));
      totalCoefficient = totalCoefficient.add(exam.getCoefficient());
    }

    return totalCoefficient.signum() == 0
        ? BigDecimal.ZERO
        : weightedSum.divide(totalCoefficient, 2, RoundingMode.HALF_UP);
  }
}
