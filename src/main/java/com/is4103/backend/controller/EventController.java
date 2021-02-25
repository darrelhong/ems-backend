package com.is4103.backend.controller;

import java.util.ArrayList;
import java.util.List;

import com.is4103.backend.dto.CreateEventRequest;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.repository.EventRepository;
import com.is4103.backend.service.EventOrganiserService;
import com.is4103.backend.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
        return eventRepository.save(event);
    }

    @GetMapping(path = "/get-events")
    public Page<Event> getEvents(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return eventService.getEvents(page, size);
    }
}
