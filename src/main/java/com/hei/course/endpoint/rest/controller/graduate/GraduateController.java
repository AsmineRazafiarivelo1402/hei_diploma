package com.hei.course.endpoint.rest.controller.graduate;

import com.hei.course.service.graduate.GraduateEligibilityService;
import com.hei.course.service.graduate.GraduateExcelWriter;
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

  private final GraduateEligibilityService graduateEligibilityService;
  private final GraduateExcelWriter graduateExcelWriter;

  @GetMapping(
      value = "/diplomas",
      produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  public ResponseEntity<byte[]> downloadGraduateList(@RequestParam UUID promotionId) {
    var graduates = graduateEligibilityService.findGraduates(promotionId);
    var excelBytes = graduateExcelWriter.toExcel(graduates);

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
