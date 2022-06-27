package com.db.cloud.school.bexevents.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JWTException extends RuntimeException {
    private String message;

    public JWTException(String message) {
        super(message);
        this.message = message;
    }
}
