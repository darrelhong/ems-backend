package com.is4103.backend.controller;

import java.util.ArrayList;
import java.util.List;

import com.is4103.backend.dto.CreateEventRequest;
import com.is4103.backend.dto.EventSearchCriteria;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventBoothTransaction;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.TicketTransaction;
import com.is4103.backend.repository.EventRepository;
import com.is4103.backend.service.EventOrganiserService;
import com.is4103.backend.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventOrganiserService eventOrganiserService;

    @GetMapping(path = "/all")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

     @GetMapping("/getAllBpByEvent/{id}")
     public List<BusinessPartner> getEventBps(@PathVariable Long id) {
     List<BusinessPartner> eventBpList = new ArrayList<>();
     List<EventBoothTransaction> eventBoothTransactionList = new ArrayList<>();
     Event event = this.getEventById(id);
     eventBoothTransactionList = event.getEventBoothTransactions();
     for (int i = 0; i < eventBoothTransactionList.size(); i++) {
     EventBoothTransaction transItem = eventBoothTransactionList.get(i);
     if (!(transItem.getPaymentStatus().toString().equals("REFUNDED"))) {
     eventBpList.add(transItem.getBusinessPartner());
        }
     }
     return eventBpList;
     }

     @GetMapping("/getAllAttByEvent/{id}")
     public List<Attendee> getEventAtts(@PathVariable Long id) {
         List<Attendee> eventAttList = new ArrayList<>();
         List<TicketTransaction> eventTicketTransactionList = new ArrayList<>();
         Event event = this.getEventById(id);
         eventTicketTransactionList = event.getTicketTransactions();
         for (int i = 0; i < eventTicketTransactionList.size(); i++) {
             TicketTransaction transItem = eventTicketTransactionList.get(i);
             if (!(transItem.getPaymentStatus().toString().equals("REFUNDED"))) {
                 eventAttList.add(transItem.getAttendee());
             }
         }
         return eventAttList;
     }

    @GetMapping("/{oid}/all")
    public List<Event> getAllEventsByOrganiser(@PathVariable Long oid) {
        return eventService.getAllEventsByOrganiser(oid);
    }

    @PostMapping("/create")
    public Event createEvent(@RequestBody CreateEventRequest createEventRequest) {
        Event event = new Event();

        EventOrganiser eventOrganiser = eventOrganiserService
                .getEventOrganiserById(createEventRequest.getEventOrganiserId());
        event.setEventOrganiser(eventOrganiser);
        event.setName(createEventRequest.getName());
        event.setAddress(createEventRequest.getAddress());
        event.setDescriptions(createEventRequest.getDescriptions());
        event.setTicketPrice(createEventRequest.getTicketPrice());
        event.setTicketCapacity(createEventRequest.getTicketCapacity());
        event.setPhysical(createEventRequest.isPhysical());
        event.setEventStartDate(createEventRequest.getEventStartDate());
        event.setEventEndDate(createEventRequest.getEventEndDate());
        event.setSaleStartDate(createEventRequest.getSaleStartDate());
        event.setSalesEndDate(createEventRequest.getSalesEndDate());
        event.setImages(new ArrayList<>());
        event.setBoothCapacity(createEventRequest.getBoothCapacity());
        event.setRating(createEventRequest.getRating());
        event.setEventStatus(createEventRequest.getEventStatus());
        event.setVip(createEventRequest.isVip());
        event.setPublished(createEventRequest.isPublished());
        event.setHidden(createEventRequest.isHidden());
        return eventRepository.save(event);
    }

    @PostMapping("/update")
    public Event updateEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @GetMapping(path = "/get-events")
    public Page<Event> getEvents(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size, @RequestParam(required = false) String sort,
            @RequestParam(required = false) String sortDir, @RequestParam(required = false) String keyword) {
        return eventService.getPublishedEvents(page, size, sort, sortDir, keyword);
    }

    @GetMapping(path = "/search")
    public Page<Event> search(EventSearchCriteria eventSearchCriteria) {
        return eventService.search(eventSearchCriteria);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        eventRepository.delete(eventService.getEventById(id));
        return ResponseEntity.ok("Success");
    }
}
