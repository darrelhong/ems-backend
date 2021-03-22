package com.is4103.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

import lombok.Getter;
import lombok.Setter;

@Entity

@Getter
@Setter
@Data

public class TicketTransaction {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
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

    @Column
    @NotNull
    private String stripePaymentId;

    @Column
    private LocalDateTime dateTimeOrdered;

    @PrePersist
    void onCreate() {
        dateTimeOrdered = LocalDateTime.now();
    }

    public TicketTransaction() {
        this.paymentStatus = PaymentStatus.PENDING;
    }
}
