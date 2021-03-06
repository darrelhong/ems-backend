package com.is4103.backend.model;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonView({ EventViews.Private.class, EventViews.Public.class })
public class EventOrganiser extends User {

    @Column(nullable = false)
    private boolean approved = false;

    private String approvalMessage;

    @Column(nullable = true)
    private String supportDocsUrl;

    @OneToMany
    private List<BusinessPartner> vipList;

    @JsonIgnore
    @Column(nullable = true)
    @ManyToMany
    private List<Attendee> attendeeFollowers;

    @JsonIgnore
    @Column(nullable = true)
    @ManyToMany
    private List<BusinessPartner> businessPartnerFollowers;

    @Transient
    @Column(nullable = true)
    @ElementCollection(targetClass = Event.class)
    @OneToMany(mappedBy = "eventOrganiser")
    @JsonIgnoreProperties("eventOrganiser")
    private List<Event> events;

    @OneToMany(mappedBy = "eventOrganiser")
    @ElementCollection(targetClass = Enquiry.class)
    private List<Enquiry> enquiries;

    public EventOrganiser() {
        super();
    }

    public EventOrganiser(List<Attendee> attendeeFollowers, List<BusinessPartner> businessPartnerFollowers) {
        this();
        this.attendeeFollowers = attendeeFollowers;
        this.businessPartnerFollowers = businessPartnerFollowers;

    }

    public List<Attendee> getAttendeeFollowers() {
        return attendeeFollowers;
    }

    public void setAttendeeFollowers(List<Attendee> attendeeFollowers) {
        this.attendeeFollowers = attendeeFollowers;
    }

    public List<BusinessPartner> getBusinessPartnerFollowers() {
        return businessPartnerFollowers;
    }

    public void setBusinessPartnerFollowers(List<BusinessPartner> businessPartnerFollowers) {
        this.businessPartnerFollowers = businessPartnerFollowers;
    }
}