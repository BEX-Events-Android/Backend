package com.db.cloud.school.bexevents.controllers;

import com.db.cloud.school.bexevents.models.Event;
import com.db.cloud.school.bexevents.models.NewEventRequest;
import com.db.cloud.school.bexevents.models.User;
import com.db.cloud.school.bexevents.repositories.EventRepository;
import com.db.cloud.school.bexevents.repositories.UserRepository;
import com.db.cloud.school.bexevents.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EventController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventService eventService;

    @GetMapping("/events/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable("id") int id) {
        Event event = eventRepository.findById(id);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = new ArrayList<>(eventRepository.findAll());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PostMapping("/events")
    public ResponseEntity<Event> addEvent(@RequestBody NewEventRequest event) {
        eventService.checkMandatoryData(event);
        String duration = eventService.getDuration(event.getStartDateTime(), event.getEndDateTime());
        User organiser = userRepository.findByEmail(event.getOrganiserEmail()).get();

        Event savedEvent = new Event(event, organiser, duration);
        eventRepository.save(savedEvent);
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable("id") int id) {
        Event event = eventRepository.findById(id);
        eventRepository.delete(event);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

}
