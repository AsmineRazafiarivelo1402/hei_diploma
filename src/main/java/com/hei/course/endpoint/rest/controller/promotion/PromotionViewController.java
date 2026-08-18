package com.hei.course.endpoint.rest.controller.promotion;

import com.hei.course.repository.JPromotionRepository;
import java.util.Comparator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class PromotionViewController {

  private final JPromotionRepository promotionRepository;

  @GetMapping("/promotions/view")
  public String listPromotions(Model model) {
    var promotions =
        promotionRepository.findAll().stream()
            .sorted(Comparator.comparingInt(p -> p.getStartYear()))
            .toList();

    model.addAttribute("promotions", promotions);

    return "promotion";
  }
}
