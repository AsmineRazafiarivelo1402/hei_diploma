package com.hei.course.entity;

import com.hei.course.model.ExamTypeEnum;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "exam")
public class JExam {

  @Id @GeneratedValue private UUID id;

  @Column(name = "date", nullable = false)
  private Instant date;

  @Column(name = "coefficient", nullable = false, precision = 4, scale = 2)
  private BigDecimal coefficient;

  @Enumerated(EnumType.STRING)
  @Column(name = "exam_type", nullable = false)
  private ExamTypeEnum examType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "courses_id", nullable = false)
  private JCourses courses;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "semester_id", nullable = false)
  private JSemester semester;

  @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = false)
  private List<JNote> noteList = new ArrayList<>();
}
