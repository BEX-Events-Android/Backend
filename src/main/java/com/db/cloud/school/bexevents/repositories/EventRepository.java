package com.db.cloud.school.bexevents.repositories;

import com.db.cloud.school.bexevents.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {

    Event findById(int id);
}
