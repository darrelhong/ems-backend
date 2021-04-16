package com.is4103.backend.dto.ticketing;

import java.time.LocalDateTime;
import java.util.UUID;

import com.is4103.backend.model.PaymentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganiserTicketDto {
    UUID id;
    PaymentStatus paymentStatus;
    String stripePaymentId;
    LocalDateTime dateTimeOrdered;
    Long eventEid;
    String eventName;
    String attendeeName;
}
