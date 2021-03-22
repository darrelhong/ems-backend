package com.is4103.backend.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

// import org.hibernate.mapping.Set;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
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
    @ManyToMany
    private List<Attendee> attendeeFollowers;

    @JsonIgnoreProperties("businessPartner")
    @Column(nullable = true)
    @ManyToMany(mappedBy="businessPartnerFollowers")
    private List<EventOrganiser> followEventOrganisers;

    // @JsonIgnoreProperties("businessPartner")
    // @OneToMany(mappedBy = "businessPartner")
    // @ElementCollection(targetClass = EventBoothTransaction.class)
    // private List<EventBoothTransaction> eventBoothTransactions;

    @OneToMany(mappedBy = "businessPartner")
    @ElementCollection(targetClass = SellerApplication.class)
    @JsonIgnoreProperties({"businessPartner","event"})
    private List<SellerApplication> sellerApplications;

    @OneToMany(mappedBy = "businessPartner")
    @ElementCollection(targetClass = Enquiry.class)
    private List<Enquiry> enquiries;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "businessPartner")
    @ElementCollection(targetClass = SellerProfile.class)
    @JsonIgnoreProperties("businessPartner")
    private List<SellerProfile> sellerProfiles;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "businessPartner")
    @ElementCollection(targetClass = Product.class)
    @JsonIgnoreProperties({"businessPartner","booths"})
    private List<Product> products;

    // @Transient
    @JsonIgnore
    @Column(nullable = true)
    @OneToMany(mappedBy = "partner")
    @ElementCollection(targetClass = Review.class)
    private List<Review> reviews;

    // public BusinessPartner() {
    //     super();
    // }

    // public BusinessPartner(String businessCategory, List<Event> favouriteEventList,
    //         List<EventBoothTransaction> eventBoothTransactions, List<EventOrganiser> followEventOrganisers) {
    //     this();
    //     this.businessCategory = businessCategory;
    //     this.favouriteEventList = favouriteEventList;
    //     this.eventBoothTransactions = eventBoothTransactions;
    //     this.followEventOrganisers = followEventOrganisers;
    //     this.attendeeFollowers = attendeeFollowers;
    // }

    // public String getBusinessCategory() {
    //     return businessCategory;
    // }

    // public void setBusinessCategory(String businessCategory) {
    //     this.businessCategory = businessCategory;
    // }

    // public List<Event> getFavouriteEventList() {
    //     return favouriteEventList;
    // }

    // public void setFavouriteEventList(List<Event> favouriteEventList) {
    //     this.favouriteEventList = favouriteEventList;
    // }

    // public List<Attendee> getAttendeeFollowers() {
    //     return attendeeFollowers;
    // }

    // public void setAttendeeFollowers(List<Attendee> attendeeFollowers) {
    //     this.attendeeFollowers = attendeeFollowers;
    // }

    // public List<Review> getReviews() {
    //     return reviews;
    // }

    // public void setReviews(List<Review> reviews) {
    //     this.reviews = reviews;
    // }

    // public List<EventBoothTransaction> getEventBoothTransactions() {
    //     return eventBoothTransactions;
    // }

    // public void setEventBoothTransactions(List<EventBoothTransaction> eventBoothTransactions) {
    //     this.eventBoothTransactions = eventBoothTransactions;
    // }

    // public List<EventOrganiser> getFollowEventOrganisers() {
    //     return followEventOrganisers;
    // }

    // public void setFollowEventOrganisers(List<EventOrganiser> followEventOrganisers) {
    //     this.followEventOrganisers = followEventOrganisers;
    // }
}
