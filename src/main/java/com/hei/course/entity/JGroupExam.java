package com.hei.course.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "group_exam")
public class JGroupExam {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private JGroup group;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exam_id", nullable = false)
  private JExam exam;
}
