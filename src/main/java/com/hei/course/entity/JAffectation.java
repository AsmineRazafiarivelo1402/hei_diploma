package com.hei.course.entity;

import com.hei.course.model.StatusAffectationEnum;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "affectation")
public class JAffectation {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private JStudent student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private JGroup group;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "semester_id", nullable = false)
  private JSemester semester;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private StatusAffectationEnum status;
}
