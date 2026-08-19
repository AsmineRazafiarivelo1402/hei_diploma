package com.hei.course.endpoint.rest.controller.promotion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.hei.course.entity.JPromotion;
import com.hei.course.repository.JPromotionRepository;
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
class PromotionViewControllerTest {

  @Mock private JPromotionRepository promotionRepository;

  private PromotionViewController controller;

  @BeforeEach
  void setUp() {
    controller = new PromotionViewController(promotionRepository);
  }

  @Test
  void adds_promotions_sorted_by_start_year_to_model() {
    JPromotion older = promotion(2020);
    JPromotion newer = promotion(2024);
    when(promotionRepository.findAll()).thenReturn(List.of(newer, older));

    Model model = new ExtendedModelMap();
    String viewName = controller.listPromotions(model);

    assertThat(viewName).isEqualTo("promotion");
    assertThat(model.getAttribute("promotions")).isEqualTo(List.of(older, newer));
  }

  private JPromotion promotion(int startYear) {
    JPromotion promotion = new JPromotion();
    promotion.setId(UUID.randomUUID());
    promotion.setStartYear(startYear);
    return promotion;
  }
}
