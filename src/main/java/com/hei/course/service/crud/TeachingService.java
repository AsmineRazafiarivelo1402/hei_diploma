package com.hei.course.service.crud;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JTeacher;
import com.hei.course.entity.JTeaching;
import com.hei.course.exception.ConflictException;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.TeachingMapper;
import com.hei.course.model.Teaching;
import com.hei.course.repository.JCourseRepository;
import com.hei.course.repository.JTeacherRepository;
import com.hei.course.repository.JTeachingRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeachingService {

  private final JTeachingRepository teachingRepository;
  private final JCourseRepository courseRepository;
  private final JTeacherRepository teacherRepository;

  public Teaching create(UUID courseId, UUID teacherId) {

    JCourses course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));

    JTeacher teacher =
        teacherRepository
            .findById(teacherId)
            .orElseThrow(() -> new NotFoundException("Teacher not found: " + teacherId));

    if (teachingRepository.existsByCourses_IdAndTeacher_Id(courseId, teacherId)) {
      throw new ConflictException(
          "Teacher " + teacherId + " is already assigned to course " + courseId);
    }

    JTeaching teaching = new JTeaching();
    teaching.setCourses(course);
    teaching.setTeacher(teacher);

    JTeaching savedTeaching = teachingRepository.save(teaching);

    return TeachingMapper.toModel(savedTeaching);
  }

  public List<Teaching> findAll() {
    return teachingRepository.findAll().stream().map(TeachingMapper::toModel).toList();
  }

  public Teaching findById(UUID id) {

    JTeaching teaching =
        teachingRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Teaching not found: " + id));

    return TeachingMapper.toModel(teaching);
  }

  public Teaching update(UUID id, UUID courseId, UUID teacherId) {

    JTeaching teaching =
        teachingRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Teaching not found: " + id));

    JCourses course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));

    JTeacher teacher =
        teacherRepository
            .findById(teacherId)
            .orElseThrow(() -> new NotFoundException("Teacher not found: " + teacherId));

    boolean duplicate = teachingRepository.existsByCourses_IdAndTeacher_Id(courseId, teacherId);

    if (duplicate
        && (!teaching.getCourses().getId().equals(courseId)
            || !teaching.getTeacher().getId().equals(teacherId))) {
      throw new ConflictException(
          "Teacher " + teacherId + " is already assigned to course " + courseId);
    }

    teaching.setCourses(course);
    teaching.setTeacher(teacher);

    JTeaching updatedTeaching = teachingRepository.save(teaching);

    return TeachingMapper.toModel(updatedTeaching);
  }

  public void delete(UUID id) {

    JTeaching teaching =
        teachingRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Teaching not found: " + id));

    teachingRepository.delete(teaching);
  }

  public List<Teaching> findByTeacher(UUID teacherId) {

    if (!teacherRepository.existsById(teacherId)) {
      throw new NotFoundException("Teacher not found: " + teacherId);
    }

    return teachingRepository.findByTeacher_Id(teacherId).stream()
        .map(TeachingMapper::toModel)
        .toList();
  }
}
