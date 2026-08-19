package com.hei.course.endpoint.event.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TranscriptEmailRequestedTest {

  @Test
  void builder_sets_student_id() {
    UUID studentId = UUID.randomUUID();

    TranscriptEmailRequested event =
        TranscriptEmailRequested.builder().studentId(studentId).build();

    assertThat(event.getStudentId()).isEqualTo(studentId);
  }

  @Test
  void exposes_consumer_durations() {
    TranscriptEmailRequested event =
        TranscriptEmailRequested.builder().studentId(UUID.randomUUID()).build();

    assertThat(event.maxConsumerDuration()).isEqualTo(Duration.ofSeconds(60));
    assertThat(event.maxConsumerBackoffBetweenRetries()).isEqualTo(Duration.ofSeconds(30));
  }

  @Test
  void no_arg_constructor_builds_empty_event() {
    TranscriptEmailRequested event = new TranscriptEmailRequested();

    assertThat(event.getStudentId()).isNull();
  }

  @Test
  void all_args_constructor_sets_student_id() {
    UUID studentId = UUID.randomUUID();

    TranscriptEmailRequested event = new TranscriptEmailRequested(studentId);

    assertThat(event.getStudentId()).isEqualTo(studentId);
  }

  @Test
  void equals_compares_student_id() {
    UUID studentId = UUID.randomUUID();
    TranscriptEmailRequested a = TranscriptEmailRequested.builder().studentId(studentId).build();
    TranscriptEmailRequested b = TranscriptEmailRequested.builder().studentId(studentId).build();

    assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
  }
}
