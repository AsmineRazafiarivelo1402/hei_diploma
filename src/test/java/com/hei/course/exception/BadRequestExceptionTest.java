package com.hei.course.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {

  @Test
  void carries_message() {
    BadRequestException exception = new BadRequestException("Invalid input");

    assertThat(exception).isInstanceOf(RuntimeException.class);
    assertThat(exception.getMessage()).isEqualTo("Invalid input");
  }
}
