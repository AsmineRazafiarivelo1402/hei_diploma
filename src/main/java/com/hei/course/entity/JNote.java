package com.hei.course.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "note")
public class JNote {

  @Id @GeneratedValue private UUID id;

  @Column(name = "value", nullable = false)
  private double value;

  @Column(name = "date", nullable = false)
  private Instant date;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exam_id", nullable = false)
  private JExam exam;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private JStudent student;

  @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JNoteHistory> noteHistoryList = new ArrayList<>();
}
