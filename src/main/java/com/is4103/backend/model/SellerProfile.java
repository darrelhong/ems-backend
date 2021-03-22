package com.is4103.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Entity
@Data
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @ElementCollection(targetClass = Event.class)
    @JsonIgnoreProperties({"sellerProfiles","eventOrganiser", "favouriteBusinessPartners","sellerApplications", "ticketTransactions", "products"})
    // @JsonIgnore
    private Event event;

    @ManyToOne
    @ElementCollection(targetClass = BusinessPartner.class)
    // @JsonIgnore
    @JsonIgnoreProperties({"products","events", "sellerProfiles", "favouriteEventList", "attendeeFollowers", "followEventOrganisers", "sellerApplications", "enquiries"})
    private BusinessPartner businessPartner;

    private String description;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "sellerProfile")
    // @JsonIgnoreProperties({"sellerProfile","products"})
    @JsonIgnore
    private List<Booth> booths;

    @ElementCollection(targetClass = String.class)
    private List<String> brochureImages;
    
}
