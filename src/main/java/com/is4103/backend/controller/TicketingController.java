package com.is4103.backend.controller;

import javax.validation.Valid;

import com.is4103.backend.dto.ticketing.CheckoutDto;
import com.is4103.backend.dto.ticketing.CheckoutResponse;
import com.is4103.backend.service.TicketingService;
import com.is4103.backend.util.errors.TicketCapacityExceededException;
import com.is4103.backend.util.errors.ticketing.CheckoutException;
import com.stripe.exception.StripeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/{id}")
    public ResponseEntity<CheckoutResponse> createTransaction(@PathVariable Long id,
            @RequestBody @Valid CheckoutDto checkoutDto) {
        try {
            CheckoutResponse result = ticketingService.createTransaction(id, checkoutDto.getTicketQty());

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
