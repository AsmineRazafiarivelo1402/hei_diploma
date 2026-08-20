package com.hei.course.endpoint.rest.controller.graduate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.hei.course.service.graduate.GraduateRankingService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class GraduateViewControllerTest {

  @Mock private GraduateRankingService graduateRankingService;

  private GraduateViewController controller;
  private final UUID promotionId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    controller = new GraduateViewController(graduateRankingService);
  }

  @Test
  void adds_promotion_and_ranked_graduates_to_model() {
    List<com.hei.course.service.graduate.GraduateRanking> rankings = List.of();
    when(graduateRankingService.rankGraduates(promotionId)).thenReturn(rankings);

    Model model = new ExtendedModelMap();
    String viewName = controller.listGraduates(promotionId, model);

    assertThat(viewName).isEqualTo("graduate");
    assertThat(model.getAttribute("promotionId")).isEqualTo(promotionId);
    assertThat(model.getAttribute("rankedGraduates")).isEqualTo(rankings);
  }
}
