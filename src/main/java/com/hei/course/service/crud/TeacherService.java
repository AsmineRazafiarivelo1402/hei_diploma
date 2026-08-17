package com.hei.course.service.crud;

import com.hei.course.entity.JTeacher;
import com.hei.course.exception.ConflictException;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.TeacherMapper;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Teacher;
import com.hei.course.repository.JTeacherRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

  private final JTeacherRepository teacherRepository;
  private final PasswordEncoder passwordEncoder;

  public Teacher create(Teacher teacher) {

    if (teacherRepository.existsByEmail(teacher.getEmail())) {
      throw new ConflictException("Email already exists: " + teacher.getEmail());
    }

    if (teacherRepository.existsByReference(teacher.getReference())) {
      throw new ConflictException("Reference already exists: " + teacher.getReference());
    }

    JTeacher entity = new JTeacher();

    entity.setReference(teacher.getReference());
    entity.setFirstName(teacher.getFirstName());
    entity.setLastName(teacher.getLastName());
    entity.setBirthdate(teacher.getBirthdate());
    entity.setEmail(teacher.getEmail());
    entity.setAddress(teacher.getAddress());
    entity.setPhoneNumber(teacher.getPhoneNumber());
    entity.setRole(RoleEnum.TEACHER);
    entity.setPassword(passwordEncoder.encode(teacher.getPassword()));

    JTeacher savedEntity = teacherRepository.save(entity);

    return TeacherMapper.toModel(savedEntity);
  }

  public List<Teacher> findAll() {
    return teacherRepository.findAll().stream().map(TeacherMapper::toModel).toList();
  }

  public Teacher findById(UUID id) {
    JTeacher entity =
        teacherRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + id));

    return TeacherMapper.toModel(entity);
  }

  public Teacher update(UUID id, Teacher teacher) {
    JTeacher entity =
        teacherRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + id));

    if (!entity.getEmail().equals(teacher.getEmail())
        && teacherRepository.existsByEmail(teacher.getEmail())) {
      throw new ConflictException("Email already exists: " + teacher.getEmail());
    }

    if (!entity.getReference().equals(teacher.getReference())
        && teacherRepository.existsByReference(teacher.getReference())) {
      throw new ConflictException("Reference already exists: " + teacher.getReference());
    }

    entity.setReference(teacher.getReference());
    entity.setFirstName(teacher.getFirstName());
    entity.setLastName(teacher.getLastName());
    entity.setBirthdate(teacher.getBirthdate());
    entity.setEmail(teacher.getEmail());
    entity.setAddress(teacher.getAddress());
    entity.setPhoneNumber(teacher.getPhoneNumber());
    entity.setRole(RoleEnum.TEACHER);

    if (teacher.getPassword() != null && !teacher.getPassword().isBlank()) {
      entity.setPassword(passwordEncoder.encode(teacher.getPassword()));
    }

    JTeacher updatedEntity = teacherRepository.save(entity);

    return TeacherMapper.toModel(updatedEntity);
  }

  public void delete(UUID id) {
    if (!teacherRepository.existsById(id)) {
      throw new NotFoundException("Teacher not found with id: " + id);
    }

    teacherRepository.deleteById(id);
  }
}
