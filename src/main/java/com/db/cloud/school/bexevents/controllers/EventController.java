package com.db.cloud.school.bexevents.controllers;

import com.db.cloud.school.bexevents.exceptions.EventNotFoundException;
import com.db.cloud.school.bexevents.models.Event;
import com.db.cloud.school.bexevents.models.EventResponse;
import com.db.cloud.school.bexevents.models.NewEventRequest;
import com.db.cloud.school.bexevents.models.User;
import com.db.cloud.school.bexevents.repositories.EventRepository;
import com.db.cloud.school.bexevents.repositories.UserRepository;
import com.db.cloud.school.bexevents.security.jwt.JwtUtils;
import com.db.cloud.school.bexevents.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class EventController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventService eventService;

    @Autowired
    JwtUtils jwtUtils;


    @GetMapping("/events/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable("id") int id, HttpServletRequest httpServletRequest) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isEmpty())
            throw new EventNotFoundException("Event not found!");
        Event event = eventOptional.get();
        boolean isAttending = eventService.checkIfUserAttends(id, httpServletRequest);
        EventResponse eventResponse = new EventResponse(event, isAttending);
        return new ResponseEntity<>(eventResponse, HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> getAllEvents(HttpServletRequest httpServletRequest) {
        List<Event> events = new ArrayList<>(eventRepository.findAll());
        List<EventResponse> eventResponses = new ArrayList<>();
        boolean isAttending;
        for (Event event : events) {
            isAttending = eventService.checkIfUserAttends(event.getId(), httpServletRequest);
            eventResponses.add(new EventResponse(event, isAttending));
        }
        return new ResponseEntity<>(eventResponses, HttpStatus.OK);
    }

    @PostMapping("/events")
    public ResponseEntity<Event> addEvent(@RequestBody NewEventRequest event) {
        eventService.checkMandatoryData(event);
        String duration = eventService.getDuration(event.getStartDateTime(), event.getEndDateTime());
        User organiser = userRepository.findByEmail(event.getOrganiserEmail()).get();
        Event savedEvent = new Event(event, organiser, duration);
        eventRepository.save(savedEvent);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable("id") int id) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isEmpty())
            throw new EventNotFoundException("Event not found!");
        eventRepository.delete(eventOptional.get());
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

    @PostMapping("/events/{id}/booking")
    public ResponseEntity<String> bookEvent(@PathVariable("id") int id, HttpServletRequest httpServletRequest) {
        String token = jwtUtils.getJwtFromCookies(httpServletRequest);
        jwtUtils.validateJwtToken(token);
        String email = jwtUtils.getEmailFromJwtToken(token);
        Optional<User> user = userRepository.findByEmail(email);
        Optional<Event> event = eventRepository.findById(id);
        event.get().getAttendees().add(user.get());
        user.get().getAttendsEvent().add(event.get());
        userRepository.save(user.get());
        eventRepository.save(event.get());
        return new ResponseEntity<String>("The booking was a success", HttpStatus.OK);
    }
}
