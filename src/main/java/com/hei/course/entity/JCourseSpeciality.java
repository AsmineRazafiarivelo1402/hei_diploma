package com.hei.course.entity;

import com.hei.course.model.SpecialityEnum;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "course_speciality")
public class JCourseSpeciality {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "courses_id", nullable = false)
  private JCourses courses;

  @Enumerated(EnumType.STRING)
  @Column(name = "speciality_enum", nullable = false)
  private SpecialityEnum specialityEnum;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "semester_id", nullable = false)
  private JSemester semester;
}
