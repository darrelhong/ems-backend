package com.is4103.backend.service;

import java.util.List;

import com.is4103.backend.model.Event;
import com.is4103.backend.repository.EventRepository;
import com.is4103.backend.util.errors.EventNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
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

}
