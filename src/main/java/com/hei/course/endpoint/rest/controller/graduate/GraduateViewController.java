package com.hei.course.endpoint.rest.controller.graduate;

import com.hei.course.service.graduate.GraduateRankingService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class GraduateViewController {

    private final GraduateRankingService graduateRankingService;

    @GetMapping("/diplomas/view")
    public String listGraduates(@RequestParam UUID promotionId, Model model) {
        model.addAttribute("promotionId", promotionId);
        model.addAttribute("rankedGraduates", graduateRankingService.rankGraduates(promotionId));
        return "graduates";
    }
}