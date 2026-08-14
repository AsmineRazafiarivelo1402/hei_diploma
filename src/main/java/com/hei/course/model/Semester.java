package com.hei.course.model;

import java.time.Instant;
import java.util.UUID;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Semester {
  private UUID id;
  private SemesterEnum semestreEnum;
  private Instant startDate;
  private Instant endDate;
}
