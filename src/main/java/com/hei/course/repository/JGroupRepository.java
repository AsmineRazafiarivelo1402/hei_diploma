package com.hei.course.repository;

import com.hei.course.entity.JGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JGroupRepository extends JpaRepository<JGroup, UUID> {

    List<JGroup> findByPromotion_Id(UUID promotionId);
}
