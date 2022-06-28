package com.db.cloud.school.bexevents.services;

import com.db.cloud.school.bexevents.exceptions.InvalidSignUpException;
import com.db.cloud.school.bexevents.payload.UserSignupRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void checkRegisterRequest(UserSignupRequest signUpRequest) {
        if (signUpRequest.getEmail() == null ||
        signUpRequest.getPassword() == null ||
        signUpRequest.getFirstName() == null ||
        signUpRequest.getLastName() == null ||
        signUpRequest.getMatchingPassword() == null)
            throw new InvalidSignUpException("One or more of the required fields are empty!");

        if (signUpRequest.getFirstName().length() < 3 || signUpRequest.getLastName().length() < 3)
            throw new InvalidSignUpException("First/Last name is too short!");

        if (signUpRequest.getPassword().length() < 6)
            throw new InvalidSignUpException("Password is too short!");

        if (signUpRequest.getPassword().contains(" "))
            throw new InvalidSignUpException("Password contains whitespaces!");

        if (!signUpRequest.getPassword().equals(signUpRequest.getMatchingPassword()))
            throw new InvalidSignUpException("Passwords do not match!");

        if(signUpRequest.getEmail().contains(" "))
            throw new InvalidSignUpException("Email contains whitespaces!");

        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        if(!signUpRequest.getEmail().matches(regex))
            throw new InvalidSignUpException("Invalid email address format!");
    }
}
