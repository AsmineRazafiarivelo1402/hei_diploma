package com.hei.course.repository;

import com.hei.course.entity.JPromotion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JPromotionRepository extends JpaRepository<JPromotion, UUID> {

  Optional<JPromotion> findByStartYearAndEndYear(int startYear, int endYear);

  @Query("SELECT DISTINCT p FROM JPromotion p LEFT JOIN FETCH p.groupList WHERE p.id = :id")
  Optional<JPromotion> findByIdWithGroups(UUID id);

  @Query("SELECT DISTINCT p FROM JPromotion p LEFT JOIN FETCH p.groupList")
  List<JPromotion> findAllWithGroups();
}
