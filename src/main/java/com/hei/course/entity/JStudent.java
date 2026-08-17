package com.hei.course.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "student")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("STUDENT")
public class JStudent extends JUsers {

  @Builder.Default
  @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = false)
  private List<JNote> noteList = new ArrayList<>();
}
