package com.hei.course.mapper;

import com.hei.course.entity.JPromotion;
import com.hei.course.model.Promotion;

public class PromotionMapper {

  private PromotionMapper() {}

  public static Promotion toModel(JPromotion entity) {
    return Promotion.builder()
        .id(entity.getId())
        .startYear(entity.getStartYear())
        .endYear(entity.getEndYear())
        .build();
  }

  public static JPromotion toEntity(Promotion model) {
    JPromotion entity = new JPromotion();

    entity.setId(model.getId());
    entity.setStartYear(model.getStartYear());
    entity.setEndYear(model.getEndYear());

    return entity;
  }
}
