package com.hei.course.repository;

import com.hei.course.entity.JUsers;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JUsersRepository extends JpaRepository<JUsers, UUID> {

  Optional<JUsers> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByReference(String reference);
}
