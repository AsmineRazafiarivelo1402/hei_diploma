package com.hei.course.repository;

import com.hei.course.entity.JUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JUsersRepository extends JpaRepository<JUsers, UUID> {

    Optional<JUsers> findByEmail(String email);
}
