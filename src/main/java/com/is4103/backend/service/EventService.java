package com.is4103.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import com.is4103.backend.dto.EventSearchCriteria;
import com.is4103.backend.model.Event;
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

    public <T> T getEventById(Long id, Class<T> type) {
        return eventRepository.findByEid(id, type).orElseThrow(() -> new EventNotFoundException());
    }

    public Page<Event> getEvents(int page, int size) {
        // return eventRepository.findByEventStatus(EventStatus.PUBLISHED,
        // PageRequest.of(page, size));
        return eventRepository.findByIsPublished(true, PageRequest.of(page, size));
    }

    public <T> Page<T> getPublishedEvents(int page, int size, String sortBy, String sortDir, String keyword,
            Class<T> type) {
        Sort sort = null;
        LocalDateTime now = LocalDateTime.now();

        if (sortBy != null && sortDir != null) {
            if (sortDir.equals("desc")) {
                sort = Sort.by(sortBy).descending();
            } else {
                sort = Sort.by(sortBy).ascending();
            }
        }
        if (keyword != null) {
            if (sort == null) {
                return eventRepository.findByNameContainingAndIsPublishedAndEventStartDateGreaterThan(keyword, true,
                        now, PageRequest.of(page, size), type);
            } else {
                return eventRepository.findByNameContainingAndIsPublishedAndEventStartDateGreaterThan(keyword, true,
                        now, PageRequest.of(page, size, sort), type);
            }

        }
        if (sort == null) {
            return eventRepository.findByIsPublishedAndEventStartDateGreaterThan(true, now, PageRequest.of(page, size),
                    type);
        } else {
            return eventRepository.findByIsPublishedAndEventStartDateGreaterThan(true, now,
                    PageRequest.of(page, size, sort), type);
        }

    }

    public List<Event> getAllEventsByOrganiser(Long oid) {
        return eventRepository.getAllEventsByOrganiser(oid);
    }

    public Page<Event> search(EventSearchCriteria eventSearchCriteria) {
        return eventRepository.findAll(new EventSpecification(eventSearchCriteria),
                eventSearchCriteria.toPageRequest());
    }

    public Event addEventImage(Event event, String imageUrl) {
        List<String> images = event.getImages();
        images.add(imageUrl);
        event.setImages(images);
        return eventRepository.save(event);
    };

    public List<String> getDistinctEventCategories() {
        return eventRepository.getDistinctEventCategories();
    }
}
