package com.is4103.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.is4103.backend.dto.EventSearchCriteria;
import com.is4103.backend.dto.FileStorageProperties;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.Rsvp;
import com.is4103.backend.model.SellerApplication;
import com.is4103.backend.repository.EventRepository;
import com.is4103.backend.repository.EventSpecification;
import com.is4103.backend.util.errors.EventNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FileStorageProperties fileStorageProperties;

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

    public boolean isBpRecommended(BusinessPartner bp, Event e) {
        List<String> eventCategories = e.getCategories();
        List<SellerApplication> applications = e.getSellerApplications();
        // CHECK 1: DONT RECO IF APPLIED ALR
        List<Long> bpIds = applications.stream().map(application -> application.getBusinessPartner().getId())
                .collect(Collectors.toList());
        if (bpIds.contains(bp.getId())) {
            return false;
        }
        // CHECK 2: DONT RECO IF ALR SENT RSVP
        List<Rsvp> rsvps = e.getRsvps();
        List<Long> rsvpBpIds = rsvps.stream().map(rsvp -> rsvp.getBusinessPartner().getId())
                .collect(Collectors.toList());
        if (rsvpBpIds.contains(bp.getId())) {
            return false;
        }

        // CHECK 3: RECO IF MATCHING CATEGORY
        return eventCategories.contains(bp.getBusinessCategory());
        // btw this works but data init, i make applications for every single BP so
        // there isnt any more BPs that are suitable
    }

    public ResponseEntity<String> removePicture(Event e, int imageIndex) {
        String imageUrl = e.getImages().get(imageIndex);
        String oldpicfilename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        System.out.println(oldpicfilename);
        Path oldFilepath = Paths.get(this.fileStorageProperties.getUploadDir() + "/eventImages/" + oldpicfilename)
                .toAbsolutePath().normalize();
        System.out.println(oldFilepath);
        try {
            Files.deleteIfExists(oldFilepath);
            //at this point we remove it from the arraylist
            e.getImages().remove(0);
            eventRepository.save(e);
            return ResponseEntity.ok("Success");
        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok("Error");
        }
      
    public List<String> getDistinctEventCategories() {
        return eventRepository.getDistinctEventCategories();
    }
}
