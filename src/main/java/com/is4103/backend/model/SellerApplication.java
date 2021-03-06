package com.is4103.backend.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Data
@JsonView(EventViews.Public.class)
public class SellerApplication {

    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private long id;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

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

    private LocalDateTime applicationDate;

    private LocalDateTime paymentDate;

    public String generateUniqueKey() {
        return "" + businessPartner.getId() + "_" + event.getEid();
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "sellerApplication")
    @JsonIgnoreProperties({ "sellerApplication", "products", "event" })
    private List<Booth> booths;

    // @OneToOne(targetEntity = EventBoothTransaction.class, fetch =
    // FetchType.EAGER)
    // private EventBoothTransaction boothTransaction;

    // instead of a transaction entity we just add:
    // 1. paymentStatus
    // 2. stripePaymentId
}
