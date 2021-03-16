package com.is4103.backend.controller;

import javax.validation.Valid;

import com.is4103.backend.dto.ticketing.CheckoutDto;
import com.is4103.backend.service.TicketingService;
import com.is4103.backend.util.errors.TicketCapacityExceededException;

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
    public ResponseEntity<String> createTransaction(@PathVariable Long id,
            @RequestBody @Valid CheckoutDto checkoutDto) {
        Boolean result = ticketingService.createTransaction(id, checkoutDto.getTicketQty());

        if (result) {
            return ResponseEntity.ok("Success");
        }
        throw new TicketCapacityExceededException();
    }

}
