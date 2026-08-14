package com.hei.course.model;

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
public class Note {
  private UUID id;
  private double value;
  private Instant date;
  private Exam exam;
  private Student student;
  private List<NoteHistory> noteHistoryList;
}
