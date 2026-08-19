package com.hei.course.service.graduate;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.entity.JStudent;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class GraduateExcelWriterTest {

  private final GraduateExcelWriter writer = new GraduateExcelWriter();

  @Test
  void writes_headers_and_ranked_students_in_excel_workbook() throws Exception {
    JStudent student = new JStudent();
    student.setReference("STU-001");
    student.setLastName("Rakoto");
    student.setFirstName("Fenitra");
    student.setEmail("fenitra@example.com");

    List<GraduateRanking> rankings =
        List.of(new GraduateRanking(1, student, new BigDecimal("14.50")));

    byte[] excelBytes = writer.toExcel(rankings);

    assertThat(excelBytes).isNotEmpty();
    assertThat(new String(excelBytes, 0, 2)).isEqualTo("PK");

    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
      Sheet sheet = workbook.getSheet("Diplômés");
      assertThat(sheet).isNotNull();

      Row header = sheet.getRow(0);
      assertThat(header.getCell(0).getStringCellValue()).isEqualTo("Rang");
      assertThat(header.getCell(1).getStringCellValue()).isEqualTo("Référence");
      assertThat(header.getCell(2).getStringCellValue()).isEqualTo("Nom");
      assertThat(header.getCell(3).getStringCellValue()).isEqualTo("Prénom");
      assertThat(header.getCell(4).getStringCellValue()).isEqualTo("Email");
      assertThat(header.getCell(5).getStringCellValue()).isEqualTo("Moyenne");

      Row row = sheet.getRow(1);
      assertThat(row.getCell(0).getNumericCellValue()).isEqualTo(1.0);
      assertThat(row.getCell(1).getStringCellValue()).isEqualTo("STU-001");
      assertThat(row.getCell(2).getStringCellValue()).isEqualTo("Rakoto");
      assertThat(row.getCell(3).getStringCellValue()).isEqualTo("Fenitra");
      assertThat(row.getCell(4).getStringCellValue()).isEqualTo("fenitra@example.com");
      assertThat(row.getCell(5).getNumericCellValue()).isEqualTo(14.50);
    }
  }

  @Test
  void generates_workbook_with_only_header_when_no_rankings() throws Exception {
    byte[] excelBytes = writer.toExcel(List.of());

    assertThat(excelBytes).isNotEmpty();
    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
      Sheet sheet = workbook.getSheet("Diplômés");
      assertThat(sheet).isNotNull();
      assertThat(sheet.getRow(1)).isNull();
    }
  }

  @Test
  void writes_multiple_rankings_in_rank_order() throws Exception {
    JStudent student1 = new JStudent();
    student1.setReference("STU-001");
    student1.setLastName("Rakoto");
    student1.setFirstName("Fenitra");
    student1.setEmail("fenitra@example.com");

    JStudent student2 = new JStudent();
    student2.setReference("STU-002");
    student2.setLastName("Zafy");
    student2.setFirstName("Andry");
    student2.setEmail("andry@example.com");

    byte[] excelBytes =
        writer.toExcel(
            List.of(
                new GraduateRanking(1, student1, new BigDecimal("14.50")),
                new GraduateRanking(2, student2, new BigDecimal("11.20"))));

    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
      Sheet sheet = workbook.getSheet("Diplômés");
      assertThat(sheet.getRow(1).getCell(0).getNumericCellValue()).isEqualTo(1.0);
      assertThat(sheet.getRow(1).getCell(1).getStringCellValue()).isEqualTo("STU-001");
      assertThat(sheet.getRow(2).getCell(0).getNumericCellValue()).isEqualTo(2.0);
      assertThat(sheet.getRow(2).getCell(1).getStringCellValue()).isEqualTo("STU-002");
    }
  }

  @Test
  void header_cells_are_bold() throws Exception {
    byte[] excelBytes = writer.toExcel(List.of());

    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
      Sheet sheet = workbook.getSheet("Diplômés");
      Cell headerCell = sheet.getRow(0).getCell(0);
      assertThat(workbook.getFontAt(headerCell.getCellStyle().getFontIndex()).getBold()).isTrue();
    }
  }

  @Test
  void ignores_unused_uuid_when_building_ranking() {
    JStudent student = new JStudent();
    student.setId(UUID.randomUUID());
    GraduateRanking ranking = new GraduateRanking(1, student, BigDecimal.TEN);
    assertThat(ranking.rank()).isEqualTo(1);
    assertThat(ranking.average()).isEqualByComparingTo("10");
  }
}
