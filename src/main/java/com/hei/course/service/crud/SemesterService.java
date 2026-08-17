package com.hei.course.service.crud;

import com.hei.course.entity.JSemester;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.SemesterMapper;
import com.hei.course.model.Semester;
import com.hei.course.repository.JSemesterRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SemesterService {

  private final JSemesterRepository semesterRepository;

  public Semester create(Semester model) {
    JSemester entity = SemesterMapper.toEntity(model);

    JSemester savedEntity = semesterRepository.save(entity);

    return SemesterMapper.toModel(savedEntity);
  }

  public List<Semester> findAll() {
    return semesterRepository.findAll().stream().map(SemesterMapper::toModel).toList();
  }

  public Semester findById(UUID id) {
    JSemester entity =
        semesterRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Semester not found: " + id));

    return SemesterMapper.toModel(entity);
  }

  public Semester update(UUID id, Semester model) {
    JSemester entity =
        semesterRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Semester not found: " + id));

    entity.setSemesterEnum(model.getSemestreEnum());
    entity.setStartDate(model.getStartDate());
    entity.setEndDate(model.getEndDate());

    JSemester updatedEntity = semesterRepository.save(entity);

    return SemesterMapper.toModel(updatedEntity);
  }

  public void delete(UUID id) {
    JSemester entity =
        semesterRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Semester not found: " + id));

    semesterRepository.delete(entity);
  }
}
