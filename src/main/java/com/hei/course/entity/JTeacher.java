package com.hei.course.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teacher")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("TEACHER")
public class JTeacher extends JUsers {

  @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = false)
  private List<JTeaching> teachingList = new ArrayList<>();
}
