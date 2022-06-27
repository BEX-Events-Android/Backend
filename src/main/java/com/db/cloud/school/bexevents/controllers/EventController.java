package com.db.cloud.school.bexevents.controllers;

import com.db.cloud.school.bexevents.exceptions.EmailNotFoundException;
import com.db.cloud.school.bexevents.exceptions.EventNotFoundException;
import com.db.cloud.school.bexevents.exceptions.UnauthorizedException;
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
import java.util.Objects;
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

    // TODO could be integrated with get all events with optional query param
    @GetMapping("/events/date/{startingDate}")
    public ResponseEntity<List<EventResponse>> getAllEventsByDate(@PathVariable("startingDate") String sD) {
        List<Event> events = new ArrayList<>(eventRepository.findAll());
        List<EventResponse> eventResponses = new ArrayList<>();

        for (Event event : events) {
            String startDateTime = event.getStartDateTime();
            String[] info = startDateTime.split(" ", 2);
            if(sD.equals(info[0])){
                eventResponses.add(new EventResponse(event));
            }
        }
        return new ResponseEntity<>(eventResponses, HttpStatus.OK);
    }
    @PostMapping("/events")
    public ResponseEntity<Event> addEvent(@RequestBody NewEventRequest event, HttpServletRequest httpServletRequest) {
        User user = jwtUtils.getUserFromCookie(httpServletRequest);
        eventService.checkMandatoryData(event);
        String duration = eventService.getDuration(event.getStartDateTime(), event.getEndDateTime());
        Optional<User> organiser = userRepository.findByEmail(event.getOrganiserEmail());
        if (organiser.isEmpty())
            throw new EmailNotFoundException("Organiser's email not found!");
        Event savedEvent = new Event(event, organiser.get(), duration);
        eventRepository.save(savedEvent);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable("id") int id, HttpServletRequest httpServletRequest) {
        User user = jwtUtils.getUserFromCookie(httpServletRequest);
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isEmpty())
            throw new EventNotFoundException("Event not found!");

        User organiser = eventOptional.get().getOrganiser();
        if (Objects.equals(user.getId(), organiser.getId()))
            eventRepository.delete(eventOptional.get());
        else
            throw new UnauthorizedException("User is not authorized!");
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

    @PostMapping("/events/{id}/booking")
    public ResponseEntity<String> bookEvent(@PathVariable("id") int id, HttpServletRequest httpServletRequest) {
        User user = jwtUtils.getUserFromCookie(httpServletRequest);
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty())
            throw new EventNotFoundException("Event not found!");
        event.get().getAttendees().add(user);
        user.getAttendsEvent().add(event.get());
        userRepository.save(user);
        eventRepository.save(event.get());
        return new ResponseEntity<String>("The booking was a success", HttpStatus.OK);
    }
}
