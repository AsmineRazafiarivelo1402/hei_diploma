package com.hei.course.entity;

import com.hei.course.model.SpecialityEnum;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "group_table")
public class JGroup {

  @Id @GeneratedValue private UUID id;

  @Column(name = "reference", nullable = false)
  private String reference;

  @Enumerated(EnumType.STRING)
  @Column(name = "speciality", nullable = false)
  private SpecialityEnum speciality;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "promotion_id", nullable = false)
  private JPromotion promotion;
}
