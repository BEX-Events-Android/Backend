package com.db.cloud.school.bexevents.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidSignUpException extends RuntimeException {
    private String message;

    public InvalidSignUpException(String message) {
        super(message);
        this.message = message;
    }
}
