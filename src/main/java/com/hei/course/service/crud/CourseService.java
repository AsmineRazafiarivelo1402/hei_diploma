package com.hei.course.service.crud;

import com.hei.course.entity.JCourses;
import com.hei.course.mapper.CourseMapper;
import com.hei.course.model.Courses;
import com.hei.course.repository.JCourseRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final JCourseRepository courseRepository;

  public Courses create(Courses model) {
    JCourses entity = CourseMapper.toEntity(model);

    JCourses savedEntity = courseRepository.save(entity);

    return CourseMapper.toModel(savedEntity);
  }

  public List<Courses> findAll() {
    return courseRepository.findAll().stream().map(CourseMapper::toModel).toList();
  }

  public Courses findById(UUID id) {
    JCourses entity =
        courseRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found: " + id));

    return CourseMapper.toModel(entity);
  }

  public Courses update(UUID id, Courses model) {
    JCourses entity =
        courseRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found: " + id));

    entity.setReference(model.getReference());
    entity.setTitle(model.getTitle());
    entity.setCredit(model.getCredit());

    JCourses updatedEntity = courseRepository.save(entity);

    return CourseMapper.toModel(updatedEntity);
  }

  public void delete(UUID id) {
    JCourses entity =
        courseRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found: " + id));

    courseRepository.delete(entity);
  }
}
