package com.is4103.backend.service;

import com.is4103.backend.model.Rsvp;
import com.is4103.backend.repository.RsvpRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RsvpService {
    @Autowired
    private RsvpRepository rsvpRepository;

     public Rsvp createRsvp(Rsvp rsvp) {
         return rsvpRepository.save(rsvp);
     }
}
