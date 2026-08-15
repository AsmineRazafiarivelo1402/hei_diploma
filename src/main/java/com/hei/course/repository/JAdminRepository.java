package com.hei.course.repository;

import com.hei.course.entity.JAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JAdminRepository extends JpaRepository<JAdmin, UUID> {
}
