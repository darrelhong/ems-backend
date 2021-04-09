package com.is4103.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.is4103.backend.dto.CreateEventRequest;
import com.is4103.backend.dto.CreateSellerApplicationRequest;
import com.is4103.backend.dto.bpEventRegistration.ApplicationDto;
import com.is4103.backend.dto.bpEventRegistration.ApplicationResponse;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.PaymentStatus;
import com.is4103.backend.model.SellerApplication;
import com.is4103.backend.model.SellerApplicationStatus;
import com.is4103.backend.repository.EventRepository;
import com.is4103.backend.service.BusinessPartnerService;
import com.is4103.backend.service.EventOrganiserService;
import com.is4103.backend.service.EventService;
import com.is4103.backend.service.SellerApplicationService;
import com.is4103.backend.service.SellerProfileService;
import com.is4103.backend.util.errors.BoothCapacityExceededException;
import com.is4103.backend.util.errors.SellerApplicationNotFoundException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.errors.ticketing.CheckoutException;
import com.stripe.exception.StripeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(path = "/sellerApplication")
public class SellerApplicationController {

    @Autowired
    private SellerApplicationService sellerApplicationService;

    @Autowired
    private EventService eventService;

    @Autowired
    private BusinessPartnerService bpService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventOrganiserService eventOrganiserService;

    @Autowired
    private SellerProfileService sellerProfileService;

    @GetMapping(path = "/{id}")
    public SellerApplication getSellerApplicationById(@PathVariable String id)
            throws SellerApplicationNotFoundException {
        return sellerApplicationService.getSellerApplicationById(id);
    }

    @GetMapping(path = "/all")
    public List<SellerApplication> getAllSellerApplications() {
        return sellerApplicationService.getAllSellerApplications();
    }

    @GetMapping(path = "/organiser/{id}")
    public List<SellerApplication> getApplicationsByEo(@PathVariable Long id) {
        List<Event> eoEvents = eventOrganiserService.getAllEventsByEoId(id);
        List<SellerApplication> allApplications = sellerApplicationService.getAllSellerApplications();
        allApplications.removeIf(application -> eoEvents.indexOf(application.getEvent()) < 0); 
        // take out if the application doesnt belong to his list of events
        return allApplications;
    }

    @PostMapping(value = "/checkout")
    public ResponseEntity<ApplicationResponse> createTransaction(@RequestBody @Valid ApplicationDto applicationDto) {
        try {
            System.out.println("boothqty " + applicationDto.getBoothQty());
            System.out.println("eventid " + applicationDto.getEventId());
            BusinessPartner bp = bpService
                    .getPartnerByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

            ApplicationResponse result = sellerApplicationService.createTransaction(applicationDto, bp);

            if (result != null) {
                return ResponseEntity.ok(result);
            }
            throw new BoothCapacityExceededException();
        } catch (StripeException | UserNotFoundException e) {
            System.out.println(e.getMessage());
            throw new CheckoutException();
        }
    }

    @PostMapping(value = "/approve/{id}")
    public SellerApplication approveApplication(@PathVariable String id) throws SellerApplicationNotFoundException {
        try {
            SellerApplication app = sellerApplicationService.getSellerApplicationById(id);
            app.setSellerApplicationStatus(SellerApplicationStatus.APPROVED);
            return sellerApplicationService.updateSellerApplication(app);
        } catch (Exception e) {
            throw new SellerApplicationNotFoundException("No such application found");
        }
    }

    @PostMapping(value = "/reject/{id}")
    public SellerApplication rejectApplication(@PathVariable String id) throws SellerApplicationNotFoundException {
        try {
            System.out.println(sellerApplicationService.getAllSellerApplications().get(0).getId());
            SellerApplication app = sellerApplicationService.getSellerApplicationById(id);
            app.setSellerApplicationStatus(SellerApplicationStatus.REJECTED);
            return sellerApplicationService.updateSellerApplication(app);
        } catch (Exception e) {
            throw new SellerApplicationNotFoundException("No such application found");
        }
    }

    @PostMapping(value = "/cancel/{id}")
    public SellerApplication cancelSellerApplication(@PathVariable String id) throws SellerApplicationNotFoundException {
        SellerApplication app = sellerApplicationService.getSellerApplicationById(id);
        app.setSellerApplicationStatus(SellerApplicationStatus.CANCELLED);
        try {
            //DELETING THE PROFILE
            Long sellerProfileId = getSellerProfileIdFromApplication(id);
            sellerProfileService.deleteProfileById(sellerProfileId);
        } catch (Exception e) {
            //either no seller profile found, or cannot delete
            System.out.println(e);
        }
        return sellerApplicationService.updateSellerApplication(app);
    };    

    @GetMapping(value ="/get-sellerprofile-id/{id}")
    public Long getSellerProfileIdFromApplication(@PathVariable String id) throws SellerApplicationNotFoundException {
        return sellerApplicationService.getSellerProfileIdFromApplication(id);
    }
    // NOT WORKING FOR SOME REASON,UNABLE TO RECEIVE THE REQ BODY IT TURNS OUT BLANK
    // IN SELLERPROFILECONTROLLER
    // @PostMapping(path="/create")
    // public SellerApplication createSellerApplication (@RequestBody
    // CreateSellerApplicationRequest request,
    // @RequestParam(name="eid", defaultValue="1") Long eid,
    // @RequestParam(name="id", defaultValue="13") Long id
    // ) {
    // SellerApplication application = new SellerApplication();
    // Event event = eventService.getEventById(eid);
    // BusinessPartner bp = bpService.getBusinessPartnerById(id);
    // application.setBusinessPartner(bp);
    // application.setEvent(event);
    // application.setDescription(request.getDescription());
    // application.setComments(request.getComments());
    // application.setBoothQuantity(request.getBoothQuantity());
    // application.setSellerApplicationStatus(SellerApplicationStatus.PENDING);
    // application.setPaymentStatus(PaymentStatus.PENDING);

    // return sellerApplicationService.createSellerApplication(application);
    // }

}
