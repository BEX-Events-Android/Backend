package com.db.cloud.school.bexevents.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidCommentException extends RuntimeException{
    private String message;

    public InvalidCommentException(String message) {
        super(message);
        this.message = message;
    }
}
