package com.db.cloud.school.bexevents.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private List<Image> images;
    private String organiserEmail;
}
