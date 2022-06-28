package com.db.cloud.school.bexevents.services;

import com.db.cloud.school.bexevents.models.Event;
import com.db.cloud.school.bexevents.models.NewEventRequest;
import com.db.cloud.school.bexevents.exceptions.EmailNotFoundException;
import com.db.cloud.school.bexevents.exceptions.InvalidEventDataException;
import com.db.cloud.school.bexevents.models.User;
import com.db.cloud.school.bexevents.repositories.EventRepository;
import com.db.cloud.school.bexevents.repositories.UserRepository;
import com.db.cloud.school.bexevents.security.jwt.JwtUtils;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    private void checkEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty())
            throw new EmailNotFoundException("Couldn't find an User with the specified email!");
    }

    private void checkDateFormat(String date) {
        // dd-mm-yy, hh:mm
        List<String> tokens = Collections.list(new StringTokenizer(date, " ")).stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());
        if (tokens.size() != 2)
            throw new InvalidEventDataException("Invalid date time format!");

        // dd, mm, yy
        List<String> dateTokens = Collections.list(new StringTokenizer(tokens.get(0), "-")).stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());

        if (dateTokens.size() != 3)
            throw new InvalidEventDataException("Invalid dd-mm-yy date format!");

        if (Integer.parseInt(dateTokens.get(0)) > 31 || Integer.parseInt(dateTokens.get(0)) < 1)
            throw new InvalidEventDataException("Invalid day!");

        if (Integer.parseInt(dateTokens.get(1)) > 12 || Integer.parseInt(dateTokens.get(1)) < 1)
            throw new InvalidEventDataException("Invalid month!");

        if (Integer.parseInt(dateTokens.get(2)) < 1)
            throw new InvalidEventDataException("Invalid year");

        // hh, mm
        List<String> timeTokens = Collections.list(new StringTokenizer(tokens.get(1), ":")).stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());

        if (timeTokens.size() != 2)
            throw new InvalidEventDataException("Invalid hh:mm time format!");

        if (Integer.parseInt(timeTokens.get(0)) > 23 || Integer.parseInt(timeTokens.get(0)) < 0)
            throw new InvalidEventDataException("Invalid hour!");

        if (Integer.parseInt(timeTokens.get(1)) > 59 || Integer.parseInt(timeTokens.get(1)) < 0)
            throw new InvalidEventDataException("Invalid minutes!");
    }

    public String getDuration(String startDate, String endDate) {
        String duration = "";
        long diff = 0;
        int weeks = 0, days = 0, hours = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.ENGLISH);
        try {
            Date sDate = sdf.parse(startDate);
            Date eDate = sdf.parse(endDate);

            long diffInMillies = Math.abs(eDate.getTime() - sDate.getTime());
            diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        while (diff - 168 > 0) {
            weeks++;
            diff -= 168;
        }

        if (weeks == 1)
            duration += "1 week ";
        else if (weeks > 1)
            duration += weeks + " weeks ";

        while (diff - 24 > 0) {
            days++;
            diff -= 24;
        }

        if (days == 1)
            duration += "1 day ";
        else if (days > 1)
            duration += days + " days ";

        if (diff == 1)
            duration += "1 hour";
        else if (diff > 1)
            duration += diff + " hours";
        return duration;
    }

    public void checkMandatoryData(NewEventRequest event) {
        if (event.getName() == null ||
        event.getStartDateTime() == null ||
        event.getEndDateTime() == null ||
        event.getLocation() == null ||
        event.getDescription() == null ||
        event.getOrganiserEmail() == null)
            throw new InvalidEventDataException("One of the event's data is null!");
        if (event.getName().isBlank() ||
        event.getStartDateTime().isBlank() ||
        event.getEndDateTime().isBlank() ||
        event.getLocation().isBlank() ||
        event.getOrganiserEmail().isBlank())
            throw new InvalidEventDataException("One of the event's data is empty!");

        checkEmail(event.getOrganiserEmail());
        checkDateFormat(event.getStartDateTime());
        checkDateFormat(event.getEndDateTime());
    }

    public boolean checkIfUserAttends(int id, HttpServletRequest httpServletRequest) {
        User user = jwtUtils.getUserFromCookie(httpServletRequest);
        Optional<Event> event = eventRepository.findById(id);
        for (User u : event.get().getAttendees()) {
            if (user.getEmail().equals(u.getEmail())) {
                return true;
            }
        }
        return false;
    }

    public void sendEmail(String toEmail, String eventName) {
        Email from = new Email("gb.stanescu01@gmail.com");
        String subject = "BEX Events";
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", "You have successfully booked a sit at " + eventName + " event!");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
