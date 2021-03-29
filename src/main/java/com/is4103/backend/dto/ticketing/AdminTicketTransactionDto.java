package com.is4103.backend.dto.ticketing;

public interface AdminTicketTransactionDto extends TicketTransactionDto {

    String getStripePaymentId();
}
