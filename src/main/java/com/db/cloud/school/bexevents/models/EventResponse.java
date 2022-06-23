package com.db.cloud.school.bexevents.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class EventResponse {
    private Integer id;
    private String name;
    private String startDateTime;
    private String endDateTime;
    private String duration;
    private String description;
    private UserInfoResponse organiser;
    private List<UserInfoResponse> attendees;
    private boolean isAttendingEvent;

    public EventResponse(Event event) {
        id = event.getId();
        name = event.getName();
        startDateTime = event.getStartDateTime();
        endDateTime = event.getEndDateTime();
        duration = event.getDuration();
        description = event.getDescription();
        organiser = new UserInfoResponse(event.getOrganiser());
        attendees = new ArrayList<>();
        for (User attendee : event.getAttendees()) {
            attendees.add(new UserInfoResponse(attendee));
        }
        isAttendingEvent = false; // TODO implement logic by jwt
    }
}
