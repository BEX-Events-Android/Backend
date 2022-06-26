package com.db.cloud.school.bexevents.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnauthorizedException extends IllegalArgumentException {
    private String message;

    public UnauthorizedException(String message) {
        super(message);
        this.message = message;
    }
}