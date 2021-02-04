package com.is4103.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data

public class Enquiry {
    
    @Id
    @GeneratedValue
    private Long eid;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false)
    private String content;

    @Column(nullable=true)
    private String contactNumber;

    @ManyToOne
    private EventOrganiser eventOrganiser;

    @ManyToOne
    private BusinessPartner businessPartner;


}
