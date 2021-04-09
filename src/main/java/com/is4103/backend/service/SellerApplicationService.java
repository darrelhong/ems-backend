package com.is4103.backend.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.is4103.backend.dto.bpEventRegistration.ApplicationDto;
import com.is4103.backend.dto.bpEventRegistration.ApplicationResponse;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.PaymentStatus;
import com.is4103.backend.model.SellerApplication;
import com.is4103.backend.model.SellerApplicationStatus;
import com.is4103.backend.repository.SellerApplicationRepository;
import com.is4103.backend.repository.SellerProfileRepository;
import com.is4103.backend.util.errors.SellerApplicationNotFoundException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;

@Service
public class SellerApplicationService {

    @Autowired
    private SellerApplicationRepository sellerApplicationRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private BusinessPartnerService bpService;

    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    @Value("${stripe.apikey}")
    private String stripeApiKey;

    public List<SellerApplication> getAllSellerApplications() {
        return sellerApplicationRepository.findAll();
    }

    // public SellerApplication getSellerApplicationById(Long id) {
    // return sellerApplicationRepository.findById(id).get();
    // }

    public SellerApplication getSellerApplicationById(String id) throws SellerApplicationNotFoundException {
        return sellerApplicationRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new SellerApplicationNotFoundException("Application Not Found!"));
    }

    public Long getSellerApplicationEventId(String id)  throws SellerApplicationNotFoundException {
        try {
            SellerApplication sa = getSellerApplicationById(id);
            return sa.getEvent().getEid();
        } catch (Exception e ) {
            throw new SellerApplicationNotFoundException();
        }
    }

    public SellerApplication createSellerApplication(SellerApplication sellerApplication) {
        return sellerApplicationRepository.save(sellerApplication);
    }

    public SellerApplication updateSellerApplication(SellerApplication sellerApplication) {
        return sellerApplicationRepository.save(sellerApplication);
    }

    // public ApplicationResponse createTransaction(Long eventId, Integer boothQty,
    // BusinessPartner bp)
    // throws StripeException {
    public ApplicationResponse createTransaction(ApplicationDto applicationDto, BusinessPartner bp)
            throws StripeException {

        Event event = eventService.getEventById(applicationDto.getEventId());
        Integer boothQty = applicationDto.getBoothQty();
        String description = applicationDto.getDescription();
        String comments = applicationDto.getComments();

        // check if enough booths available
        if (true) {
            // if (event.getTicketCapacity() >= ticketsSold + ticketQty) {
            Double paymentAmount = (double) event.getBoothPrice() * boothQty;

            // times hundered to convert to cents
            Long stripePaymentAmount = Double.valueOf(paymentAmount * 100).longValue();

            Stripe.apiKey = stripeApiKey;

            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder().setCurrency("sgd")
                    .setAmount(stripePaymentAmount).build();
            PaymentIntent intent = PaymentIntent.create(createParams);

            SellerApplication app = new SellerApplication();
            app.setEvent(event);
            app.setBusinessPartner(bp);
            app.setStripePaymentId(intent.getId());
            app.setBoothQuantity(boothQty);
            app.setComments(comments);
            app.setDescription(description);
            createSellerApplication(app);

            ApplicationResponse applicationResponse = new ApplicationResponse(paymentAmount, intent.getClientSecret(),
                    app);
            return applicationResponse;
        }
        return null;
    }

    public SellerApplication paymentComplete(String id) throws SellerApplicationNotFoundException {
        SellerApplication app = getSellerApplicationById(id);
        app.setPaymentStatus(PaymentStatus.COMPLETED);
        return sellerApplicationRepository.save(app);
    }

    public List<SellerApplication> getAllApplicationsForEvent(String id) {
        Event event = eventService.getEventById(Long.parseLong(id));
        return event.getSellerApplications();
    }

    public Long getSellerProfileIdFromApplication(String id) throws SellerApplicationNotFoundException {
        try {
            SellerApplication app = getSellerApplicationById(id);
            Event e = app.getEvent();
            BusinessPartner bp = app.getBusinessPartner();
            return sellerProfileRepository.findByEventAndBusinessPartner(e, bp).getId();
        } catch (Exception e) {
            throw new SellerApplicationNotFoundException();
        }
    }

    public List<SellerApplication> removeCancelledApplications(List<SellerApplication> applications) {
        applications.removeIf(
                application -> application.getSellerApplicationStatus().equals(SellerApplicationStatus.CANCELLED));
        return applications;
    }
}
