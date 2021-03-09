package com.is4103.backend.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;


// import org.hibernate.mapping.Set;

@Entity

public class Attendee extends User {


    @Column(nullable = true)
    @ElementCollection(targetClass = String.class)
    private List<String> categoryPreferences;

    @Column(nullable = true)
    @JsonIgnore
    @ManyToMany
    private List<EventOrganiser> followedEventOrganisers = new ArrayList<>();

    @OneToMany(mappedBy = "attendee")
    @ElementCollection(targetClass = TicketTransaction.class)
    private List<TicketTransaction> ticketTransactions;


    @JsonIgnore
    @ManyToMany
    private List<BusinessPartner> followedBusinessPartners;

    public Attendee() {

    }

    public Attendee(List<String> categoryPreferences, List<EventOrganiser> followedEventOrgs, List<BusinessPartner> followedBusinessPartners) {
        super();
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

    public List<BusinessPartner> getFollowedBusinessPartners() {
        return followedBusinessPartners;
    }

    public void setFollowedBusinessPartners(List<BusinessPartner> followedBusinessPartners) {
        this.followedBusinessPartners = followedBusinessPartners;
    }

}
