package com.db.cloud.school.bexevents.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class Profile {
    private String firstName;
    private String lastName;
    private String email;
    private List<EventResponse> pastEvents = new ArrayList<>();
    private List<EventResponse> upcomingEvents = new ArrayList<>();

    public Profile(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }

    public void setPastEventsAndUpcomingEvents(User user) {
        LocalDate currentDate = LocalDate.now();

        for (Event event : user.getAttendsEvent()) {
            List<String> list = Arrays.stream(event.getStartDateTime().split("[ ,-]"))
                    .collect(Collectors.toCollection(ArrayList<String>::new));
            if (Integer.parseInt(list.get(2)) < (currentDate.getYear() - 2000)) {
                this.pastEvents.add(new EventResponse(event, true));
            } else if (Integer.parseInt(list.get(1)) < (currentDate.getMonthValue())
                    && Integer.parseInt(list.get(2)) == (currentDate.getYear() - 2000)) {
                this.pastEvents.add(new EventResponse(event, true));
            } else if (Integer.parseInt(list.get(0)) <= (currentDate.getDayOfMonth())
                    && Integer.parseInt(list.get(1)) == currentDate.getMonthValue()
                    && Integer.parseInt(list.get(2)) == (currentDate.getYear() - 2000)) {
                this.pastEvents.add(new EventResponse(event, true));
            } else this.upcomingEvents.add(new EventResponse(event, true));
        }
    }
}
