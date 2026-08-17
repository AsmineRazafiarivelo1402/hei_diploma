package com.hei.course.service.crud;

import com.hei.course.entity.JGroup;
import com.hei.course.entity.JPromotion;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.GroupMapper;
import com.hei.course.model.Group;
import com.hei.course.repository.JGroupRepository;
import com.hei.course.repository.JPromotionRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

  private final JGroupRepository groupRepository;
  private final JPromotionRepository promotionRepository;

  public Group create(Group model, UUID promotionId) {
    JPromotion promotion =
        promotionRepository
            .findById(promotionId)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + promotionId));

    JGroup entity = GroupMapper.toEntity(model, promotion);

    JGroup savedEntity = groupRepository.save(entity);

    return GroupMapper.toModel(savedEntity);
  }

  public List<Group> findAll() {
    return groupRepository.findAll().stream().map(GroupMapper::toModel).toList();
  }

  public Group findById(UUID id) {
    JGroup entity =
        groupRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Group not found: " + id));

    return GroupMapper.toModel(entity);
  }

  public Group update(UUID id, Group model, UUID promotionId) {
    JGroup entity =
        groupRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Group not found: " + id));

    JPromotion promotion =
        promotionRepository
            .findById(promotionId)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + promotionId));

    entity.setReference(model.getReference());
    entity.setSpeciality(model.getSpeciality());
    entity.setPromotion(promotion);

    JGroup updatedEntity = groupRepository.save(entity);

    return GroupMapper.toModel(updatedEntity);
  }

  public void delete(UUID id) {
    JGroup entity =
        groupRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Group not found: " + id));

    groupRepository.delete(entity);
  }
}
