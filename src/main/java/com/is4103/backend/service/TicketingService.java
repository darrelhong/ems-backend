package com.is4103.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import com.is4103.backend.dto.ticketing.CheckoutResponse;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.PaymentStatus;
import com.is4103.backend.model.TicketTransaction;
import com.is4103.backend.repository.TicketTransactionRepository;
import com.is4103.backend.util.errors.ticketing.TicketTransactionNotFoundException;
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

    public TicketTransaction findById(String id) throws TicketTransactionNotFoundException {
        return ttRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new TicketTransactionNotFoundException("Ticket Transaction Not Found"));
    }

    public CheckoutResponse createTransaction(Long eventId, Integer ticketQty, Attendee attendee)
            throws StripeException {
        Event event = eventService.getEventById(eventId);
        Long ticketsSold = ttRepository.countByEventAndPaymentStatus(event, PaymentStatus.COMPLETED);

        List<TicketTransaction> tickets = new ArrayList<>();

        // check available tickets
        if (event.getTicketCapacity() >= ticketsSold + ticketQty) {
            Double paymentAmount = (double) event.getTicketPrice() * ticketQty;

            // times hundered to convert to cents
            Long stripePaymentAmount = Double.valueOf(paymentAmount * 100).longValue();

            Stripe.apiKey = stripeApiKey;

            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder().setCurrency("sgd")
                    .setAmount(stripePaymentAmount).build();
            PaymentIntent intent = PaymentIntent.create(createParams);

            for (int i = 0; i < ticketQty; i++) {
                TicketTransaction tt = new TicketTransaction();
                tt.setEvent(event);
                tt.setAttendee(attendee);
                tt.setStripePaymentId(intent.getId());
                ttRepository.save(tt);
                tickets.add(tt);
            }

            CheckoutResponse checkoutResponse = new CheckoutResponse(paymentAmount, intent.getClientSecret(), tickets);
            return checkoutResponse;
        }
        return null;
    }

    public void paymentComplete(List<String> ids) throws TicketTransactionNotFoundException {
        for (String id : ids) {
            TicketTransaction tt = findById(id);
            tt.setPaymentStatus(PaymentStatus.COMPLETED);
            ttRepository.save(tt);
        }
    }
}
