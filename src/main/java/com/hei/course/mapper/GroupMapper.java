package com.hei.course.mapper;

import com.hei.course.entity.JGroup;
import com.hei.course.entity.JPromotion;
import com.hei.course.model.Group;
import com.hei.course.model.Promotion;

public class GroupMapper {

  private GroupMapper() {}

  public static Group toModel(JGroup entity) {
    Promotion promotion = null;

    if (entity.getPromotion() != null) {
      promotion =
          Promotion.builder()
              .id(entity.getPromotion().getId())
              .startYear(entity.getPromotion().getStartYear())
              .endYear(entity.getPromotion().getEndYear())
              .build();
    }

    return Group.builder()
        .id(entity.getId())
        .reference(entity.getReference())
        .speciality(entity.getSpeciality())
        .promotion(promotion)
        .build();
  }

  public static JGroup toEntity(Group model, JPromotion promotion) {
    JGroup entity = new JGroup();

    entity.setId(model.getId());
    entity.setReference(model.getReference());
    entity.setSpeciality(model.getSpeciality());
    entity.setPromotion(promotion);

    return entity;
  }
}
