package com.is4103.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long eid;

    @ManyToOne
    private EventOrganiser eventOrganiser;

    @ManyToMany
    @ElementCollection(targetClass = BusinessPartner.class)
    private List<BusinessPartner> favouriteBusinessPartners;

    @OneToMany(mappedBy = "event")
    @ElementCollection(targetClass = EventBoothTransaction.class)
    private List<EventBoothTransaction> eventBoothTransactions;

    @OneToMany(mappedBy = "event")
    @ElementCollection(targetClass = Booth.class)
    private List<Booth> booths;

    @OneToMany(mappedBy = "event")
    @ElementCollection(targetClass = TicketTransaction.class)
    private List<TicketTransaction> ticketTransactions;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String descriptions;

    @Column(nullable = true)
    private String website;

    @Column(nullable = true)
    private float ticketPrice;

    @Column(nullable = true)
    private int ticketCapacity;

    @Column(nullable = false)
    private boolean isPhysical;

    @Column(nullable = false)
    private LocalDateTime eventStartDate;

    @Column(nullable = false)
    private LocalDateTime eventEndDate;

    @Column(nullable = true)
    private LocalDateTime saleStartDate;

    @Column(nullable = true)
    private LocalDateTime salesndDate;

    @Column(nullable = false)
    @ElementCollection(targetClass = String.class)
    private List<String> images;

    @Column(nullable = false)
    private int boothCapacity;

    @Column(nullable = true)
    private int rating;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;
}