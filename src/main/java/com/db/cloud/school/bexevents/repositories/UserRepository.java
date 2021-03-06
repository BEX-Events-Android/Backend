package com.db.cloud.school.bexevents.repositories;

import com.db.cloud.school.bexevents.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByFirstName(String firstName);

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}
