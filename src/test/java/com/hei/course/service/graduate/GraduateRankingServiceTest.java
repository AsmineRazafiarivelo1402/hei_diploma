package com.hei.course.service.graduate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.hei.course.entity.JStudent;
import com.hei.course.service.transcript.TranscriptComputationService;
import com.hei.course.service.transcript.TranscriptData.Transcript;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GraduateRankingServiceTest {

  @Mock private GraduateEligibilityService graduateEligibilityService;
  @Mock private TranscriptComputationService transcriptComputationService;

  private GraduateRankingService service;
  private final UUID promotionId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    service = new GraduateRankingService(graduateEligibilityService, transcriptComputationService);
  }

  @Test
  void ranks_graduates_by_descending_general_average() {
    JStudent best = student("Rakoto", "Fenitra");
    JStudent worst = student("Zafy", "Andry");

    when(graduateEligibilityService.findGraduates(promotionId)).thenReturn(List.of(best, worst));
    when(transcriptComputationService.computeFor(best.getId()))
        .thenReturn(transcriptWithAverage("14.50"));
    when(transcriptComputationService.computeFor(worst.getId()))
        .thenReturn(transcriptWithAverage("11.20"));

    List<GraduateRanking> rankings = service.rankGraduates(promotionId);

    assertThat(rankings).hasSize(2);
    assertThat(rankings).extracting(GraduateRanking::rank).containsExactly(1, 2);
    assertThat(rankings.get(0).student()).isEqualTo(best);
    assertThat(rankings.get(0).average()).isEqualByComparingTo("14.50");
    assertThat(rankings.get(1).student()).isEqualTo(worst);
  }

  @Test
  void returns_empty_list_when_no_graduates() {
    when(graduateEligibilityService.findGraduates(promotionId)).thenReturn(List.of());

    List<GraduateRanking> rankings = service.rankGraduates(promotionId);

    assertThat(rankings).isEmpty();
  }

  private Transcript transcriptWithAverage(String average) {
    return new Transcript(List.of(), 0, new BigDecimal(average));
  }

  private JStudent student(String lastName, String firstName) {
    JStudent student = new JStudent();
    student.setId(UUID.randomUUID());
    student.setLastName(lastName);
    student.setFirstName(firstName);
    return student;
  }
}
