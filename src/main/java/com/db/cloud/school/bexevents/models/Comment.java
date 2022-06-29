package com.db.cloud.school.bexevents.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "user")
    private String email;

    @Column(name = "datePosted")
    private String datePosted;

    @Column(name = "textBox")
    private String textBox;

    @ManyToOne
    @Column(name = "event")
    private Event event;

    public Comment(User user, String textBox, Event event) {
        this.email = user.getEmail();
        // TODO generate date posted when comment is created
        this.textBox = textBox;
        this.event = event;
    }
}
