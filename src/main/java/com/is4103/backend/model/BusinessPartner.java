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
import com.fasterxml.jackson.annotation.JsonView;

// import org.hibernate.mapping.Set;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@JsonView(EventViews.Private.class)
public class BusinessPartner extends User {

    @Column(nullable = true)
    private String businessCategory;

    @Column(nullable = true)
    @ElementCollection(targetClass = Event.class)
    private List<Event> favouriteEventList;

    @JsonIgnore
    @Column(nullable = true)
    @ElementCollection(targetClass = Attendee.class)
    @ManyToMany(cascade = CascadeType.MERGE)
    // @JoinTable(name="followed_BusinessPartners", joinColumns=
    // @JoinColumn(name="business_partner_id"), inverseJoinColumns =
    // @JoinColumn(name="attendee_id"))
    private Set<Attendee> attendeeFollowers = new HashSet<>();

    @JsonIgnore
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

    public BusinessPartner(String businessCategory, List<Event> favouriteEventList,
            List<EventBoothTransaction> eventBoothTransactions, List<EventOrganiser> followEventOrganisers) {
        super();
        this.businessCategory = businessCategory;
        this.favouriteEventList = favouriteEventList;
        this.eventBoothTransactions = eventBoothTransactions;
        this.followEventOrganisers = followEventOrganisers;
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

    public Set<Attendee> getAttendeeFollowers() {
        return attendeeFollowers;
    }

    public void setAttendeeFollowers(Set<Attendee> attendeeFollowers) {
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
