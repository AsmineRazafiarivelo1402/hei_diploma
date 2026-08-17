package com.hei.course.service.crud;

import com.hei.course.entity.JAffectation;
import com.hei.course.entity.JGroup;
import com.hei.course.entity.JSemester;
import com.hei.course.entity.JStudent;
import com.hei.course.exception.ConflictException;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.AffectationMapper;
import com.hei.course.model.Affectation;
import com.hei.course.repository.JAffectationRepository;
import com.hei.course.repository.JGroupRepository;
import com.hei.course.repository.JSemesterRepository;
import com.hei.course.repository.JStudentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AffectationService {

  private final JAffectationRepository affectationRepository;
  private final JStudentRepository studentRepository;
  private final JGroupRepository groupRepository;
  private final JSemesterRepository semesterRepository;

  public Affectation create(Affectation model, UUID studentId, UUID groupId, UUID semesterId) {

    JStudent student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));

    JGroup group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new NotFoundException("Group not found: " + groupId));

    JSemester semester =
        semesterRepository
            .findById(semesterId)
            .orElseThrow(() -> new NotFoundException("Semester not found: " + semesterId));

    if (affectationRepository.findByStudent_IdAndSemester_Id(studentId, semesterId).isPresent()) {

      throw new ConflictException("Student is already affected to a group for this semester");
    }

    JAffectation entity = AffectationMapper.toEntity(model, student, group, semester);

    JAffectation savedEntity = affectationRepository.save(entity);

    return AffectationMapper.toModel(savedEntity);
  }

  public List<Affectation> findAll() {

    return affectationRepository.findAll().stream().map(AffectationMapper::toModel).toList();
  }

  public Affectation findById(UUID id) {

    JAffectation entity =
        affectationRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Affectation not found: " + id));

    return AffectationMapper.toModel(entity);
  }

  public Affectation update(
      UUID id, Affectation model, UUID studentId, UUID groupId, UUID semesterId) {

    JAffectation entity =
        affectationRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Affectation not found: " + id));

    JStudent student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));

    JGroup group =
        groupRepository
            .findById(groupId)
            .orElseThrow(() -> new NotFoundException("Group not found: " + groupId));

    JSemester semester =
        semesterRepository
            .findById(semesterId)
            .orElseThrow(() -> new NotFoundException("Semester not found: " + semesterId));

    var existingAffectation =
        affectationRepository.findByStudent_IdAndSemester_Id(studentId, semesterId);

    if (existingAffectation.isPresent() && !existingAffectation.get().getId().equals(id)) {

      throw new ConflictException("Student is already affected to a group for this semester");
    }

    entity.setStudent(student);
    entity.setGroup(group);
    entity.setSemester(semester);
    entity.setStatus(model.getStatus());

    JAffectation updatedEntity = affectationRepository.save(entity);

    return AffectationMapper.toModel(updatedEntity);
  }

  public void delete(UUID id) {

    JAffectation entity =
        affectationRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Affectation not found: " + id));

    affectationRepository.delete(entity);
  }

  public List<Affectation> findByStudent(UUID studentId) {

    if (!studentRepository.existsById(studentId)) {
      throw new NotFoundException("Student not found: " + studentId);
    }

    return affectationRepository.findByStudent_Id(studentId).stream()
        .map(AffectationMapper::toModel)
        .toList();
  }

  public List<Affectation> findByPromotion(UUID promotionId) {

    return affectationRepository.findByGroup_Promotion_Id(promotionId).stream()
        .map(AffectationMapper::toModel)
        .toList();
  }
}
