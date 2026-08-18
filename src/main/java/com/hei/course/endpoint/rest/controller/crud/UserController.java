package com.hei.course.endpoint.rest.controller.crud;

import com.hei.course.model.Users;
import com.hei.course.service.event.UserService;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<Users> createAdmin(@RequestBody CreateAdminRequest request) {

    Users admin =
        userService.createAdmin(
            request.reference(),
            request.firstName(),
            request.lastName(),
            request.birthdate(),
            request.email(),
            request.password(),
            request.address(),
            request.phoneNumber());

    return ResponseEntity.status(HttpStatus.CREATED).body(admin);
  }

  public record CreateAdminRequest(
      String reference,
      String firstName,
      String lastName,
      Instant birthdate,
      String email,
      String password,
      String address,
      String phoneNumber) {}
}
