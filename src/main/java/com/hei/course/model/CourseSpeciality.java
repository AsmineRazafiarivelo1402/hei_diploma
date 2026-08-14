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
public class CourseSpeciality {
  private UUID id;
  private Courses courses;
  private SpecialityEnum speciality;
  private Semester semester;
}
