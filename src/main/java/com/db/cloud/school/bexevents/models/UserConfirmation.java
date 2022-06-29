package com.db.cloud.school.bexevents.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserConfirmation {
    private User user;
    private UUID code;

    public UserConfirmation(User user) {
        this.user = user;
        code = UUID.randomUUID();
    }
}
