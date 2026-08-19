package com.hei.course.entity;

import com.hei.course.model.SpecialityEnum;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "group_table")
public class JGroup {

  @Id @GeneratedValue private UUID id;

  @Column(name = "reference", nullable = false)
  private String reference;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "speciality", nullable = false)
  private SpecialityEnum speciality;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "promotion_id", nullable = false)
  private JPromotion promotion;
}
