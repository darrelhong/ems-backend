package com.is4103.backend.repository;

import java.util.UUID;

import com.is4103.backend.model.Event;
import com.is4103.backend.model.PaymentStatus;
import com.is4103.backend.model.TicketTransaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTransactionRepository extends JpaRepository<TicketTransaction, UUID> {

    Long countByEvent(Event event);

    Long countByEventAndPaymentStatus(Event event, PaymentStatus paymentStatus);
}
