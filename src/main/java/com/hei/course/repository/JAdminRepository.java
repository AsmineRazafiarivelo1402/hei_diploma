package com.hei.course.repository;

import com.hei.course.entity.JAdmin;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JAdminRepository extends JpaRepository<JAdmin, UUID> {}
