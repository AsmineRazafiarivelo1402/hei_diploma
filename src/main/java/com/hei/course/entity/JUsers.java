package com.hei.course.entity;

import com.hei.course.model.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class JUsers {

  @Id @GeneratedValue private UUID id;

  @Column(name = "reference", nullable = false, unique = true)
  private String reference;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "birthdate")
  private Instant birthdate;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "address")
  private String address;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private RoleEnum role;

  @Column(name = "password", nullable = false)
  private String password;
}
