package com.hei.course.endpoint.rest.controller.graduate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.hei.course.service.graduate.GraduateExcelWriter;
import com.hei.course.service.graduate.GraduateRankingService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GraduateControllerTest {

  @Mock private GraduateRankingService graduateRankingService;
  @Mock private GraduateExcelWriter graduateExcelWriter;

  private GraduateController controller;
  private final UUID promotionId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    controller = new GraduateController(graduateRankingService, graduateExcelWriter);
  }

  @Test
  void downloads_graduate_list_as_excel_attachment() {
    byte[] excelBytes = "excel".getBytes();
    when(graduateRankingService.rankGraduates(promotionId)).thenReturn(List.of());
    when(graduateExcelWriter.toExcel(List.of())).thenReturn(excelBytes);

    ResponseEntity<byte[]> response = controller.downloadGraduateList(promotionId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
        .contains("diplomes-" + promotionId + ".xlsx");
    assertThat(response.getHeaders().getContentType().toString())
        .contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    assertThat(response.getBody()).isEqualTo(excelBytes);
  }
}
