package com.is4103.backend.service;

import java.util.List;

import com.is4103.backend.dto.EventSearchCriteria;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventStatus;
import com.is4103.backend.repository.EventRepository;
import com.is4103.backend.repository.EventSpecification;
import com.is4103.backend.util.errors.EventNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }



    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException());
    }

    public Page<Event> getEvents(int page, int size) {
        // return eventRepository.findByEventStatus(EventStatus.PUBLISHED, PageRequest.of(page, size));
        return eventRepository.findByIsPublished(true, PageRequest.of(page,size));
    }
    public Page<Event> getPublishedEvents(int page, int size, String sortBy, String sortDir) {
        Sort sort;
        if (sortBy != null && sortDir != null) {
            if (sortDir.equals("desc")) {
                sort = Sort.by(sortBy).descending();
            } else {
                sort = Sort.by(sortBy).ascending();
            }
            return eventRepository.findByIsPublished(true, PageRequest.of(page, size, sort));
        }
        return eventRepository.findByIsPublished(true, PageRequest.of(page, size));
    }

    public List<Event> getAllEventsByOrganiser(Long oid) {
        return eventRepository.getAllEventsByOrganiser(oid);
    }

    public Page<Event> search(EventSearchCriteria eventSearchCriteria) {
        return eventRepository.findAll(new EventSpecification(eventSearchCriteria),
                eventSearchCriteria.toPageRequest());
    }
}
