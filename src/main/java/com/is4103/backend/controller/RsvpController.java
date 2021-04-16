package com.is4103.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.is4103.backend.dto.EmailRequest;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.Rsvp;
import com.is4103.backend.service.BusinessPartnerService;
import com.is4103.backend.service.EventService;
import com.is4103.backend.service.MailService;
import com.is4103.backend.service.RsvpService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/rsvp")
public class RsvpController {

    @Autowired
    private RsvpService rsvpService;

    @Autowired
    private BusinessPartnerService bpService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MailService mailService;
    
    @PostMapping(path = "/create")
    public Rsvp createRsvp(@RequestParam(name = "eid", defaultValue = "1") Long eid,
    @RequestParam(name = "id", defaultValue = "13") Long id) {
        Rsvp rsvp = new Rsvp();
        Event event = eventService.getEventById(eid);
        BusinessPartner bp = bpService.getBusinessPartnerById(id);
        rsvp.setEvent(event);
        rsvp.setBusinessPartner(bp);

        //SENDING THE RSVP EMAIL
        EmailRequest request = new EmailRequest();
        EventOrganiser organiser = event.getEventOrganiser();
        request.setSenderId(organiser.getId());
        request.setRecipientId(bp.getId());
        request.setSubject("Invitation to apply for " +event.getName());
        request.setTextBody("Hi there!  We at " +organiser.getName() +" are interested in collaborating and we hope you would apply for our event at the link at " + "http://localhost:3000/partner/event/" +event.getEid() + ".\r\n\r\n" +"<br>"
        +"Feel free to email us at " +organiser.getEmail() +" if you have any queries!\r\n\r\n" + "<b>");
        mailService.sendEmailNotif(request);
        return rsvpService.createRsvp(rsvp);
    }

    @GetMapping("/event/{id}")
    public List<Rsvp> getEventRsvps (@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return event.getRsvps();
    }

    @GetMapping("/check-sent")
    public Boolean checkIfRsvpSent (@RequestParam(name = "eid", defaultValue = "1") Long eid,
    @RequestParam(name = "id", defaultValue = "13") Long id) {
        List<Rsvp> rsvps = getEventRsvps(eid);
        List<Long> rsvpBpIds = rsvps.stream().map(rsvp -> rsvp.getBusinessPartner().getId()).collect(Collectors.toList());
        return rsvpBpIds.indexOf(id) >= 0;
    }
}
