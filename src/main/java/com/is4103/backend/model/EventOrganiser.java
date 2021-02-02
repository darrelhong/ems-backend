package com.is4103.backend.model;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

@Entity

public class EventOrganiser extends User {

    @Column(nullable = true)
    @ElementCollection(targetClass = Attendee.class)
    private List<Attendee> attendeeFollowers;

    @Column(nullable = true)
    @ElementCollection(targetClass = BusinessPartner.class)
    private List<BusinessPartner> businessPartnerFollowers;

    public EventOrganiser(List<Attendee> attendeeFollowers, List<BusinessPartner> businessPartnerFollowers) {
        super();
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
