package com.hei.course.crud;

import com.hei.course.conf.FacadeIT;
import com.hei.course.entity.JAdmin;
import com.hei.course.entity.JCourses;
import com.hei.course.entity.JPromotion;
import com.hei.course.entity.JSemester;
import com.hei.course.entity.JStudent;
import com.hei.course.entity.JTeacher;
import com.hei.course.model.RoleEnum;
import com.hei.course.model.SemesterEnum;
import com.hei.course.repository.JAdminRepository;
import com.hei.course.repository.JCourseRepository;
import com.hei.course.repository.JPromotionRepository;
import com.hei.course.repository.JSemesterRepository;
import com.hei.course.repository.JStudentRepository;
import com.hei.course.repository.JTeacherRepository;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

abstract class AbstractAdminCrudIT extends FacadeIT {

  protected static final String RAW_PASSWORD = "P@ssw0rd!";

  private static final AtomicInteger PROMOTION_COUNTER = new AtomicInteger(0);

  @Autowired protected TestRestTemplate rawRestTemplate;
  @Autowired protected JAdminRepository adminRepository;
  @Autowired protected JPromotionRepository promotionRepository;
  @Autowired protected JSemesterRepository semesterRepository;
  @Autowired protected JCourseRepository courseRepository;
  @Autowired protected JTeacherRepository teacherRepository;
  @Autowired protected JStudentRepository studentRepository;
  @Autowired protected PasswordEncoder passwordEncoder;

  protected TestRestTemplate restTemplate;

  @BeforeEach
  void authenticateAsAdmin() {
    String adminEmail = "admin-" + UUID.randomUUID() + "@hei.test";

    JAdmin admin = new JAdmin();
    admin.setReference(UUID.randomUUID().toString());
    admin.setFirstName("Test");
    admin.setLastName("Admin");
    admin.setEmail(adminEmail);
    admin.setRole(RoleEnum.ADMIN);
    admin.setPassword(passwordEncoder.encode(RAW_PASSWORD));
    adminRepository.save(admin);

    restTemplate = rawRestTemplate.withBasicAuth(adminEmail, RAW_PASSWORD);
  }

  protected JPromotion persistPromotion() {
    JPromotion promotion = new JPromotion();
    int start = 1990 + PROMOTION_COUNTER.getAndIncrement();
    promotion.setStartYear(start);
    promotion.setEndYear(start + 3);
    return promotionRepository.save(promotion);
  }

  protected JSemester persistSemester() {
    JSemester semester = new JSemester();
    semester.setSemesterEnum(SemesterEnum.S1);
    semester.setStartDate(Instant.parse("2024-09-01T00:00:00Z"));
    semester.setEndDate(Instant.parse("2024-12-31T00:00:00Z"));
    return semesterRepository.save(semester);
  }

  protected JCourses persistCourse() {
    JCourses course = new JCourses();
    String suffix = UUID.randomUUID().toString();
    course.setReference("CRS-" + suffix);
    course.setTitle("Algorithmique");
    course.setCredit(5);
    return courseRepository.save(course);
  }

  protected JTeacher persistTeacher() {
    JTeacher teacher = new JTeacher();
    String suffix = UUID.randomUUID().toString();
    teacher.setReference("TEA-" + suffix);
    teacher.setFirstName("Hery");
    teacher.setLastName("Andria");
    teacher.setBirthdate(Instant.parse("1985-01-01T00:00:00Z"));
    teacher.setEmail("teacher-" + suffix + "@hei.test");
    teacher.setAddress("Antananarivo");
    teacher.setPhoneNumber("0330000000");
    teacher.setRole(RoleEnum.TEACHER);
    teacher.setPassword(passwordEncoder.encode(RAW_PASSWORD));
    return teacherRepository.save(teacher);
  }

  protected JStudent persistStudent() {
    JStudent student = new JStudent();
    String suffix = UUID.randomUUID().toString();
    student.setReference("STU-" + suffix);
    student.setFirstName("Jean");
    student.setLastName("Rakoto");
    student.setBirthdate(Instant.parse("2000-01-01T00:00:00Z"));
    student.setEmail("student-" + suffix + "@hei.test");
    student.setAddress("Antananarivo");
    student.setPhoneNumber("0340000000");
    student.setRole(RoleEnum.STUDENT);
    student.setPassword(passwordEncoder.encode(RAW_PASSWORD));
    return studentRepository.save(student);
  }
}
