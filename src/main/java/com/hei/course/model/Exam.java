package com.hei.course.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Exam {
  private UUID id;
  private Instant date;
  private BigDecimal coefficient;
  private ExamTypeEnum examType;
  private Courses courses;
  private Semester semester;
  private List<Note> noteList;
}
