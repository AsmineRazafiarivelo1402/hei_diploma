package com.hei.course.service.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hei.course.entity.JUsers;
import com.hei.course.exception.ConflictException;
import com.hei.course.mapper.UserMapper;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.Student;
import com.hei.course.model.Users;
import com.hei.course.repository.JUsersRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private JUsersRepository usersRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private UserService service;

  @BeforeEach
  void setUp() {
    service = new UserService(usersRepository, passwordEncoder);
  }

  @Test
  void creates_student_with_encoded_password() {
    when(usersRepository.existsByEmail("fenitra@example.com")).thenReturn(false);
    when(usersRepository.existsByReference("REF-1")).thenReturn(false);
    when(passwordEncoder.encode("secret")).thenReturn("encoded");
    when(usersRepository.save(org.mockito.ArgumentMatchers.any(JUsers.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Users created =
        service.createStudent(
            "REF-1",
            "Fenitra",
            "Rakoto",
            Instant.parse("2000-01-01T00:00:00Z"),
            "fenitra@example.com",
            "secret",
            "Antananarivo",
            "+261");

    assertThat(created).isInstanceOf(Student.class);
    assertThat(created.getReference()).isEqualTo("REF-1");
    assertThat(created.getRole()).isEqualTo(RoleEnum.STUDENT);
    assertThat(created.getPassword()).isEqualTo("encoded");

    ArgumentCaptor<JUsers> captor = ArgumentCaptor.forClass(JUsers.class);
    verify(usersRepository).save(captor.capture());
    assertThat(captor.getValue().getEmail()).isEqualTo("fenitra@example.com");
    assertThat(captor.getValue().getRole()).isEqualTo(RoleEnum.STUDENT);
  }

  @Test
  void creates_teacher_with_teacher_role() {
    when(usersRepository.existsByEmail("jean@example.com")).thenReturn(false);
    when(usersRepository.existsByReference("REF-T")).thenReturn(false);
    when(passwordEncoder.encode("secret")).thenReturn("encoded");
    when(usersRepository.save(org.mockito.ArgumentMatchers.any(JUsers.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Users created =
        service.createTeacher(
            "REF-T",
            "Jean",
            "Rasoa",
            Instant.parse("1980-05-10T00:00:00Z"),
            "jean@example.com",
            "secret",
            "Toamasina",
            "+261-32");

    assertThat(created.getRole()).isEqualTo(RoleEnum.TEACHER);
  }

  @Test
  void creates_admin_with_admin_role() {
    when(usersRepository.existsByEmail("admin@example.com")).thenReturn(false);
    when(usersRepository.existsByReference("REF-A")).thenReturn(false);
    when(passwordEncoder.encode("secret")).thenReturn("encoded");
    when(usersRepository.save(org.mockito.ArgumentMatchers.any(JUsers.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Users created =
        service.createAdmin(
            "REF-A",
            "Admin",
            "Admin",
            Instant.parse("1990-01-01T00:00:00Z"),
            "admin@example.com",
            "secret",
            "Antananarivo",
            "+261");

    assertThat(created.getRole()).isEqualTo(RoleEnum.ADMIN);
  }

  @Test
  void throws_when_email_already_exists() {
    when(usersRepository.existsByEmail("fenitra@example.com")).thenReturn(true);

    assertThatThrownBy(
            () ->
                service.createStudent(
                    "REF-1",
                    "Fenitra",
                    "Rakoto",
                    Instant.parse("2000-01-01T00:00:00Z"),
                    "fenitra@example.com",
                    "secret",
                    "Antananarivo",
                    "+261"))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("Email already exists");
  }

  @Test
  void throws_when_reference_already_exists() {
    when(usersRepository.existsByEmail("fenitra@example.com")).thenReturn(false);
    when(usersRepository.existsByReference("REF-1")).thenReturn(true);

    assertThatThrownBy(
            () ->
                service.createStudent(
                    "REF-1",
                    "Fenitra",
                    "Rakoto",
                    Instant.parse("2000-01-01T00:00:00Z"),
                    "fenitra@example.com",
                    "secret",
                    "Antananarivo",
                    "+261"))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("Reference already exists");
  }

  @Test
  void rounds_trip_through_user_mapper_for_existing_student() {
    JUsers entity = new com.hei.course.entity.JStudent();
    entity.setId(UUID.randomUUID());
    entity.setReference("REF-1");
    entity.setRole(RoleEnum.STUDENT);

    Users model = UserMapper.toModel(entity);
    assertThat(model.getId()).isEqualTo(entity.getId());
  }
}
