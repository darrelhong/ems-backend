package com.is4103.backend.service;

import java.util.ArrayList;
import java.util.List;

import com.is4103.backend.dto.ticketing.CheckoutResponse;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.PaymentStatus;
import com.is4103.backend.model.TicketTransaction;
import com.is4103.backend.repository.TicketTransactionRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TicketingService {

    @Value("${stripe.apikey}")
    private String stripeApiKey;

    @Autowired
    private TicketTransactionRepository ttRepository;

    @Autowired
    private EventService eventService;

    public CheckoutResponse createTransaction(Long eventId, Integer ticketQty, Attendee attendee)
            throws StripeException {
        Event event = eventService.getEventById(eventId);
        Long ticketsSold = ttRepository.countByEventAndPaymentStatus(event, PaymentStatus.COMPLETED);

        List<TicketTransaction> tickets = new ArrayList<>();

        // check available tickets
        if (event.getTicketCapacity() >= ticketsSold + ticketQty) {
            for (int i = 0; i < ticketQty; i++) {
                TicketTransaction tt = new TicketTransaction();
                tt.setEvent(event);
                tt.setAttendee(attendee);
                ttRepository.save(tt);
                tickets.add(tt);
            }

            // times hundered to convert to cents
            Long paymentAmount = (long) event.getTicketPrice() * ticketQty * 100;

            Stripe.apiKey = stripeApiKey;

            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder().setCurrency("sgd")
                    .setAmount(paymentAmount).build();

            PaymentIntent intent = PaymentIntent.create(createParams);
            CheckoutResponse checkoutResponse = new CheckoutResponse(intent.getClientSecret(), tickets);
            return checkoutResponse;
        }
        return null;
    }

}
