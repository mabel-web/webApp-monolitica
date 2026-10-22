package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByRunAndIdNot(String run, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByRun(String run);

    boolean existsByEmail(String email);

}
