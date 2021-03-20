package com.is4103.backend.dto.ticketing;

import java.util.UUID;

import com.is4103.backend.model.PaymentStatus;

public interface TicketTransactionDto {
    UUID getId();

    PaymentStatus getPaymentStatus();

    EventSummary getEvent();

    AttendeeSummary getAttendee();

    interface EventSummary {
        Long getEid();

        String getName();
    }

    interface AttendeeSummary {
        String getName();

        String getId();
    }

}
