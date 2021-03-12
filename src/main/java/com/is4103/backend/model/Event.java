package com.is4103.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Entity
@Data
@JsonView(EventViews.Public.class)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long eid;

    @ManyToOne
    @JsonIgnoreProperties({ "events", "approved", "approvalMessage", "supportDocsUrl", "vipList", "attendeeFollowers",
            "businessPartnerFollowers", "enquiries", "description", "profilePic", "email", "enabled", "phonenumber",
            "address", "roles", "notifications" })
    private EventOrganiser eventOrganiser;

    @JsonView(EventViews.Private.class)
    @ManyToMany(fetch = FetchType.LAZY)
    @ElementCollection(targetClass = BusinessPartner.class)
    private List<BusinessPartner> favouriteBusinessPartners;

    @JsonView(EventViews.Private.class)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "event")
    @ElementCollection(targetClass = EventBoothTransaction.class)
    @JsonIgnoreProperties("event")
    private List<EventBoothTransaction> eventBoothTransactions;

    @JsonView(EventViews.Private.class)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "event")
    @JsonIgnoreProperties("event")
    private List<Booth> booths;

    @JsonView(EventViews.Private.class)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "event")
    @ElementCollection(targetClass = TicketTransaction.class)
    private List<TicketTransaction> ticketTransactions;

    // @Column(nullable = false)
    private String name;

    // @Column(nullable = false)
    private String address;

    // @Column(nullable = false)
    private String descriptions;

    private boolean isSellingTicket;

    // @Column(nullable = true)
    private float ticketPrice;

    // @Column(nullable = true)
    private int ticketCapacity;

    // @Column(nullable = false)
    private boolean isPhysical;

    // @Column(nullable = false)
    // @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime eventStartDate;

    // @Column(nullable = false)
    // @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime eventEndDate;

    // @Column(nullable = true)
    // @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime saleStartDate;

    // @Column(nullable = true)
    @Column(nullable = true)
    // @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime salesEndDate;

    // @Column(nullable = false)
    @ElementCollection(targetClass = String.class)
    private List<String> images;

    // @Column(nullable = false)
    private int boothCapacity;

    // @Column(nullable = true)
    private int rating;

    // @Column(nullable = false)s
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    private boolean isVip;

    private boolean isHidden;

    private boolean isPublished;

    public boolean isAvailableForSale() {
        if (this.saleStartDate != null && this.salesEndDate != null) {
            return LocalDateTime.now().isAfter(this.saleStartDate) && LocalDateTime.now().isBefore(this.salesEndDate);
        }
        return false;
    }
}
