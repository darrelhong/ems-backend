package com.is4103.backend.dto.ticketing;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketTransactionsResponse {
    private Collection<TicketTransactionDto> tickets;
    private Collection<TicketTransactionEventDto> events;
}
