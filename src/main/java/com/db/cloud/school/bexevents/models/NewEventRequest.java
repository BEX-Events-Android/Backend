package com.db.cloud.school.bexevents.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewEventRequest {
    private String name;
    private String startDateTime;
    private String endDateTime;
    private String location;
    private String description;
    private String organiserEmail;
}
