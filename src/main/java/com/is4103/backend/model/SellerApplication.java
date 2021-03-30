package com.is4103.backend.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Entity
@Data
@JsonView(EventViews.Public.class)
public class SellerApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // @ManyToOne
    // @JsonIgnoreProperties({ "events", "approved", "approvalMessage",
    // "supportDocsUrl", "vipList", "attendeeFollowers",
    // "businessPartnerFollowers", "enquiries", "description", "profilePic",
    // "email", "enabled", "phonenumber",
    // "address", "roles", "notifications" })
    // private EventOrganiser eventOrganiser;

    @ManyToOne
    @JsonIgnoreProperties({ "events", "products", "sellerProfiles", "favouriteEventList", "attendeeFollowers",
            "followEventOrganisers", "sellerApplications", "enquiries" })
    // @JsonIgnoreProperties("sellerApplications")
    private BusinessPartner businessPartner;

    @ManyToOne
    @JsonIgnoreProperties({ "sellerProfiles", "eventOrganiser", "favouriteBusinessPartners", "sellerApplications",
            "ticketTransactions", "products" })
    private Event event;

    private String description;

    private String comments;

    private int boothQuantity;

    @Enumerated(EnumType.STRING)
    private SellerApplicationStatus sellerApplicationStatus;

    private String stripePaymentId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // @OneToOne(targetEntity = EventBoothTransaction.class, fetch =
    // FetchType.EAGER)
    // private EventBoothTransaction boothTransaction;

    // instead of a transaction entity we just add:
    // 1. paymentStatus
    // 2. stripePaymentId
}
