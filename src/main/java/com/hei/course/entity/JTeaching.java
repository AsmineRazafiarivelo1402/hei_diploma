package com.hei.course.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "teaching")
public class JTeaching {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "courses_id", nullable = false)
  private JCourses courses;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "teacher_id", nullable = false)
  private JTeacher teacher;
}
