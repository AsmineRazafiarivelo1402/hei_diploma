package com.hei.course.service.crud;

import com.hei.course.entity.JCourseSpeciality;
import com.hei.course.entity.JCourses;
import com.hei.course.entity.JSemester;
import com.hei.course.exception.ConflictException;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.CourseSpecialityMapper;
import com.hei.course.model.CourseSpeciality;
import com.hei.course.repository.JCourseRepository;
import com.hei.course.repository.JCourseSpecialityRepository;
import com.hei.course.repository.JSemesterRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseSpecialityService {

  private final JCourseSpecialityRepository courseSpecialityRepository;
  private final JCourseRepository courseRepository;
  private final JSemesterRepository semesterRepository;

  public CourseSpeciality create(CourseSpeciality model, UUID coursesId, UUID semesterId) {

    JCourses courses =
        courseRepository
            .findById(coursesId)
            .orElseThrow(() -> new NotFoundException("Course not found: " + coursesId));

    JSemester semester =
        semesterRepository
            .findById(semesterId)
            .orElseThrow(() -> new NotFoundException("Semester not found: " + semesterId));

    boolean alreadyExists =
        courseSpecialityRepository.findByCourses_Id(coursesId).stream()
            .anyMatch(
                existing ->
                    existing.getSemester().getId().equals(semesterId)
                        && existing.getSpecialityEnum().equals(model.getSpeciality()));

    if (alreadyExists) {
      throw new ConflictException(
          "This speciality is already assigned to this course for this semester");
    }

    JCourseSpeciality entity = CourseSpecialityMapper.toEntity(model, courses, semester);

    JCourseSpeciality savedEntity = courseSpecialityRepository.save(entity);

    return CourseSpecialityMapper.toModel(savedEntity);
  }

  public List<CourseSpeciality> findAll() {

    return courseSpecialityRepository.findAll().stream()
        .map(CourseSpecialityMapper::toModel)
        .toList();
  }

  public CourseSpeciality findById(UUID id) {

    JCourseSpeciality entity =
        courseSpecialityRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Course speciality not found: " + id));

    return CourseSpecialityMapper.toModel(entity);
  }

  public List<CourseSpeciality> findByCourse(UUID coursesId) {

    if (!courseRepository.existsById(coursesId)) {
      throw new NotFoundException("Course not found: " + coursesId);
    }

    return courseSpecialityRepository.findByCourses_Id(coursesId).stream()
        .map(CourseSpecialityMapper::toModel)
        .toList();
  }

  public CourseSpeciality update(UUID id, CourseSpeciality model, UUID coursesId, UUID semesterId) {

    JCourseSpeciality entity =
        courseSpecialityRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Course speciality not found: " + id));

    JCourses courses =
        courseRepository
            .findById(coursesId)
            .orElseThrow(() -> new NotFoundException("Course not found: " + coursesId));

    JSemester semester =
        semesterRepository
            .findById(semesterId)
            .orElseThrow(() -> new NotFoundException("Semester not found: " + semesterId));

    boolean alreadyExists =
        courseSpecialityRepository.findByCourses_Id(coursesId).stream()
            .anyMatch(
                existing ->
                    existing.getSemester().getId().equals(semesterId)
                        && existing.getSpecialityEnum().equals(model.getSpeciality())
                        && !existing.getId().equals(id));

    if (alreadyExists) {
      throw new ConflictException(
          "This speciality is already assigned to this course for this semester");
    }

    entity.setCourses(courses);
    entity.setSpecialityEnum(model.getSpeciality());
    entity.setSemester(semester);

    JCourseSpeciality updatedEntity = courseSpecialityRepository.save(entity);

    return CourseSpecialityMapper.toModel(updatedEntity);
  }

  public void delete(UUID id) {

    JCourseSpeciality entity =
        courseSpecialityRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Course speciality not found: " + id));

    courseSpecialityRepository.delete(entity);
  }
}
