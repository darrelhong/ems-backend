package com.is4103.backend.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Entity
@Data
@JsonView(EventViews.Private.class)
public class TicketTransaction {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne
    @JsonView(EventViews.Basic.class)
    private Event event;

    @ManyToOne
    @JsonIgnoreProperties({ "categoryPreferences", "followedEventOrganisers", "ticketTransactions",
            "followedBusinessPartners", "description", "enabled", "address", "roles", "notifications" })
    private Attendee attendee;

    public TicketTransaction() {
        this.paymentStatus = PaymentStatus.PENDING;
    }
}
