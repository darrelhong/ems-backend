package com.is4103.backend.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

// import org.hibernate.mapping.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Attendee extends User {

    @Column(nullable = true)
    @ElementCollection(targetClass = String.class)
    private List<String> categoryPreferences;

    @Column(nullable = true)
    @JsonIgnore
    @ElementCollection(targetClass = EventOrganiser.class)
    @ManyToMany(mappedBy = "attendeeFollowers")
    private List<EventOrganiser> followedEventOrganisers;

    @OneToMany(mappedBy = "attendee")
    private List<TicketTransaction> ticketTransactions;

    @JsonIgnore
    @ManyToMany(mappedBy = "attendeeFollowers", cascade = CascadeType.MERGE)
    @ElementCollection(targetClass = BusinessPartner.class)
    private Set<BusinessPartner> followedBusinessPartners = new HashSet<>();

    public Attendee() {
        super();
    }

    public Attendee(List<String> categoryPreferences, List<EventOrganiser> followedEventOrgs,
            Set<BusinessPartner> followedBusinessPartners) {
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

    public List<EventOrganiser> getFollowedEventOrgs() {
        return followedEventOrganisers;
    }

    public void setFollowedEventOrgs(List<EventOrganiser> followedEventOrgs) {
        this.followedEventOrganisers = followedEventOrgs;
    }

    public Set<BusinessPartner> getFollowedBusinessPartners() {
        return followedBusinessPartners;
    }

    public void setFollowedBusinessPartners(Set<BusinessPartner> followedBusinessPartners) {
        this.followedBusinessPartners = followedBusinessPartners;
    }

}
