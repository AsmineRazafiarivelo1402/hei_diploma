package com.hei.course.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
public class JCourses {

  @Id @GeneratedValue private UUID id;

  @Column(name = "reference", nullable = false, unique = true)
  private String reference;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "credit", nullable = false)
  private int credit;

  @OneToMany(mappedBy = "courses", cascade = CascadeType.ALL, orphanRemoval = false)
  private List<JExam> exams = new ArrayList<>();
}
