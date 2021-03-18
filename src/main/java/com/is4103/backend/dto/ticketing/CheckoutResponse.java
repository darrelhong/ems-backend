package com.is4103.backend.dto.ticketing;

import java.util.List;

import com.is4103.backend.model.TicketTransaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CheckoutResponse {

    private Double paymentAmount;

    private String clientSecret;

    private List<TicketTransaction> tickets;

}
