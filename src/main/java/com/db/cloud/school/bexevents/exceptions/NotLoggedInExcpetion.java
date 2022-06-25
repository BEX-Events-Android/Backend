package com.db.cloud.school.bexevents.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotLoggedInExcpetion extends IllegalArgumentException {
    private String message;

    public NotLoggedInExcpetion(String message) {
        super(message);
        this.message = message;
    }
}
