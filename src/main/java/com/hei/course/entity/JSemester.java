package com.hei.course.entity;

import com.hei.course.model.SemesterEnum;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "semester")
public class JSemester {

  @Id @GeneratedValue private UUID id;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "semester_enum", nullable = false)
  private SemesterEnum semesterEnum;

  @Column(name = "date_debut", nullable = false)
  private Instant startDate;

  @Column(name = "date_fin", nullable = false)
  private Instant endDate;
}
