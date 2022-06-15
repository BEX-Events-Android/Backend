package com.db.cloud.school.bexevents.repositories;

import com.db.cloud.school.bexevents.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByFirstName(String firstName);
}
