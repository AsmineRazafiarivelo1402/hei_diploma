package com.hei.course.service.transcript;

import com.hei.course.model.SemesterEnum;
import java.math.BigDecimal;
import java.util.List;

public class TranscriptData {

    public record CourseAverage(String courseTitle, int credit, BigDecimal average) {}

    public enum Status {
        COMPLETE,
        PROVISIONAL
    }

    public record SemesterTranscript(
            SemesterEnum semester,
            List<CourseAverage> courseAverages,
            int totalCredits,
            BigDecimal average,
            Status status) {}

    public record Transcript(
            List<SemesterTranscript> semesters, int totalCredits, BigDecimal generalAverage) {}
}