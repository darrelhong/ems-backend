package com.is4103.backend.model;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

@Entity

public class BusinessPartner extends User {

    @Column(nullable = false)
    private String businessCategory;

    @Column(nullable = true)
    @ElementCollection(targetClass = Event.class)
    private List<Event> favouriteEventList;

    @Column(nullable = true)
    @ElementCollection(targetClass = Attendee.class)
    private List<Attendee> attendeeFollowers;

    public BusinessPartner(String businessCategory, List<Event> favouriteEventList) {
        super();
        this.businessCategory = businessCategory;
        this.favouriteEventList = favouriteEventList;
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
}
