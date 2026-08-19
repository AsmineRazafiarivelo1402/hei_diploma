package com.hei.course.service.graduate;

import com.hei.course.entity.JStudent;
import java.math.BigDecimal;

public record GraduateRanking(int rank, JStudent student, BigDecimal average) {}
