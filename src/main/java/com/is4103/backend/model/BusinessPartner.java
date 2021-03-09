package com.is4103.backend.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

// import org.hibernate.mapping.Set;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity

public class BusinessPartner extends User {

    @Column(nullable = true)
    private String businessCategory;

    @Column(nullable = true)
    @ElementCollection(targetClass = Event.class)
    private List<Event> favouriteEventList;


    @JsonIgnore
    @Column(nullable = true)
    @ManyToMany
    private List<Attendee> attendeeFollowers;

    @JsonIgnore
    @Column(nullable = true)
    @ManyToMany(mappedBy="businessPartnerFollowers")
    private List<EventOrganiser> followEventOrganisers;

    @Transient
    @OneToMany(mappedBy = "businessPartner")
    @ElementCollection(targetClass = EventBoothTransaction.class)
    private List<EventBoothTransaction> eventBoothTransactions;

    @OneToMany(mappedBy = "businessPartner")
    @ElementCollection(targetClass = Enquiry.class)
    private List<Enquiry> enquiries;

    public BusinessPartner() {

    }
    
    public BusinessPartner(String businessCategory, List<Event> favouriteEventList, List<EventBoothTransaction> eventBoothTransactions,List<EventOrganiser> followEventOrganisers, List<Attendee> attendeeFollowers ) {
        super();
        this.businessCategory = businessCategory;
        this.favouriteEventList = favouriteEventList;
        this.eventBoothTransactions = eventBoothTransactions;
        this.followEventOrganisers = followEventOrganisers;
        this.attendeeFollowers = attendeeFollowers;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public List<Event> getFavouriteEventList() {
        return favouriteEventList;
    }

    public void setFavouriteEventList(List<Event> favouriteEventList) {
        this.favouriteEventList = favouriteEventList;
    }

    public List<Attendee> getAttendeeFollowers() {
        return attendeeFollowers;
    }

    public void setAttendeeFollowers(List<Attendee> attendeeFollowers) {
        this.attendeeFollowers = attendeeFollowers;
    }

    public List<EventBoothTransaction> getEventBoothTransactions() {
        return eventBoothTransactions;
    }

    public void setEventBoothTransactions(List<EventBoothTransaction> eventBoothTransactions) {
        this.eventBoothTransactions = eventBoothTransactions;
    }

    public List<EventOrganiser> getFollowEventOrganisers() {
        return followEventOrganisers;
    }

    public void setFollowEventOrganisers(List<EventOrganiser> followEventOrganisers) {
        this.followEventOrganisers = followEventOrganisers;
    }
}
