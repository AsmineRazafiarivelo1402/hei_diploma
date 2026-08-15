package com.hei.course.repository;


import com.hei.course.entity.JGroupExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JGroupExamRepository extends JpaRepository<JGroupExam, UUID> {

    List<JGroupExam> findByGroup_Id(UUID groupId);
}
