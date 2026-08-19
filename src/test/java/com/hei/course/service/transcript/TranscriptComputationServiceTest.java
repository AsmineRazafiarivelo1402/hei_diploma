package com.hei.course.service.transcript;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.hei.course.entity.JAffectation;
import com.hei.course.entity.JCourses;
import com.hei.course.entity.JExam;
import com.hei.course.entity.JGroup;
import com.hei.course.entity.JGroupExam;
import com.hei.course.entity.JNote;
import com.hei.course.entity.JSemester;
import com.hei.course.entity.JStudent;
import com.hei.course.model.SemesterEnum;
import com.hei.course.repository.JAffectationRepository;
import com.hei.course.repository.JGroupExamRepository;
import com.hei.course.repository.JNoteRepository;
import com.hei.course.service.transcript.TranscriptData.CourseAverage;
import com.hei.course.service.transcript.TranscriptData.SemesterTranscript;
import com.hei.course.service.transcript.TranscriptData.Status;
import com.hei.course.service.transcript.TranscriptData.Transcript;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranscriptComputationServiceTest {

  @Mock private JNoteRepository noteRepository;
  @Mock private JAffectationRepository affectationRepository;
  @Mock private JGroupExamRepository groupExamRepository;

  private TranscriptComputationService service;
  private final UUID studentId = UUID.randomUUID();
  private final UUID semesterS1Id = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    service =
        new TranscriptComputationService(
            noteRepository, affectationRepository, groupExamRepository);
  }

  @Test
  void computes_semester_averages_and_credit_weighted_general_average() {
    JSemester s1 = semester(SemesterEnum.S1, semesterS1Id);
    JSemester s2 = semester(SemesterEnum.S2, UUID.randomUUID());

    JCourses java = course("Java", 3);
    JCourses sql = course("SQL", 2);
    JCourses web = course("Web", 4);

    JExam javaExam = exam(java, s1, 2);
    JExam sqlExam = exam(sql, s1, 1);
    JExam webExam = exam(web, s2, 1);

    when(noteRepository.findByStudent_Id(studentId))
        .thenReturn(List.of(note(javaExam, 12), note(sqlExam, 15), note(webExam, 10)));

    JGroup group = group(UUID.randomUUID());
    when(affectationRepository.findByStudent_IdAndSemester_Id(studentId, semesterS1Id))
        .thenReturn(Optional.of(affectationOf(group)));
    when(affectationRepository.findByStudent_IdAndSemester_Id(studentId, s2.getId()))
        .thenReturn(Optional.empty());
    when(groupExamRepository.findByGroup_Id(group.getId()))
        .thenReturn(List.of(groupExam(group, javaExam), groupExam(group, sqlExam)));

    Transcript transcript = service.computeFor(studentId);

    assertThat(transcript.totalCredits()).isEqualTo(9);
    assertThat(transcript.generalAverage()).isEqualByComparingTo("11.78");

    assertThat(transcript.semesters()).hasSize(2);
    assertThat(transcript.semesters())
        .extracting(SemesterTranscript::semester)
        .containsExactly(SemesterEnum.S1, SemesterEnum.S2);

    SemesterTranscript s1Transcript = transcript.semesters().get(0);
    assertThat(s1Transcript.totalCredits()).isEqualTo(5);
    assertThat(s1Transcript.average()).isEqualByComparingTo("13.20");
    assertThat(s1Transcript.status()).isEqualTo(Status.COMPLETE);
    assertThat(s1Transcript.courseAverages())
        .extracting(CourseAverage::courseTitle)
        .containsExactly("Java", "SQL");

    SemesterTranscript s2Transcript = transcript.semesters().get(1);
    assertThat(s2Transcript.totalCredits()).isEqualTo(4);
    assertThat(s2Transcript.average()).isEqualByComparingTo("10.00");
  }

  @Test
  void marks_semester_provisional_when_an_expected_course_is_not_graded() {
    JSemester s1 = semester(SemesterEnum.S1, semesterS1Id);
    JCourses java = course("Java", 3);
    JCourses sql = course("SQL", 2);
    JCourses web = course("Web", 4);

    JExam javaExam = exam(java, s1, 2);
    JExam sqlExam = exam(sql, s1, 1);
    JExam webExam = exam(web, s1, 1);

    when(noteRepository.findByStudent_Id(studentId))
        .thenReturn(List.of(note(javaExam, 12), note(sqlExam, 15)));

    JGroup group = group(UUID.randomUUID());
    when(affectationRepository.findByStudent_IdAndSemester_Id(studentId, semesterS1Id))
        .thenReturn(Optional.of(affectationOf(group)));
    when(groupExamRepository.findByGroup_Id(group.getId()))
        .thenReturn(
            List.of(
                groupExam(group, javaExam), groupExam(group, sqlExam), groupExam(group, webExam)));

    Transcript transcript = service.computeFor(studentId);

    assertThat(transcript.semesters()).hasSize(1);
    assertThat(transcript.semesters().get(0).status()).isEqualTo(Status.PROVISIONAL);
  }

  @Test
  void marks_semester_complete_when_student_has_no_affectation() {
    JSemester s1 = semester(SemesterEnum.S1, semesterS1Id);
    JCourses java = course("Java", 3);
    JExam javaExam = exam(java, s1, 2);

    when(noteRepository.findByStudent_Id(studentId)).thenReturn(List.of(note(javaExam, 12)));
    when(affectationRepository.findByStudent_IdAndSemester_Id(studentId, semesterS1Id))
        .thenReturn(Optional.empty());

    Transcript transcript = service.computeFor(studentId);

    assertThat(transcript.semesters().get(0).status()).isEqualTo(Status.COMPLETE);
  }

  @Test
  void returns_empty_transcript_when_student_has_no_notes() {
    when(noteRepository.findByStudent_Id(studentId)).thenReturn(List.of());

    Transcript transcript = service.computeFor(studentId);

    assertThat(transcript.semesters()).isEmpty();
    assertThat(transcript.totalCredits()).isZero();
    assertThat(transcript.generalAverage()).isEqualByComparingTo("0");
  }

  @Test
  void returns_zero_average_when_course_has_zero_total_coefficient() {
    JSemester s1 = semester(SemesterEnum.S1, semesterS1Id);
    JCourses java = course("Java", 3);
    JExam javaExam = exam(java, s1, BigDecimal.ZERO);

    when(noteRepository.findByStudent_Id(studentId)).thenReturn(List.of(note(javaExam, 12)));
    when(affectationRepository.findByStudent_IdAndSemester_Id(studentId, semesterS1Id))
        .thenReturn(Optional.empty());

    Transcript transcript = service.computeFor(studentId);

    assertThat(transcript.semesters().get(0).courseAverages().get(0).average())
        .isEqualByComparingTo("0");
    assertThat(transcript.semesters().get(0).average()).isEqualByComparingTo("0");
  }

  @Test
  void sorts_courses_alphabetically_and_weights_average_by_credit() {
    JSemester s1 = semester(SemesterEnum.S1, semesterS1Id);
    JCourses zeta = course("Zeta", 4);
    JCourses alpha = course("Alpha", 1);
    JExam zetaExam = exam(zeta, s1, 1);
    JExam alphaExam = exam(alpha, s1, 1);

    when(noteRepository.findByStudent_Id(studentId))
        .thenReturn(List.of(note(zetaExam, 10), note(alphaExam, 20)));
    when(affectationRepository.findByStudent_IdAndSemester_Id(studentId, semesterS1Id))
        .thenReturn(Optional.empty());

    Transcript transcript = service.computeFor(studentId);

    SemesterTranscript semester = transcript.semesters().get(0);
    assertThat(semester.courseAverages())
        .extracting(CourseAverage::courseTitle)
        .containsExactly("Alpha", "Zeta");
    assertThat(semester.totalCredits()).isEqualTo(5);
    assertThat(semester.average()).isEqualByComparingTo("12.00");
  }

  private JSemester semester(SemesterEnum semesterEnum, UUID id) {
    JSemester semester = new JSemester();
    semester.setId(id);
    semester.setSemesterEnum(semesterEnum);
    return semester;
  }

  private JCourses course(String title, int credit) {
    JCourses courses = new JCourses();
    courses.setId(UUID.randomUUID());
    courses.setTitle(title);
    courses.setCredit(credit);
    return courses;
  }

  private JExam exam(JCourses courses, JSemester semester, double coefficient) {
    return exam(courses, semester, BigDecimal.valueOf(coefficient));
  }

  private JExam exam(JCourses courses, JSemester semester, BigDecimal coefficient) {
    JExam exam = new JExam();
    exam.setId(UUID.randomUUID());
    exam.setCourses(courses);
    exam.setSemester(semester);
    exam.setCoefficient(coefficient);
    return exam;
  }

  private JNote note(JExam exam, double value) {
    return JNote.builder().student(new JStudent()).exam(exam).value(value).build();
  }

  private JGroup group(UUID id) {
    JGroup group = new JGroup();
    group.setId(id);
    return group;
  }

  private JAffectation affectationOf(JGroup group) {
    JAffectation affectation = new JAffectation();
    affectation.setGroup(group);
    return affectation;
  }

  private JGroupExam groupExam(JGroup group, JExam exam) {
    return JGroupExam.builder().group(group).exam(exam).build();
  }
}
