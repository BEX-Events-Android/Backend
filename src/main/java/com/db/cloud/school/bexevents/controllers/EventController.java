package com.db.cloud.school.bexevents.controllers;

import com.db.cloud.school.bexevents.models.Event;
import com.db.cloud.school.bexevents.repositories.EventRepository;
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
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        // TO DO: create new event request DTO that contains what's documented in event API
        // TO DO: validate event request DTO for mandatory data
        // TO DO: calculate duration from start/end date
        // TO DO: add check for organiser's email
        Event savedEvent = eventRepository.save(event);
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable("id") int id) {
        Event event = eventRepository.findById(id);
        eventRepository.delete(event);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

}
