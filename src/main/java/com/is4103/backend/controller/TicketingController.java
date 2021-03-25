package com.is4103.backend.controller;

import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import com.is4103.backend.dto.ticketing.CheckoutDto;
import com.is4103.backend.dto.ticketing.CheckoutResponse;
import com.is4103.backend.dto.ticketing.TransactionListDto;
import com.is4103.backend.dto.ticketing.TicketTransactionDto;
import com.is4103.backend.dto.ticketing.TicketTransactionEventDto;
import com.is4103.backend.dto.ticketing.TicketTransactionsResponse;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.TicketTransaction;
import com.is4103.backend.service.AttendeeService;
import com.is4103.backend.service.TicketingService;
import com.is4103.backend.util.errors.TicketCapacityExceededException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.errors.ticketing.CheckoutException;
import com.is4103.backend.util.errors.ticketing.TicketTransactionNotFoundException;
import com.stripe.exception.StripeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(path = "/ticketing")
public class TicketingController {

    @Autowired
    private TicketingService ticketingService;

    @Autowired
    private AttendeeService attendeeService;

    @PostMapping(value = "/checkout")
    public ResponseEntity<CheckoutResponse> createTransaction(@RequestBody @Valid CheckoutDto checkoutDto) {
        try {
            Attendee attendee = attendeeService
                    .getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

            CheckoutResponse result = ticketingService.createTransaction(checkoutDto.getEventId(),
                    checkoutDto.getTicketQty(), attendee);

            if (result != null) {
                return ResponseEntity.ok(result);
            }
            throw new TicketCapacityExceededException();
        } catch (StripeException | UserNotFoundException e) {
            System.out.println(e.getMessage());
            throw new CheckoutException();
        }
    }

    @PostMapping(value = "/payment-complete")
    public ResponseEntity<List<TicketTransaction>> paymentComplete(
            @RequestBody @Valid TransactionListDto transactionListDto) {
        try {
            List<TicketTransaction> response = ticketingService
                    .paymentComplete(transactionListDto.getTicketTransactionIds());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new CheckoutException();
        }
    }

    @PostMapping(value = "/cancel")
    public ResponseEntity<?> cancelCheckout(@RequestBody @Valid TransactionListDto transactionListDto)
            throws TicketTransactionNotFoundException {
        ticketingService.cancelCheckout(transactionListDto.getTicketTransactionIds());
        return ResponseEntity.ok("Success");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/attendee/{id}")
    public ResponseEntity<Collection<TicketTransactionDto>> getTicketTransactionsById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketingService.getTicketTransactionsById(id, TicketTransactionDto.class));
    }

    @PreAuthorize("hasRole('ATND')")
    @GetMapping(value = "/attendee")
    public ResponseEntity<TicketTransactionsResponse> getTicketTransactionsAttendee(
            @RequestParam(name = "period", defaultValue = "upcoming") String period) {
        Attendee attendee = attendeeService
                .getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        Collection<TicketTransactionDto> tickets = ticketingService.getTicketTransactionsAttendee(attendee, period,
                TicketTransactionDto.class);
        Collection<TicketTransactionEventDto> events = ticketingService.getDistinctEventsPurchased(attendee);
        TicketTransactionsResponse resp = new TicketTransactionsResponse(tickets, events);
        return ResponseEntity.ok(resp);
    }
}
