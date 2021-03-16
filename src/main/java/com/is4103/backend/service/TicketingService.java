package com.is4103.backend.service;

import com.is4103.backend.model.Event;
import com.is4103.backend.model.PaymentStatus;
import com.is4103.backend.repository.TicketTransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketingService {

    @Autowired
    private TicketTransactionRepository ttRepository;

    @Autowired
    private EventService eventService;

    public Boolean createTransaction(Long eventId, Integer ticketQty) {
        Event event = eventService.getEventById(eventId);
        Long ticketsSold = ttRepository.countByEventAndPaymentStatus(event, PaymentStatus.COMPLETED);

        // check available tickets
        if (event.getTicketCapacity() >= ticketsSold + ticketQty) {
            return true;
        }
        return false;
    }

}
