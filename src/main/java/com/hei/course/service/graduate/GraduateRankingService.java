package com.hei.course.service.graduate;

import com.hei.course.entity.JStudent;
import com.hei.course.service.transcript.TranscriptComputationService;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GraduateRankingService {

    private final GraduateEligibilityService graduateEligibilityService;
    private final TranscriptComputationService transcriptComputationService;

    /** Graduates of the promotion, ranked by descending general average (rank 1 = best average). */
    public List<GraduateRanking> rankGraduates(UUID promotionId) {
        var graduates = graduateEligibilityService.findGraduates(promotionId);

        List<Map.Entry<JStudent, BigDecimal>> studentsWithAverage =
                graduates.stream()
                        .map(
                                student ->
                                        Map.entry(
                                                student, transcriptComputationService.computeFor(student.getId()).generalAverage()))
                        .sorted(Comparator.<Map.Entry<JStudent, BigDecimal>>comparingDouble(entry -> entry.getValue().doubleValue())
                                .reversed())
                        .toList();

        return IntStream.range(0, studentsWithAverage.size())
                .mapToObj(
                        index -> {
                            var entry = studentsWithAverage.get(index);
                            return new GraduateRanking(index + 1, entry.getKey(), entry.getValue());
                        })
                .toList();
    }
}