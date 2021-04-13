package com.is4103.backend.model;

import java.time.LocalDateTime;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private BusinessPartner partner;

    private String reviewText;

    @ManyToOne
    @JsonIgnore
    private Event event;

    private String reviewDateTime;
}
