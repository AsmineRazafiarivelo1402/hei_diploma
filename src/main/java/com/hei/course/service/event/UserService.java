package com.hei.course.service.event;

import com.hei.course.entity.JAdmin;
import com.hei.course.entity.JStudent;
import com.hei.course.entity.JTeacher;
import com.hei.course.entity.JUsers;
import com.hei.course.exception.ConflictException;
import com.hei.course.mapper.UserMapper;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Users;
import com.hei.course.repository.JUsersRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final JUsersRepository usersRepository;
  private final PasswordEncoder passwordEncoder;

  public Users createStudent(
      String reference,
      String firstName,
      String lastName,
      Instant birthdate,
      String email,
      String rawPassword,
      String address,
      String phoneNumber) {

    JStudent entity = new JStudent();

    return saveUser(
        entity,
        reference,
        firstName,
        lastName,
        birthdate,
        email,
        rawPassword,
        address,
        phoneNumber,
        RoleEnum.STUDENT);
  }

  public Users createTeacher(
      String reference,
      String firstName,
      String lastName,
      Instant birthdate,
      String email,
      String rawPassword,
      String address,
      String phoneNumber) {

    JTeacher entity = new JTeacher();

    return saveUser(
        entity,
        reference,
        firstName,
        lastName,
        birthdate,
        email,
        rawPassword,
        address,
        phoneNumber,
        RoleEnum.TEACHER);
  }

  public Users createAdmin(
      String reference,
      String firstName,
      String lastName,
      Instant birthdate,
      String email,
      String rawPassword,
      String address,
      String phoneNumber) {

    JAdmin entity = new JAdmin();

    return saveUser(
        entity,
        reference,
        firstName,
        lastName,
        birthdate,
        email,
        rawPassword,
        address,
        phoneNumber,
        RoleEnum.ADMIN);
  }

  private Users saveUser(
      JUsers entity,
      String reference,
      String firstName,
      String lastName,
      Instant birthdate,
      String email,
      String rawPassword,
      String address,
      String phoneNumber,
      RoleEnum role) {

    if (usersRepository.existsByEmail(email)) {
      throw new ConflictException("Email already exists: " + email);
    }

    if (usersRepository.existsByReference(reference)) {
      throw new ConflictException("Reference already exists: " + reference);
    }

    entity.setReference(reference);
    entity.setFirstName(firstName);
    entity.setLastName(lastName);
    entity.setBirthdate(birthdate);
    entity.setEmail(email);
    entity.setPassword(passwordEncoder.encode(rawPassword));
    entity.setAddress(address);
    entity.setPhoneNumber(phoneNumber);
    entity.setRole(role);

    JUsers savedEntity = usersRepository.save(entity);

    return UserMapper.toModel(savedEntity);
  }
}
