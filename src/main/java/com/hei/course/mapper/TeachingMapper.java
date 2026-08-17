package com.hei.course.mapper;

import com.hei.course.entity.JTeaching;
import com.hei.course.model.Teaching;

public class TeachingMapper {

    private TeachingMapper() {}

    public static Teaching toModel(JTeaching entity) {
        return Teaching.builder()
                .id(entity.getId())
                .courses(CourseMapper.toModel(entity.getCourses()))
                .teacher(TeacherMapper.toModel(entity.getTeacher()))
                .build();
    }

    public static JTeaching toEntity(JTeaching entity, Teaching model) {
        entity.setId(model.getId());
        return entity;
    }
}