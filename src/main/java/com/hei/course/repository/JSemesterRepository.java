package com.hei.course.repository;

import com.hei.course.entity.JSemester;
import com.hei.course.model.SemesterEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JSemesterRepository extends JpaRepository<JSemester, UUID> {

    List<JSemester> findBySemestreEnum(SemesterEnum semesterEnum);
}
