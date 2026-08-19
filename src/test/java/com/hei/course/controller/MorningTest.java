package com.hei.course.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MorningTest {

  @Test
  void returns_good_morning() {
    Morning morning = new Morning();

    assertThat(morning.morning()).isEqualTo("Good morning");
  }
}
