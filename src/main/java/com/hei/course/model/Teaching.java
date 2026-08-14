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
public class Teaching {
  private UUID id;
  private Courses courses;
  private Teacher teacher;
}
