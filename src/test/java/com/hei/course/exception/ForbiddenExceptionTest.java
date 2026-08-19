package com.hei.course.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ForbiddenExceptionTest {

  @Test
  void carries_message() {
    ForbiddenException exception = new ForbiddenException("Forbidden access");

    assertThat(exception).isInstanceOf(RuntimeException.class);
    assertThat(exception.getMessage()).isEqualTo("Forbidden access");
  }
}
