package com.hei.course.service.crud;

import com.hei.course.entity.JCourses;
import com.hei.course.entity.JExam;
import com.hei.course.entity.JSemester;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.ExamMapper;
import com.hei.course.model.Exam;
import com.hei.course.repository.JCourseRepository;
import com.hei.course.repository.JExamRepository;
import com.hei.course.repository.JSemesterRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamService {

  private final JExamRepository examRepository;
  private final JCourseRepository courseRepository;
  private final JSemesterRepository semesterRepository;

  public Exam create(Exam model, UUID courseId, UUID semesterId) {

    JCourses course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));

    JSemester semester =
        semesterRepository
            .findById(semesterId)
            .orElseThrow(() -> new NotFoundException("Semester not found: " + semesterId));

    JExam entity = ExamMapper.toEntity(model, course, semester);

    JExam savedEntity = examRepository.save(entity);

    return ExamMapper.toModel(savedEntity);
  }

  public List<Exam> findAll() {
    return examRepository.findAll().stream().map(ExamMapper::toModel).toList();
  }

  public Exam findById(UUID id) {
    JExam entity =
        examRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Exam not found: " + id));

    return ExamMapper.toModel(entity);
  }

  public Exam update(UUID id, Exam model, UUID courseId, UUID semesterId) {

    JExam entity =
        examRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Exam not found: " + id));

    JCourses course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));

    JSemester semester =
        semesterRepository
            .findById(semesterId)
            .orElseThrow(() -> new NotFoundException("Semester not found: " + semesterId));

    entity.setDate(model.getDate());
    entity.setCoefficient(model.getCoefficient());
    entity.setExamType(model.getExamType());
    entity.setCourses(course);
    entity.setSemester(semester);

    JExam updatedEntity = examRepository.save(entity);

    return ExamMapper.toModel(updatedEntity);
  }

  public void delete(UUID id) {

    JExam entity =
        examRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Exam not found: " + id));

    examRepository.delete(entity);
  }
}
