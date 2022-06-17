package com.db.cloud.school.bexevents.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EventNotFoundException extends RuntimeException {
    private String message;

    public EventNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}