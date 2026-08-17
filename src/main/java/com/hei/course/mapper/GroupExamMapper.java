package com.hei.course.mapper;

import com.hei.course.entity.JExam;
import com.hei.course.entity.JGroup;
import com.hei.course.entity.JGroupExam;
import com.hei.course.model.GroupExam;

public class GroupExamMapper {

  private GroupExamMapper() {}

  public static GroupExam toModel(JGroupExam entity) {
    return GroupExam.builder()
        .id(entity.getId())
        .group(GroupMapper.toModel(entity.getGroup()))
        .exam(ExamMapper.toModel(entity.getExam()))
        .build();
  }

  public static JGroupExam toEntity(GroupExam model, JGroup group, JExam exam) {

    JGroupExam entity = new JGroupExam();

    entity.setId(model.getId());
    entity.setGroup(group);
    entity.setExam(exam);

    return entity;
  }
}
