package com.hei.course.endpoint.rest.controller.graduate;

import com.hei.course.service.graduate.GraduateExcelWriter;
import com.hei.course.service.graduate.GraduateRankingService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class GraduateController {

  private final GraduateRankingService graduateRankingService;
  private final GraduateExcelWriter graduateExcelWriter;

  /**
   * Downloads the Excel list of graduated students for a given promotion, ranked by descending
   * general average (rank 1 = best). A student is considered graduated only if every course
   * average (S1-S6) is >= 10. Restricted to ADMIN, see SecurityBeansConfig.
   */
  @GetMapping(value = "/diplomas", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  public ResponseEntity<byte[]> downloadGraduateList(@RequestParam UUID promotionId) {
    var rankedGraduates = graduateRankingService.rankGraduates(promotionId);
    var excelBytes = graduateExcelWriter.toExcel(rankedGraduates);

    var contentDisposition =
            ContentDisposition.attachment().filename("diplomes-" + promotionId + ".xlsx").build();

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentType(
                    MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excelBytes);
  }
}