package com.hei.course.service.transcript;

import com.hei.course.entity.JAffectation;
import com.hei.course.entity.JExam;
import com.hei.course.entity.JNote;
import com.hei.course.model.SemesterEnum;
import com.hei.course.repository.JAffectationRepository;
import com.hei.course.repository.JGroupExamRepository;
import com.hei.course.repository.JNoteRepository;
import com.hei.course.service.transcript.TranscriptData.CourseAverage;
import com.hei.course.service.transcript.TranscriptData.SemesterTranscript;
import com.hei.course.service.transcript.TranscriptData.Status;
import com.hei.course.service.transcript.TranscriptData.Transcript;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TranscriptComputationService {

  private final JNoteRepository noteRepository;
  private final JAffectationRepository affectationRepository;
  private final JGroupExamRepository groupExamRepository;

  public Transcript computeFor(UUID studentId) {
    List<JNote> notes = noteRepository.findByStudent_Id(studentId);

    Map<SemesterSummaryKey, List<JNote>> notesBySemester =
        notes.stream()
            .collect(
                Collectors.groupingBy(
                    note ->
                        new SemesterSummaryKey(
                            note.getExam().getSemester().getId(),
                            note.getExam().getSemester().getSemesterEnum())));

    List<SemesterTranscript> semesters =
        notesBySemester.entrySet().stream()
            .map(entry -> toSemesterTranscript(studentId, entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(s -> s.semester().name()))
            .toList();

    int totalCredits = semesters.stream().mapToInt(SemesterTranscript::totalCredits).sum();
    BigDecimal generalAverage = creditWeightedAverage(semesters);

    return new Transcript(semesters, totalCredits, generalAverage);
  }

  private SemesterTranscript toSemesterTranscript(
      UUID studentId, SemesterSummaryKey semesterKey, List<JNote> notesForSemester) {
    Map<UUID, List<JNote>> notesByCourse =
        notesForSemester.stream()
            .collect(Collectors.groupingBy(note -> note.getExam().getCourses().getId()));

    List<CourseAverage> courseAverages =
        notesByCourse.values().stream()
            .map(
                notes -> {
                  var course = notes.get(0).getExam().getCourses();
                  return new CourseAverage(
                      course.getTitle(), course.getCredit(), weightedAverage(notes));
                })
            .sorted(Comparator.comparing(CourseAverage::courseTitle))
            .toList();

    int totalCredits = courseAverages.stream().mapToInt(CourseAverage::credit).sum();
    BigDecimal semesterAverage = creditWeightedCourseAverage(courseAverages);
    Status status = resolveStatus(studentId, semesterKey, notesByCourse.keySet());

    return new SemesterTranscript(
        semesterKey.semesterEnum(), courseAverages, totalCredits, semesterAverage, status);
  }

  private Status resolveStatus(
      UUID studentId, SemesterSummaryKey semesterKey, Set<UUID> courseIdsWithNotes) {
    Set<UUID> expectedCourseIds = expectedCourseIds(studentId, semesterKey.semesterId());
    boolean allExpectedCoursesGraded = courseIdsWithNotes.containsAll(expectedCourseIds);
    return allExpectedCoursesGraded ? Status.COMPLETE : Status.PROVISIONAL;
  }

  /** The set of courses the student's group is expected to be graded on for a given semester. */
  private Set<UUID> expectedCourseIds(UUID studentId, UUID semesterId) {
    return affectationRepository
        .findByStudent_IdAndSemester_Id(studentId, semesterId)
        .map(JAffectation::getGroup)
        .map(group -> groupExamRepository.findByGroup_Id(group.getId()))
        .orElseGet(List::of)
        .stream()
        .map(groupExam -> groupExam.getExam())
        .filter(exam -> exam.getSemester().getId().equals(semesterId))
        .map(exam -> exam.getCourses().getId())
        .collect(Collectors.toSet());
  }

  private BigDecimal weightedAverage(List<JNote> notes) {
    BigDecimal weightedSum = BigDecimal.ZERO;
    BigDecimal totalCoefficient = BigDecimal.ZERO;

    for (JNote note : notes) {
      JExam exam = note.getExam();
      weightedSum =
          weightedSum.add(exam.getCoefficient().multiply(BigDecimal.valueOf(note.getValue())));
      totalCoefficient = totalCoefficient.add(exam.getCoefficient());
    }

    return totalCoefficient.signum() == 0
        ? BigDecimal.ZERO
        : weightedSum.divide(totalCoefficient, 2, RoundingMode.HALF_UP);
  }

  private BigDecimal creditWeightedCourseAverage(List<CourseAverage> courseAverages) {
    BigDecimal weightedSum = BigDecimal.ZERO;
    int totalCredits = 0;

    for (CourseAverage courseAverage : courseAverages) {
      weightedSum =
          weightedSum.add(
              courseAverage.average().multiply(BigDecimal.valueOf(courseAverage.credit())));
      totalCredits += courseAverage.credit();
    }

    return totalCredits == 0
        ? BigDecimal.ZERO
        : weightedSum.divide(BigDecimal.valueOf(totalCredits), 2, RoundingMode.HALF_UP);
  }

  private BigDecimal creditWeightedAverage(List<SemesterTranscript> semesters) {
    BigDecimal weightedSum = BigDecimal.ZERO;
    int totalCredits = 0;

    for (SemesterTranscript semester : semesters) {
      weightedSum =
          weightedSum.add(semester.average().multiply(BigDecimal.valueOf(semester.totalCredits())));
      totalCredits += semester.totalCredits();
    }

    return totalCredits == 0
        ? BigDecimal.ZERO
        : weightedSum.divide(BigDecimal.valueOf(totalCredits), 2, RoundingMode.HALF_UP);
  }

  private record SemesterSummaryKey(UUID semesterId, SemesterEnum semesterEnum) {}
}
