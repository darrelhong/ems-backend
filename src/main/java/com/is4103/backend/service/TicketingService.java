package com.is4103.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.is4103.backend.dto.ticketing.CheckoutResponse;
import com.is4103.backend.dto.ticketing.TicketTransactionEventDto;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TicketingService {

    @Value("${stripe.apikey}")
    private String stripeApiKey;

    @Autowired
    private TicketTransactionRepository ttRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private AttendeeService attendeeService;

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

    public List<TicketTransaction> paymentComplete(List<String> ids) throws TicketTransactionNotFoundException {
        List<TicketTransaction> tickets = new ArrayList<>();
        for (String id : ids) {
            TicketTransaction tt = findById(id);
            tt.setPaymentStatus(PaymentStatus.COMPLETED);
            ttRepository.save(tt);
            tickets.add(tt);
        }
        return tickets;
    }

    public void cancelCheckout(List<String> ids) throws TicketTransactionNotFoundException {
        for (String id : ids) {
            TicketTransaction tt = findById(id);
            ttRepository.delete(tt);
        }
    }

    public <T> Collection<T> getTicketTransactionsById(Long id, Class<T> type) {
        Attendee attendee = attendeeService.getAttendeeById(id);
        return ttRepository.findByAttendee(attendee, type, Sort.by("dateTimeOrdered").descending());
    }

    public <T> Collection<T> getTicketTransactionsAttendee(Attendee attendee, String period, Class<T> type) {
        if (period.equals("upcoming")) {
            return ttRepository.findByAttendeeAndEvent_EventStartDateAfterAndPaymentStatus(attendee,
                    LocalDateTime.now(), PaymentStatus.COMPLETED, type, Sort.by("dateTimeOrdered").descending());
        } else if (period.equals("previous")) {
            return ttRepository.findByAttendeeAndEvent_EventStartDateBeforeAndPaymentStatus(attendee,
                    LocalDateTime.now(), PaymentStatus.COMPLETED, type, Sort.by("dateTimeOrdered").descending());
        }
        return new ArrayList<>();
    }

    public Collection<TicketTransactionEventDto> getDistinctEventsPurchased(Attendee attendee, String period) {
        if (period.equals("upcoming")) {
            return ttRepository.findDistinctEventsByAttendeeUpcoming(attendee, PaymentStatus.COMPLETED,
                    LocalDateTime.now());
        } else if (period.equals("previous")) {
            return ttRepository.findDistinctEventsByAttendeePrevious(attendee, PaymentStatus.COMPLETED,
                    LocalDateTime.now());
        }
        return new ArrayList<>();
    }
}
