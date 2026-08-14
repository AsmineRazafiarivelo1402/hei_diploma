package com.hei.course.model;

import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
public class Student extends Users {
  private List<Note> noteList;
}
