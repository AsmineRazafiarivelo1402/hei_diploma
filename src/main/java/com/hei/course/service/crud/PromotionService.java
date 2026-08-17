package com.hei.course.service.crud;

import com.hei.course.entity.JPromotion;
import com.hei.course.exception.ConflictException;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.PromotionMapper;
import com.hei.course.model.Promotion;
import com.hei.course.repository.JPromotionRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionService {

  private final JPromotionRepository promotionRepository;

  public Promotion create(Promotion model) {

    promotionRepository
        .findByStartYearAndEndYear(model.getStartYear(), model.getEndYear())
        .ifPresent(
            existing -> {
              throw new ConflictException(
                  "Promotion already exists for years "
                      + model.getStartYear()
                      + "-"
                      + model.getEndYear());
            });

    JPromotion entity = PromotionMapper.toEntity(model);

    JPromotion savedEntity = promotionRepository.save(entity);

    return PromotionMapper.toModel(savedEntity);
  }

  public List<Promotion> findAll() {
    return promotionRepository.findAll().stream().map(PromotionMapper::toModel).toList();
  }

  public Promotion findById(UUID id) {

    JPromotion entity =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + id));

    return PromotionMapper.toModel(entity);
  }

  public Promotion update(UUID id, Promotion model) {

    JPromotion entity =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + id));

    promotionRepository
        .findByStartYearAndEndYear(model.getStartYear(), model.getEndYear())
        .ifPresent(
            existing -> {
              if (!existing.getId().equals(id)) {
                throw new ConflictException(
                    "Promotion already exists for years "
                        + model.getStartYear()
                        + "-"
                        + model.getEndYear());
              }
            });

    entity.setStartYear(model.getStartYear());
    entity.setEndYear(model.getEndYear());

    JPromotion updatedEntity = promotionRepository.save(entity);

    return PromotionMapper.toModel(updatedEntity);
  }

  public void delete(UUID id) {

    JPromotion entity =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + id));

    promotionRepository.delete(entity);
  }
}
