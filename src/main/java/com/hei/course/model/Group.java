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
public class Group {
  private UUID id;
  private String reference;
  private SpecialityEnum speciality;
  private Promotion promotion;
}
