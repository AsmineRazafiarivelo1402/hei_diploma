package com.hei.course.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "promotion")
public class JPromotion {

  @Id @GeneratedValue private UUID id;

  @Column(name = "start_year", nullable = false)
  private int startYear;

  @Column(name = "end_year", nullable = false)
  private int endYear;

  @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = false)
  private List<JGroup> groupList = new ArrayList<>();
}
