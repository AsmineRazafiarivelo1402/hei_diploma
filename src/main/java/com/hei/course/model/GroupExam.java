package com.hei.course.model;

import java.util.UUID;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class GroupExam {
  private UUID id;
  private Group group;
  private Exam exam;
}
