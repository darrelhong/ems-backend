package com.is4103.backend.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data

public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int rating;
    
    @ManyToOne
    private Attendee attendee;

    @ManyToOne
    private BusinessPartner partner;

    private String reviewText;

    @ManyToOne
    private Event event;

    private String reviewDateTime;
}
