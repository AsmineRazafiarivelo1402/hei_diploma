package com.hei.course.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Admin.class, name = "ADMIN"),
  @JsonSubTypes.Type(value = Teacher.class, name = "TEACHER"),
  @JsonSubTypes.Type(value = Student.class, name = "STUDENT")
})
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
