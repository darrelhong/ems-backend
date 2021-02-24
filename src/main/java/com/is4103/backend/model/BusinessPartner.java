package com.is4103.backend.model;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import java.util.List;

@Entity

public class BusinessPartner extends User {

    @Column(nullable = true)
    private String businessCategory;

    @Column(nullable = true)
    @ElementCollection(targetClass = Event.class)
    private List<Event> favouriteEventList;

    @Column(nullable = true)
    @ElementCollection(targetClass = Attendee.class)
    @ManyToMany(mappedBy = "businessPartnerFollowers")
    private List<Attendee> attendeeFollowers;

    @Column(nullable = true)
    @ManyToMany
    @ElementCollection(targetClass = EventOrganiser.class)
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
    
    public BusinessPartner(String businessCategory, List<Event> favouriteEventList, List<EventBoothTransaction> eventBoothTransactions) {
        super();
        this.businessCategory = businessCategory;
        this.favouriteEventList = favouriteEventList;
        this.eventBoothTransactions = eventBoothTransactions;
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
}
