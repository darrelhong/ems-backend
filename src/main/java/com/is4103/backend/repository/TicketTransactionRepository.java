package com.is4103.backend.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import com.is4103.backend.dto.ticketing.TicketTransactionEventDto;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.PaymentStatus;
import com.is4103.backend.model.TicketTransaction;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TicketTransactionRepository
        extends JpaRepository<TicketTransaction, UUID>, JpaSpecificationExecutor<TicketTransaction> {

    Long countByEvent(Event event);

    Long countByEventAndPaymentStatus(Event event, PaymentStatus paymentStatus);

    <T> Collection<T> findByAttendee(Attendee attendee, Class<T> type);

    <T> Collection<T> findByAttendee(Attendee attendee, Class<T> type, Sort sort);

    <T> Collection<T> findByAttendeeAndEvent_EventStartDateAfter(Attendee attendee, LocalDateTime now, Class<T> type,
            Sort sort);

    <T> Collection<T> findByAttendeeAndEvent_EventStartDateBefore(Attendee attendee, LocalDateTime now, Class<T> type,
            Sort sort);

    <T> Collection<T> findByAttendeeAndEvent_EventStartDateAfterAndPaymentStatus(Attendee attendee, LocalDateTime now,
            PaymentStatus paymentStatus, Class<T> type, Sort sort);

    <T> Collection<T> findByAttendeeAndEvent_EventStartDateBeforeAndPaymentStatus(Attendee attendee, LocalDateTime now,
            PaymentStatus paymentStatus, Class<T> type, Sort sort);

    @Query("SELECT DISTINCT(tt.event.eid) as eid, tt.event.name as name FROM TicketTransaction tt WHERE tt.attendee = ?1 AND tt.paymentStatus = ?2 AND tt.event.eventStartDate > ?3")
    Collection<TicketTransactionEventDto> findDistinctEventsByAttendeeUpcoming(Attendee attendee,
            PaymentStatus paymentStatus, LocalDateTime eventStartDate);

    @Query("SELECT DISTINCT(tt.event.eid) as eid, tt.event.name as name FROM TicketTransaction tt WHERE tt.attendee = ?1 AND tt.paymentStatus = ?2 AND tt.event.eventStartDate < ?3")
    Collection<TicketTransactionEventDto> findDistinctEventsByAttendeePrevious(Attendee attendee,
            PaymentStatus paymentStatus, LocalDateTime eventStartDate);
}
