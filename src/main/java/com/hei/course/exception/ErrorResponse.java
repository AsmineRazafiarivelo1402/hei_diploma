package com.hei.course.exception;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

  private final Instant timestamp;
  private final int status;
  private final String error;
  private final String message;
  private final String path;
}
