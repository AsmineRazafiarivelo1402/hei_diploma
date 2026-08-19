package com.hei.course.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.course.conf.FacadeIT;
import com.hei.course.entity.JAdmin;
import com.hei.course.entity.JStudent;
import com.hei.course.entity.JTeacher;
import com.hei.course.entity.JUsers;
import com.hei.course.model.RoleEnum;
import com.hei.course.repository.JAdminRepository;
import com.hei.course.repository.JStudentRepository;
import com.hei.course.repository.JTeacherRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecurityIT extends FacadeIT {

  private static final String RAW_PASSWORD = "P@ssw0rd!";

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private JAdminRepository adminRepository;
  @Autowired private JTeacherRepository teacherRepository;
  @Autowired private JStudentRepository studentRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private String adminEmail;
  private String teacherEmail;
  private String studentEmail;
  private static final String WRONG_PASSWORD = "not-the-password";

  @BeforeEach
  void createUsers() {
    String suffix = UUID.randomUUID().toString();
    adminEmail = "admin-" + suffix + "@hei.test";
    teacherEmail = "teacher-" + suffix + "@hei.test";
    studentEmail = "student-" + suffix + "@hei.test";

    adminRepository.save(newUser(new JAdmin(), adminEmail, RoleEnum.ADMIN));
    teacherRepository.save(newUser(new JTeacher(), teacherEmail, RoleEnum.TEACHER));
    studentRepository.save(newUser(new JStudent(), studentEmail, RoleEnum.STUDENT));
  }

  private <T extends JUsers> T newUser(T user, String email, RoleEnum role) {
    user.setReference(UUID.randomUUID().toString());
    user.setFirstName("Test");
    user.setLastName(role.name());
    user.setEmail(email);
    user.setRole(role);
    user.setPassword(passwordEncoder.encode(RAW_PASSWORD));
    return user;
  }


  @Test
  void unknown_email_is_rejected_with_401() {
    var response =
        restTemplate
            .withBasicAuth("does-not-exist@hei.test", RAW_PASSWORD)
            .getForEntity("/students", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void wrong_password_is_rejected_with_401() {
    var response =
        restTemplate
            .withBasicAuth(adminEmail, WRONG_PASSWORD)
            .getForEntity("/students", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void valid_credentials_are_mapped_to_the_user_role_and_authenticate_successfully() {
    var response =
        restTemplate
            .withBasicAuth(adminEmail, RAW_PASSWORD)
            .getForEntity("/students", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }


  @Test
  void ping_is_public_and_does_not_require_authentication() {
    var response = restTemplate.getForEntity("/ping", String.class);

    assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.FORBIDDEN);
  }


  @Test
  void notes_get_requires_authentication() {
    var anonymous = restTemplate.getForEntity("/notes", String.class);
    assertThat(anonymous.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    var asTeacher =
        restTemplate.withBasicAuth(teacherEmail, RAW_PASSWORD).getForEntity("/notes", String.class);
    assertThat(asTeacher.getStatusCode()).isEqualTo(HttpStatus.OK);

    var asAdmin =
        restTemplate.withBasicAuth(adminEmail, RAW_PASSWORD).getForEntity("/notes", String.class);
    assertThat(asAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void notes_get_is_forbidden_for_student_by_business_rule() {

    var asStudent =
        restTemplate.withBasicAuth(studentEmail, RAW_PASSWORD).getForEntity("/notes", String.class);
    assertThat(asStudent.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }


  @Test
  void notes_post_is_forbidden_for_student() {
    var response =
        restTemplate
            .withBasicAuth(studentEmail, RAW_PASSWORD)
            .postForEntity(
                "/notes?examId=" + UUID.randomUUID() + "&studentId=" + UUID.randomUUID(),
                "{}",
                String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void notes_post_passes_security_for_teacher_and_admin() {

    var asTeacher =
        restTemplate
            .withBasicAuth(teacherEmail, RAW_PASSWORD)
            .postForEntity(
                "/notes?examId=" + UUID.randomUUID() + "&studentId=" + UUID.randomUUID(),
                "{}",
                String.class);
    assertThat(asTeacher.getStatusCode()).isNotIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);

    var asAdmin =
        restTemplate
            .withBasicAuth(adminEmail, RAW_PASSWORD)
            .postForEntity(
                "/notes?examId=" + UUID.randomUUID() + "&studentId=" + UUID.randomUUID(),
                "{}",
                String.class);
    assertThat(asAdmin.getStatusCode()).isNotIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
  }


  @Test
  void students_endpoint_is_admin_only() {
    var anonymous = restTemplate.getForEntity("/students", String.class);
    assertThat(anonymous.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    var asStudent =
        restTemplate
            .withBasicAuth(studentEmail, RAW_PASSWORD)
            .getForEntity("/students", String.class);
    assertThat(asStudent.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    var asTeacher =
        restTemplate
            .withBasicAuth(teacherEmail, RAW_PASSWORD)
            .getForEntity("/students", String.class);
    assertThat(asTeacher.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    var asAdmin =
        restTemplate
            .withBasicAuth(adminEmail, RAW_PASSWORD)
            .getForEntity("/students", String.class);
    assertThat(asAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void users_endpoint_is_admin_only() {
    var asTeacher =
        restTemplate.withBasicAuth(teacherEmail, RAW_PASSWORD).getForEntity("/users", String.class);
    assertThat(asTeacher.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }


  @Test
  void unmapped_endpoint_defaults_to_admin_only() {
    var asTeacher =
        restTemplate
            .withBasicAuth(teacherEmail, RAW_PASSWORD)
            .getForEntity("/some-unmapped-path", String.class);
    assertThat(asTeacher.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);


    var asAdmin =
        restTemplate
            .withBasicAuth(adminEmail, RAW_PASSWORD)
            .getForEntity("/some-unmapped-path", String.class);
    assertThat(asAdmin.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
