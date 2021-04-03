package com.is4103.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.is4103.backend.dto.EventSearchCriteria;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.TicketTransaction;
import com.is4103.backend.model.User;
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

    @Autowired
    private UserService userService;

    @Autowired
    private TicketingService tktService;

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

    public List<Event> getAllEventsThisWeekend() {
        User user = userService.getUserById(userService.getCurrentUserId());
        List<Event> eventList = getAllEvents();
        List<Event> filterEventList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime comingSunday = now.plusDays(7 - now.getDayOfWeek().getValue());
        comingSunday = comingSunday.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        LocalDateTime comingSaturday = now.plusDays(6 - now.getDayOfWeek().getValue());
        comingSaturday = comingSaturday.withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        for (Event event : eventList) {
            if (event.getEventStatus().toString().equals("CREATED") && event.isPublished() == true
                    && !(event.getEventStartDate().isAfter(comingSunday))
                    && !(event.getEventEndDate().isBefore(comingSaturday))
                    && !(event.getSalesEndDate().isBefore(now))) {
                
                if (user instanceof BusinessPartner) {
                    LocalDateTime thirdDayFromNow = now.plusDays(3);
                    thirdDayFromNow = thirdDayFromNow.withHour(0).withMinute(0).withSecond(0).withNano(0);
                    if (event.getEventStartDate().isBefore(thirdDayFromNow)) {
                        continue;
                    }
                }
                filterEventList.add(event);
            }
        }

        return filterEventList;
    }

    public List<Event> getAllEventsNextWeek() {
        User user = userService.getUserById(userService.getCurrentUserId());
        List<Event> eventList = getAllEvents();
        List<Event> filterEventList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextSunday = now.plusDays(14 - now.getDayOfWeek().getValue());
        nextSunday = nextSunday.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        LocalDateTime comingMonday = now.plusDays(8 - now.getDayOfWeek().getValue());
        comingMonday = comingMonday.withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        for (Event event : eventList) {
            if (event.getEventStatus().toString().equals("CREATED") && event.isPublished() == true
                    && !(event.getEventStartDate().isAfter(nextSunday))
                    && !(event.getEventEndDate().isBefore(comingMonday))
                    && !(event.getSalesEndDate().isBefore(now))) {
                
                if (user instanceof BusinessPartner) {
                    LocalDateTime thirdDayFromNow = now.plusDays(3);
                    thirdDayFromNow = thirdDayFromNow.withHour(0).withMinute(0).withSecond(0).withNano(0);
                    if (event.getEventStartDate().isBefore(thirdDayFromNow)) {
                        continue;
                    }
                }
                filterEventList.add(event);
            }
        }

        return filterEventList;
    }

    public List<Event> getAllEventsInNext30Days() {
        List<Event> eventList = getAllEvents();
        List<Event> filterEventList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastDay = now.plusDays(30);
        lastDay = lastDay.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        LocalDateTime thirdDay = now.plusDays(3);
        thirdDay = thirdDay.withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        for (Event event : eventList) {
            if (event.getEventStatus().toString().equals("CREATED") && event.isPublished() == true
                    && !(event.getEventStartDate().isBefore(thirdDay))
                    && !(event.getEventStartDate().isAfter(lastDay))
                    && !(event.getSalesEndDate().isBefore(now))) {
                filterEventList.add(event);
            }
        }

        return filterEventList;
    }

    public Event getMostPopularEvent() {
        User user = userService.getUserById(userService.getCurrentUserId());
        List<Event> eventList = getAllEvents();
        Event mostPopularEvent = null;
        LocalDateTime now = LocalDateTime.now();
        
        for (Event event : eventList) {
            if (event.getEventStatus().toString().equals("CREATED") && event.isPublished() == true
                    && !(event.getSaleStartDate().isAfter(now))
                    && !(event.getSalesEndDate().isBefore(now))) {
                if (mostPopularEvent == null) {
                
                    if (user instanceof BusinessPartner) {
                        LocalDateTime thirdDayFromNow = now.plusDays(3);
                        thirdDayFromNow = thirdDayFromNow.withHour(0).withMinute(0).withSecond(0).withNano(0);
                        if (event.getEventStartDate().isBefore(thirdDayFromNow)) {
                            continue;
                        }
                    }
                    mostPopularEvent = event;
                }
                else {
                    List<TicketTransaction> transList = tktService.getAllTransactions();

                    Integer e1NoOfTicketsSold = 0;
                    for (TicketTransaction trans : transList) {
                        if (trans.getEvent().getEid() == event.getEid()) {
                            e1NoOfTicketsSold++;
                        }
                    }
                    Integer e2NoOfTicketsSold = 0;
                    for (TicketTransaction trans : transList) {
                        if (trans.getEvent().getEid() == mostPopularEvent.getEid()) {
                            e2NoOfTicketsSold++;
                        }
                    }

                    if (e1NoOfTicketsSold > e2NoOfTicketsSold) {
                
                        if (user instanceof BusinessPartner) {
                            LocalDateTime thirdDayFromNow = now.plusDays(3);
                            thirdDayFromNow = thirdDayFromNow.withHour(0).withMinute(0).withSecond(0).withNano(0);
                            if (event.getEventStartDate().isBefore(thirdDayFromNow)) {
                                continue;
                            }
                        }
                        mostPopularEvent = event;
                    }
                }
            }
        }

        return mostPopularEvent;
    }

    public List<Event> getTopTenEvents() {
        User user = userService.getUserById(userService.getCurrentUserId());
        List<Event> eventList = getAllEvents();
        List<Event> filterEventList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Event event : eventList) {
            if (event.getEventStatus().toString().equals("CREATED") && event.isPublished() == true
                    && !(event.getSaleStartDate().isAfter(now))
                    && !(event.getSalesEndDate().isBefore(now))) {
                if (filterEventList.size() < 10) {
                
                    if (user instanceof BusinessPartner) {
                        LocalDateTime thirdDayFromNow = now.plusDays(3);
                        thirdDayFromNow = thirdDayFromNow.withHour(0).withMinute(0).withSecond(0).withNano(0);
                        if (event.getEventStartDate().isBefore(thirdDayFromNow)) {
                            continue;
                        }
                    }
                    filterEventList.add(event);
                    filterEventList.sort(new Comparator<Event>() {
                        @Override
                        public int compare(Event e1, Event e2) {
                            List<TicketTransaction> transList = tktService.getAllTransactions();

                            Integer e1NoOfTicketsSold = 0;
                            for (TicketTransaction trans : transList) {
                                if (trans.getEvent().getEid() == e1.getEid()) {
                                    e1NoOfTicketsSold++;
                                }
                            }
                            Integer e2NoOfTicketsSold = 0;
                            for (TicketTransaction trans : transList) {
                                if (trans.getEvent().getEid() == e2.getEid()) {
                                    e2NoOfTicketsSold++;
                                }
                            }

                            return e2NoOfTicketsSold.compareTo(e1NoOfTicketsSold);
                        }
                    });
                }
                else {
                    List<TicketTransaction> transList = tktService.getAllTransactions();

                    Integer noOfTicketsSold = 0;
                    for (TicketTransaction trans : transList) {
                        if (trans.getEvent().getEid() == event.getEid()) {
                            noOfTicketsSold++;
                        }
                    }
                    Integer tenthNoOfTicketsSold = 0;
                    for (TicketTransaction trans : transList) {
                        if (trans.getEvent().getEid() == filterEventList.get(9).getEid()) {
                            tenthNoOfTicketsSold++;
                        }
                    }

                    if (noOfTicketsSold > tenthNoOfTicketsSold) {
                
                        if (user instanceof BusinessPartner) {
                            LocalDateTime thirdDayFromNow = now.plusDays(3);
                            thirdDayFromNow = thirdDayFromNow.withHour(0).withMinute(0).withSecond(0).withNano(0);
                            if (event.getEventStartDate().isBefore(thirdDayFromNow)) {
                                continue;
                            }
                        }
                        filterEventList.remove(9);
                        filterEventList.add(event);
                        filterEventList.sort(new Comparator<Event>() {
                            @Override
                            public int compare(Event e1, Event e2) {
                                List<TicketTransaction> transList = tktService.getAllTransactions();
    
                                Integer e1NoOfTicketsSold = 0;
                                for (TicketTransaction trans : transList) {
                                    if (trans.getEvent().getEid() == e1.getEid()) {
                                        e1NoOfTicketsSold++;
                                    }
                                }
                                Integer e2NoOfTicketsSold = 0;
                                for (TicketTransaction trans : transList) {
                                    if (trans.getEvent().getEid() == e2.getEid()) {
                                        e2NoOfTicketsSold++;
                                    }
                                }

                                return e2NoOfTicketsSold.compareTo(e1NoOfTicketsSold);
                            }
                        });
                    }
                }
            }
        }

        return filterEventList;
    }
}
