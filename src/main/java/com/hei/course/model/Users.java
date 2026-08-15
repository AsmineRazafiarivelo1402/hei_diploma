package com.hei.course.model;

import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class Users {
  private UUID id;
  private String reference;
  private String firstName;
  private String lastName;
  private Instant birthdate;
  private String email;
  private String address;
  private String phoneNumber;
  private RoleEnum role;
  private String password;
}
