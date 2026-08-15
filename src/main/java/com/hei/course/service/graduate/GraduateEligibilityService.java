package com.hei.course.service.graduate;

import com.hei.course.entity.JAffectation;
import com.hei.course.entity.JExam;
import com.hei.course.entity.JNote;
import com.hei.course.entity.JStudent;
import com.hei.course.repository.JAffectationRepository;
import com.hei.course.repository.JNoteRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GraduateEligibilityService {

  private static final double PASSING_GRADE = 10.0;

  private final JAffectationRepository affectationRepository;
  private final JNoteRepository noteRepository;

  public List<JStudent> findGraduates(UUID promotionId) {
    return studentsOfPromotion(promotionId).stream()
        .filter(this::isEligibleForGraduation)
        .sorted(Comparator.comparing(JStudent::getLastName).thenComparing(JStudent::getFirstName))
        .toList();
  }

  private List<JStudent> studentsOfPromotion(UUID promotionId) {
    return affectationRepository.findByGroup_Promotion_Id(promotionId).stream()
        .map(JAffectation::getStudent)
        .distinct()
        .toList();
  }

  private boolean isEligibleForGraduation(JStudent student) {
    List<JNote> notes = noteRepository.findByStudent_Id(student.getId());
    if (notes.isEmpty()) {
      return false;
    }

    Map<UUID, List<JNote>> notesByCourse =
        notes.stream().collect(Collectors.groupingBy(note -> note.getExam().getCourses().getId()));

    return notesByCourse.values().stream().allMatch(this::courseAverageIsPassing);
  }

  private boolean courseAverageIsPassing(List<JNote> notesForCourse) {
    BigDecimal weightedSum = BigDecimal.ZERO;
    BigDecimal totalCoefficient = BigDecimal.ZERO;

    for (JNote note : notesForCourse) {
      JExam exam = note.getExam();
      BigDecimal coefficient = exam.getCoefficient();
      weightedSum = weightedSum.add(coefficient.multiply(BigDecimal.valueOf(note.getValue())));
      totalCoefficient = totalCoefficient.add(coefficient);
    }

    if (totalCoefficient.signum() == 0) {
      return false;
    }

    double average =
        weightedSum.divide(totalCoefficient, 4, java.math.RoundingMode.HALF_UP).doubleValue();
    return average >= PASSING_GRADE;
  }
}
