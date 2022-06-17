package com.db.cloud.school.bexevents.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidEventDataException extends RuntimeException {
    private String message;

    public InvalidEventDataException(String message) {
        super(message);
        this.message = message;
    }
}
