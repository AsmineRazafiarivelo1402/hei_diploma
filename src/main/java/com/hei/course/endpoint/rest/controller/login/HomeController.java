package com.hei.course.endpoint.rest.controller.login;

import com.hei.course.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/home")
  public String home(@AuthenticationPrincipal UserPrincipal principal) {
    boolean isStudent =
        principal.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"));
    return isStudent ? "redirect:/mon-releve" : "redirect:/promotions/view";
  }
}
