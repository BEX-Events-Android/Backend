package com.db.cloud.school.bexevents.services;

import com.db.cloud.school.bexevents.models.NewEventRequest;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface IEventService {
    default String getDuration(String startDate, String endDate) {
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
    void checkMandatoryData(NewEventRequest event);
    void checkMandatoryDataForComment(String comment);
    boolean checkIfUserAttends(int eventId, HttpServletRequest httpServletRequest);
    void sendConfirmationBookingEmail(String toEmail, String eventName);
    Set<String> getLocations();
}
