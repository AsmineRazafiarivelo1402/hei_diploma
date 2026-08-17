package com.hei.course.service.event;

import com.hei.course.endpoint.event.model.TranscriptEmailRequested;
import com.hei.course.service.transcript.TranscriptService;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TranscriptEmailRequestedService implements Consumer<TranscriptEmailRequested> {

  private final TranscriptService transcriptService;

  @Override
  public void accept(TranscriptEmailRequested event) {
    transcriptService.sendTranscriptByEmail(event.getStudentId());
  }
}
