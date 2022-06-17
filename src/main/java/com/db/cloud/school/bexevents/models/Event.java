package com.db.cloud.school.bexevents.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "startDateTime")
    private String startDateTime;

    @Column(name = "endDateTime")
    private String endDateTime;

    // TO DO: calculate duration based on start/end date
    private String duration;

    @Column(name = "location")
    private String location;

    @Column(name = "description")
    private String description;

    // an event's organiser can organise other events as well
    // thus a manyToOne relation has been chosen
    @ManyToOne
    @JoinColumn(name = "organiser")
    private User organiser;

    // an event can host multiple users
    // thus a oneToMany relation has been chosen
    @OneToMany
    @Column(name = "attendees")
    private List<User> attendees;

    @Column(name = "isAttendingEvent")
    private boolean isAttendingEvent;

    public Event(NewEventRequest newEventRequest, User organiser, String duration) {
        name = newEventRequest.getName();
        startDateTime = newEventRequest.getStartDateTime();
        endDateTime = newEventRequest.getEndDateTime();
        location = newEventRequest.getLocation();
        description = newEventRequest.getDescription();
        this.duration = duration;
        this.organiser = organiser;
        attendees = new ArrayList<>();
        isAttendingEvent = false;
    }
}
