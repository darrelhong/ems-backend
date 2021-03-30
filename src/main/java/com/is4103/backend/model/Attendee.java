package com.is4103.backend.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Attendee extends User {


    @Column(nullable = true)
    @ElementCollection(targetClass = String.class)
    private List<String> categoryPreferences;

    @Column(nullable = true)
    @JsonIgnore
    @ManyToMany
    private List<EventOrganiser> followedEventOrganisers = new ArrayList<>();

    @OneToMany(mappedBy = "attendee")
    private List<TicketTransaction> ticketTransactions;

    //  @Transient
     @JsonIgnore
     @Column(nullable = true)
    @OneToMany(mappedBy = "attendee")
    // @ElementCollection(targetClass = Review.class)
    private List<Review> reviews;



    @JsonIgnore
    @ManyToMany
    private List<BusinessPartner> followedBusinessPartners;

    @ManyToMany
    @JoinColumn
    @JsonIgnore
    private List<Event> favouriteEvents;

    public Attendee() {
        super();
    }


    public Attendee(List<String> categoryPreferences, List<EventOrganiser> followedEventOrgs, List<BusinessPartner> followedBusinessPartners) {
       this();
        this.categoryPreferences = categoryPreferences;
        this.followedEventOrganisers = followedEventOrgs;
        this.followedBusinessPartners = followedBusinessPartners;
    }

    public List<String> getCategoryPreferences() {
        return categoryPreferences;
    }

    public void setCategoryPreferences(List<String> categoryPreferences) {
        this.categoryPreferences = categoryPreferences;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<EventOrganiser> getFollowedEventOrgs() {
        return followedEventOrganisers;
    }

    public void setFollowedEventOrgs(List<EventOrganiser> followedEventOrgs) {
        this.followedEventOrganisers = followedEventOrgs;
    }

    public List<BusinessPartner> getFollowedBusinessPartners() {
        return followedBusinessPartners;
    }

    public void setFollowedBusinessPartners(List<BusinessPartner> followedBusinessPartners) {
        this.followedBusinessPartners = followedBusinessPartners;
    }

}
