package com.hei.course.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("STUDENT")
public class JStudent extends JUsers {

  @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = false)
  private List<JNote> noteList = new ArrayList<>();
}
