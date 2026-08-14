package com.hei.course.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("ADMIN")
public class JAdmin extends JUsers {

  public JAdmin() {
    super();
  }
}
