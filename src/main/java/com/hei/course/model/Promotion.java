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
public class Promotion {
  private UUID id;
  private int startYear;
  private int endYear;
  private List<Group> groupList;
}
