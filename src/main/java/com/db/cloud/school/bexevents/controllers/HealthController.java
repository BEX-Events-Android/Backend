package com.db.cloud.school.bexevents.controllers;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> health() throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body("I am ok!!!");
    }
}

