package com.hei.course.repository;

import com.hei.course.entity.JAffectation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JAffectationRepository extends JpaRepository<JAffectation, UUID> {

  List<JAffectation> findByStudent_Id(UUID studentId);

  Optional<JAffectation> findByStudent_IdAndSemester_Id(UUID studentId, UUID semesterId);

  List<JAffectation> findByGroup_Promotion_Id(UUID promotionId);
}
