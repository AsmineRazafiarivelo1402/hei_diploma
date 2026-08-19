package com.hei.course.service.graduate;

import java.io.ByteArrayOutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class GraduateExcelWriter {

  private static final String[] HEADERS = {
    "Rang", "Référence", "Nom", "Prénom", "Email", "Moyenne"
  };

  /** Expects rankings already sorted by rank ascending (rank 1 first). */
  public byte[] toExcel(List<GraduateRanking> rankedGraduates) {
    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      XSSFSheet sheet = workbook.createSheet("Diplômés");
      writeHeader(workbook, sheet);
      writeRows(sheet, rankedGraduates);
      autoSizeColumns(sheet);

      workbook.write(out);
      return out.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate graduate list Excel file", e);
    }
  }

  private void writeHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
    Row headerRow = sheet.createRow(0);
    CellStyle headerStyle = workbook.createCellStyle();
    Font boldFont = workbook.createFont();
    boldFont.setBold(true);
    headerStyle.setFont(boldFont);

    for (int i = 0; i < HEADERS.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(HEADERS[i]);
      cell.setCellStyle(headerStyle);
    }
  }

  private void writeRows(XSSFSheet sheet, List<GraduateRanking> rankedGraduates) {
    int rowIndex = 1;
    for (GraduateRanking ranking : rankedGraduates) {
      var student = ranking.student();
      Row row = sheet.createRow(rowIndex++);
      row.createCell(0).setCellValue(ranking.rank());
      row.createCell(1).setCellValue(student.getReference());
      row.createCell(2).setCellValue(student.getLastName());
      row.createCell(3).setCellValue(student.getFirstName());
      row.createCell(4).setCellValue(student.getEmail());
      row.createCell(5).setCellValue(ranking.average().doubleValue());
    }
  }

  private void autoSizeColumns(XSSFSheet sheet) {
    for (int i = 0; i < HEADERS.length; i++) {
      sheet.autoSizeColumn(i);
    }
  }
}
