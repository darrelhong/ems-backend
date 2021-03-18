package com.is4103.backend.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Data
public class TicketTransaction {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne
    @JsonIgnoreProperties({ "eventOrganiser", "eventOrganiser", "eventBoothTransactions", "booths",
            "ticketTransactions", "favouriteBusinessPartners", "address", "descriptions", "ticketPrice",
            "ticketCapacity", "eventStartDate", "eventEndDate", "saleStartDate", "salesEndDate", "images",
            "boothCapacity", "rating", "eventStatus", "hidden", "sellingTicket", "physical", "vip", "published",
            "availableForSale" })
    private Event event;

    @ManyToOne
    @JsonIgnoreProperties({ "categoryPreferences", "followedEventOrganisers", "ticketTransactions",
            "followedBusinessPartners", "description", "enabled", "address", "roles", "notifications", "profilePic",
            "email", "phonenumber", "followedEventOrgs" })
    private Attendee attendee;

    public TicketTransaction() {
        this.paymentStatus = PaymentStatus.PENDING;
    }
}
