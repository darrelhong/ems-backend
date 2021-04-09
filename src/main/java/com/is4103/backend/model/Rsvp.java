package com.is4103.backend.model;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.NoArgsConstructor;
import lombok.Data;

@Entity
@Data
@NoArgsConstructor
public class Rsvp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @ElementCollection(targetClass = BusinessPartner.class)
    @JsonIgnoreProperties({ "businessCategory", "products", "events", "sellerProfiles", "favouriteEventList", "attendeeFollowers",
    "followEventOrganisers", "sellerApplications", "enquiries" })
    private BusinessPartner businessPartner;

    @ManyToOne
    @JsonIgnoreProperties({ "sellerProfiles", "eventOrganiser", "favouriteBusinessPartners", "sellerApplications",
            "ticketTransactions", "products" })
    private Event event;

}