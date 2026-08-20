package com.hei.course.endpoint.rest.controller.transcript;

import com.hei.course.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TranscriptViewController {

  @GetMapping("/mon-releve")
  public String myTranscript(@AuthenticationPrincipal UserPrincipal principal, Model model) {
    var student = principal.getUser();
    model.addAttribute("studentId", student.getId());
    model.addAttribute("firstName", student.getFirstName());
    return "mon-releve";
  }
}
