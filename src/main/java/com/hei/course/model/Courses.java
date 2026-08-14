package com.hei.course.model;

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
public class Courses {
  private UUID id;
  private String reference;
  private String title;
  private int credit;
  private List<Exam> exams;
}
