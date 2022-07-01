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
    private List<NewEventRequest> pastEvents = new ArrayList<>();
    private List<NewEventRequest> upcomingEvents = new ArrayList<>();

    public Profile(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }

    public void setPastEventsAndUpcomingEvents(User user) {
        LocalDate currentDate = LocalDate.now();

        for (Event currentEvent : user.getAttendsEvent()) {
            List<String> list = Arrays.stream(currentEvent.getStartDateTime().split("[ ,-]"))
                    .collect(Collectors.toCollection(ArrayList<String>::new));
            NewEventRequest event = new NewEventRequest(currentEvent.getName(), currentEvent.getStartDateTime(), currentEvent.getEndDateTime(),
                    currentEvent.getLocation(), currentEvent.getDescription(), currentEvent.getAssets(), currentEvent.getOrganiser().getEmail());
            if (Integer.parseInt(list.get(2)) < (currentDate.getYear() - 2000)) {
                this.pastEvents.add(event);
            } else if (Integer.parseInt(list.get(1)) < (currentDate.getMonthValue())
                    && Integer.parseInt(list.get(2)) == (currentDate.getYear() - 2000)) {
                this.pastEvents.add(event);
            } else if (Integer.parseInt(list.get(0)) <= (currentDate.getDayOfMonth())
                    && Integer.parseInt(list.get(1)) == currentDate.getMonthValue()
                    && Integer.parseInt(list.get(2)) == (currentDate.getYear() - 2000)) {
                this.pastEvents.add(event);
            } else this.upcomingEvents.add(event);
        }
    }
}
