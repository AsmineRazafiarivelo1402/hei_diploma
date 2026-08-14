package com.hei.course.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "note_history")
public class JNoteHistory {

  @Id @GeneratedValue private UUID id;

  @Column(name = "old_value", nullable = false)
  private double oldValue;

  @Column(name = "new_value", nullable = false)
  private double newValue;

  @Column(name = "update_at", nullable = false)
  private Instant updateAt;

  @Column(name = "reason")
  private String reason;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "updated_by", nullable = false)
  private JUsers updatedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "note_id", nullable = false)
  private JNote note;
}
