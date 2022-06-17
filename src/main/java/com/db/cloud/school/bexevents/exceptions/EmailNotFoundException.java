package com.db.cloud.school.bexevents.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmailNotFoundException extends RuntimeException {
    private String message;

    public EmailNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
