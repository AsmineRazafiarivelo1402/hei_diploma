package com.hei.course.service.graduate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.hei.course.entity.JAffectation;
import com.hei.course.entity.JCourses;
import com.hei.course.entity.JExam;
import com.hei.course.entity.JNote;
import com.hei.course.entity.JStudent;
import com.hei.course.repository.JAffectationRepository;
import com.hei.course.repository.JNoteRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GraduateEligibilityServiceTest {

  @Mock private JAffectationRepository affectationRepository;
  @Mock private JNoteRepository noteRepository;

  private GraduateEligibilityService service;
  private final UUID promotionId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    service = new GraduateEligibilityService(affectationRepository, noteRepository);
  }

  @Test
  void student_with_all_course_averages_above_10_is_graduated() {
    JStudent student = studentWithId();
    when(affectationRepository.findByGroup_Promotion_Id(promotionId))
        .thenReturn(List.of(affectationOf(student)));
    when(noteRepository.findByStudent_Id(student.getId()))
        .thenReturn(
            List.of(note(student, course("Java"), 12, 1), note(student, course("SQL"), 15, 1)));

    List<JStudent> graduates = service.findGraduates(promotionId);

    assertThat(graduates).containsExactly(student);
  }

  @Test
  void student_with_one_course_average_below_10_is_not_graduated() {
    JStudent student = studentWithId();
    when(affectationRepository.findByGroup_Promotion_Id(promotionId))
        .thenReturn(List.of(affectationOf(student)));
    when(noteRepository.findByStudent_Id(student.getId()))
        .thenReturn(
            List.of(note(student, course("Java"), 8, 1), note(student, course("SQL"), 15, 1)));

    List<JStudent> graduates = service.findGraduates(promotionId);

    assertThat(graduates).isEmpty();
  }

  @Test
  void student_with_no_notes_is_not_graduated() {
    JStudent student = studentWithId();
    when(affectationRepository.findByGroup_Promotion_Id(promotionId))
        .thenReturn(List.of(affectationOf(student)));
    when(noteRepository.findByStudent_Id(student.getId())).thenReturn(List.of());

    List<JStudent> graduates = service.findGraduates(promotionId);

    assertThat(graduates).isEmpty();
  }

  private JStudent studentWithId() {
    JStudent student = new JStudent();
    student.setId(UUID.randomUUID());
    student.setFirstName("Fenitra");
    student.setLastName("Rakoto");
    return student;
  }

  private JAffectation affectationOf(JStudent student) {
    JAffectation affectation = new JAffectation();
    affectation.setStudent(student);
    return affectation;
  }

  private JCourses course(String title) {
    JCourses courses = new JCourses();
    courses.setId(UUID.randomUUID());
    courses.setTitle(title);
    return courses;
  }

  private JNote note(JStudent student, JCourses courses, double value, double coefficient) {
    JExam exam = new JExam();
    exam.setCourses(courses);
    exam.setCoefficient(BigDecimal.valueOf(coefficient));

    return JNote.builder().student(student).exam(exam).value(value).build();
  }
}
