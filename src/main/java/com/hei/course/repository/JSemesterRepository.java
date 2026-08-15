package com.hei.course.repository;

import com.hei.course.entity.JSemester;
import com.hei.course.model.SemesterEnum;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JSemesterRepository extends JpaRepository<JSemester, UUID> {

  List<JSemester> findBySemesterEnum(SemesterEnum semesterEnum);
}
