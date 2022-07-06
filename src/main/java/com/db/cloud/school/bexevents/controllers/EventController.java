package com.db.cloud.school.bexevents.controllers;

import com.db.cloud.school.bexevents.exceptions.EmailNotFoundException;
import com.db.cloud.school.bexevents.exceptions.EventNotFoundException;
import com.db.cloud.school.bexevents.exceptions.UnauthorizedException;
import com.db.cloud.school.bexevents.models.*;
import com.db.cloud.school.bexevents.repositories.EventRepository;
import com.db.cloud.school.bexevents.repositories.UserRepository;
import com.db.cloud.school.bexevents.security.jwt.JwtUtils;
import com.db.cloud.school.bexevents.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/events")
public class EventController {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private EventService eventService;
    private JwtUtils jwtUtils;

    public EventController(EventRepository eventRepository, UserRepository userRepository, EventService eventService, JwtUtils jwtUtils) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventService = eventService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable("id") int id, HttpServletRequest httpServletRequest) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isEmpty())
            throw new EventNotFoundException("Event not found!");
        Event event = eventOptional.get();
        boolean isAttending = eventService.checkIfUserAttends(id, httpServletRequest);
        EventResponse eventResponse = new EventResponse(event, isAttending);
        return new ResponseEntity<>(eventResponse, HttpStatus.OK);
    }

    private ResponseEntity<List<EventResponse>> findNoFilter(List<Event> events,
                                                             List<EventResponse> eventResponses,
                                                             HttpServletRequest httpServletRequest) {
        boolean isAttending;
        for (Event event : events) {
            isAttending = eventService.checkIfUserAttends(event.getId(), httpServletRequest);
            eventResponses.add(new EventResponse(event, isAttending));
        }
        return new ResponseEntity<>(eventResponses, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllFilteredEvents(@RequestParam(required = false) String date,
                                                                @RequestParam(required = false) List<String> location,
                                                                HttpServletRequest httpServletRequest) {
        List<Event> events = new ArrayList<>(eventRepository.findAll());
        List<EventResponse> eventResponses = new ArrayList<>();
        boolean isAttending;
        if (date == null && location == null) {
            findNoFilter(events, eventResponses, httpServletRequest);
        }
        else if (date != null && location != null) {
            for (Event event : events) {
                String startDateTime = event.getStartDateTime();
                String[] info = startDateTime.split(" ", 2);
                if(date.equals(info[0]) && location.contains(event.getLocation())){
                    isAttending = eventService.checkIfUserAttends(event.getId(), httpServletRequest);
                    eventResponses.add(new EventResponse(event, isAttending));
                }
            }
            return new ResponseEntity<>(eventResponses, HttpStatus.OK);
        }
       else if(date != null) {
            for (Event event : events) {
                String startDateTime = event.getStartDateTime();
                String[] info = startDateTime.split(" ", 2);
                if(date.equals(info[0])){
                    isAttending = eventService.checkIfUserAttends(event.getId(), httpServletRequest);
                    eventResponses.add(new EventResponse(event, isAttending));
                }
            }
            return new ResponseEntity<>(eventResponses, HttpStatus.OK);
        } else {
            for (Event event : events) {
                if (location.contains(event.getLocation())) {
                    isAttending = eventService.checkIfUserAttends(event.getId(), httpServletRequest);
                    eventResponses.add(new EventResponse(event, isAttending));
                }
            }
            return new ResponseEntity<>(eventResponses, HttpStatus.OK);
        }
       return null; // TODO refactor
    }
    
    @PostMapping
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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable("id") int eventId, HttpServletRequest httpServletRequest) {
        int userId = jwtUtils.getUserFromCookie(httpServletRequest).getId();
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EventNotFoundException("Event not found!");
        }

        int organiserId = event.get().getOrganiser().getId();
        if (userId != organiserId) {
            throw new UnauthorizedException("User is not authorized!");
        }

        eventRepository.delete(event.get());
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

    @PostMapping("/{id}/booking")
    public ResponseEntity<String> bookEvent(@PathVariable("id") int eventId, HttpServletRequest httpServletRequest) {
        User user = jwtUtils.getUserFromCookie(httpServletRequest);
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty())
            throw new EventNotFoundException("Event not found!");
        if (event.get().getAttendees().contains(user) && user.getAttendsEvent().contains(event.get())) {
            return new ResponseEntity<String>("You have already booked a seat for this event", HttpStatus.NOT_ACCEPTABLE);
        }
        if (user.getEmail().equals(event.get().getOrganiser().getEmail())) {
            return new ResponseEntity<String>("You can't book a seat to this event, because you are the organiser", HttpStatus.NOT_ACCEPTABLE);
        }
        event.get().getAttendees().add(user);
        user.getAttendsEvent().add(event.get());
        userRepository.save(user);
        eventRepository.save(event.get());

        eventService.sendConfirmationBookingEmail(user.getEmail(), event.get().getName());

        return new ResponseEntity<String>("The booking was a success", HttpStatus.OK);
    }


    @GetMapping("/locations")
    public ResponseEntity<Set<String>> getLocations (HttpServletRequest httpServletRequest) {
        User user = jwtUtils.getUserFromCookie(httpServletRequest);
        Set<String> eventLocations = eventService.getLocations();
        return new ResponseEntity<>(eventLocations, HttpStatus.OK);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<String> addCommentToEvent(@PathVariable("id") int id,
                                                    HttpServletRequest httpServletRequest,
                                                    @RequestBody String comment) {
        User user = jwtUtils.getUserFromCookie(httpServletRequest);
        Optional<Event> event = eventRepository.findById(id);
        eventService.checkMandatoryDataForComment(comment);
        event.get().getComments().add(new Comment(user, comment));
        eventRepository.save(event.get());
        return new ResponseEntity<>("Your comment was successfully posted to" + " " + event.get().getName() + " event", HttpStatus.OK);
    }
}
