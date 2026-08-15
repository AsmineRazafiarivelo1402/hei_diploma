package com.hei.course.repository;

import com.hei.course.entity.JPromotion;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPromotionRepository extends JpaRepository<JPromotion, UUID> {

  Optional<JPromotion> findByStartYearAndEndYear(int startYear, int endYear);
}
