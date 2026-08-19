package com.hei.course.service.event;

import static org.mockito.Mockito.verify;

import com.hei.course.endpoint.event.model.TranscriptEmailRequested;
import com.hei.course.service.transcript.TranscriptService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranscriptEmailRequestedServiceTest {

  @Mock private TranscriptService transcriptService;

  private TranscriptEmailRequestedService service;

  @BeforeEach
  void setUp() {
    service = new TranscriptEmailRequestedService(transcriptService);
  }

  @Test
  void sends_transcript_by_email_when_event_is_consumed() {
    UUID studentId = UUID.randomUUID();
    TranscriptEmailRequested event =
        TranscriptEmailRequested.builder().studentId(studentId).build();

    service.accept(event);

    verify(transcriptService).sendTranscriptByEmail(studentId);
  }
}
