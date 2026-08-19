package com.hei.course.service.transcript;

import com.hei.course.entity.JStudent;
import com.hei.course.service.transcript.TranscriptData.SemesterTranscript;
import com.hei.course.service.transcript.TranscriptData.Status;
import com.hei.course.service.transcript.TranscriptData.Transcript;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.stereotype.Component;

@Component
public class TranscriptPdfWriter {

  public File toPdf(JStudent student, Transcript transcript) {
    try {
      File file = Files.createTempFile("releve-" + student.getReference(), ".pdf").toFile();

      try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(file));
           Document document = new Document(pdfDocument)) {
        document.add(new Paragraph("Relevé de notes"));
        document.add(
                new Paragraph(
                        student.getFirstName() + " " + student.getLastName() + " (" + student.getReference() + ")"));

        for (SemesterTranscript semester : transcript.semesters()) {
          addSemesterSection(document, semester);
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total crédits : " + transcript.totalCredits()).setBold());
        document.add(new Paragraph("Moyenne générale : " + transcript.generalAverage()).setBold());
      }

      return file;
    } catch (IOException e) {
      throw new RuntimeException("Failed to generate transcript PDF", e);
    }
  }

  private void addSemesterSection(Document document, SemesterTranscript semester) {
    document.add(new Paragraph(" "));
    document.add(new Paragraph(semester.semester().name()).setBold());

    Table table = new Table(3);
    table.addHeaderCell("Matière");
    table.addHeaderCell("Crédit");
    table.addHeaderCell("Moyenne");

    for (var courseAverage : semester.courseAverages()) {
      table.addCell(courseAverage.courseTitle());
      table.addCell(String.valueOf(courseAverage.credit()));
      table.addCell(String.valueOf(courseAverage.average()));
    }
    document.add(table);

    document.add(new Paragraph("Crédits du semestre : " + semester.totalCredits()));
    document.add(new Paragraph("Moyenne du semestre : " + semester.average()));
    document.add(new Paragraph("Statut : " + statusLabel(semester.status())));
  }

  private String statusLabel(Status status) {
    return status == Status.COMPLETE ? "Note complète" : "Note provisoire";
  }
}