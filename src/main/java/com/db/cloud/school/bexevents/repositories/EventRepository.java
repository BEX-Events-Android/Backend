package com.db.cloud.school.bexevents.repositories;

import com.db.cloud.school.bexevents.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Optional<Event> findById(int id);
}
