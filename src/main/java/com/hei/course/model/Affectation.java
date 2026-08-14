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
public class Affectation {
  private UUID id;
  private Student student;
  private Group group;
  private Semester semestre;
  private StatusAffectationEnum status;
}
