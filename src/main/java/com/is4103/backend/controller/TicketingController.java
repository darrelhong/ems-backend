package com.is4103.backend.controller;

import javax.validation.Valid;

import com.is4103.backend.dto.ticketing.CheckoutDto;
import com.is4103.backend.dto.ticketing.CheckoutResponse;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.User;
import com.is4103.backend.service.AttendeeService;
import com.is4103.backend.service.TicketingService;
import com.is4103.backend.util.errors.TicketCapacityExceededException;
import com.is4103.backend.util.errors.ticketing.CheckoutException;
import com.stripe.exception.StripeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Attendee attendee = attendeeService
                .getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            CheckoutResponse result = ticketingService.createTransaction(checkoutDto.getEventId(),
                    checkoutDto.getTicketQty(), attendee);

            if (result != null) {
                return ResponseEntity.ok(result);
            }
            throw new TicketCapacityExceededException();
        } catch (StripeException e) {
            System.out.println(e.getMessage());
            throw new CheckoutException();
        }
    }

}
