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
public class NoteHistory {
  private UUID id;
  private double oldValue;
  private double newValue;
  private Instant updateAt;
  private String reason;
  private Users updatedBy;
  private Note note;
}
