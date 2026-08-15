package com.hei.course.service.graduate;

import com.hei.course.entity.JStudent;
import java.io.ByteArrayOutputStream;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

  private static final DateTimeFormatter BIRTHDATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
  private static final String[] HEADERS = {
    "Référence", "Nom", "Prénom", "Email", "Date de naissance"
  };

  public byte[] toExcel(List<JStudent> graduates) {
    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      XSSFSheet sheet = workbook.createSheet("Diplômés");
      writeHeader(workbook, sheet);
      writeRows(sheet, graduates);
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

  private void writeRows(XSSFSheet sheet, List<JStudent> graduates) {
    int rowIndex = 1;
    for (JStudent student : graduates) {
      Row row = sheet.createRow(rowIndex++);
      row.createCell(0).setCellValue(student.getReference());
      row.createCell(1).setCellValue(student.getLastName());
      row.createCell(2).setCellValue(student.getFirstName());
      row.createCell(3).setCellValue(student.getEmail());
      row.createCell(4)
          .setCellValue(
              student.getBirthdate() == null
                  ? ""
                  : BIRTHDATE_FORMAT.format(student.getBirthdate().atZone(ZoneOffset.UTC)));
    }
  }

  private void autoSizeColumns(XSSFSheet sheet) {
    for (int i = 0; i < HEADERS.length; i++) {
      sheet.autoSizeColumn(i);
    }
  }
}
