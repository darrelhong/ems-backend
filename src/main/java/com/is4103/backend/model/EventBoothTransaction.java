package com.is4103.backend.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class EventBoothTransaction {
    @Id
    @GeneratedValue
    private Long eid;

    @Enumerated(EnumType.STRING)
    private BoothApplicationStatus boothApplicationstatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne
    private BusinessPartner businessPartner;

    @ManyToOne
    private Event event;

}
