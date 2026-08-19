package com.hei.course.service.crud;

import com.hei.course.entity.JStudent;
import com.hei.course.exception.ConflictException;
import com.hei.course.exception.NotFoundException;
import com.hei.course.mapper.StudentMapper;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Student;
import com.hei.course.repository.JStudentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

  private final JStudentRepository studentRepository;
  private final PasswordEncoder passwordEncoder;

  public Student create(Student student) {

    if (studentRepository.existsByEmail(student.getEmail())) {
      throw new ConflictException("Email already exists: " + student.getEmail());
    }

    if (studentRepository.existsByReference(student.getReference())) {
      throw new ConflictException("Reference already exists: " + student.getReference());
    }

    JStudent entity = new JStudent();

    entity.setReference(student.getReference());
    entity.setFirstName(student.getFirstName());
    entity.setLastName(student.getLastName());
    entity.setBirthdate(student.getBirthdate());
    entity.setEmail(student.getEmail());
    entity.setAddress(student.getAddress());
    entity.setPhoneNumber(student.getPhoneNumber());
    entity.setRole(RoleEnum.STUDENT);
    entity.setPassword(passwordEncoder.encode(student.getPassword()));

    JStudent savedEntity = studentRepository.save(entity);

    return StudentMapper.toModel(savedEntity);
  }

  public List<Student> findAll() {
    return studentRepository.findAll().stream().map(StudentMapper::toModel).toList();
  }

  public Student findById(UUID id) {
    JStudent entity =
        studentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Student not found with id: " + id));

    return StudentMapper.toModel(entity);
  }

  public Student update(UUID id, Student student) {
    JStudent entity =
        studentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Student not found with id: " + id));

    if (!entity.getEmail().equals(student.getEmail())
        && studentRepository.existsByEmail(student.getEmail())) {
      throw new ConflictException("Email already exists: " + student.getEmail());
    }

    if (!entity.getReference().equals(student.getReference())
        && studentRepository.existsByReference(student.getReference())) {
      throw new ConflictException("Reference already exists: " + student.getReference());
    }

    entity.setReference(student.getReference());
    entity.setFirstName(student.getFirstName());
    entity.setLastName(student.getLastName());
    entity.setBirthdate(student.getBirthdate());
    entity.setEmail(student.getEmail());
    entity.setAddress(student.getAddress());
    entity.setPhoneNumber(student.getPhoneNumber());
    entity.setRole(RoleEnum.STUDENT);

    if (student.getPassword() != null && !student.getPassword().isBlank()) {
      entity.setPassword(passwordEncoder.encode(student.getPassword()));
    }

    JStudent updatedEntity = studentRepository.save(entity);

    return StudentMapper.toModel(updatedEntity);
  }

  public void delete(UUID id) {
    if (!studentRepository.existsById(id)) {
      throw new NotFoundException("Student not found with id: " + id);
    }

    studentRepository.deleteById(id);
  }
}
