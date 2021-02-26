package com.is4103.backend.util;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.EventStatus;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.model.User;
import com.is4103.backend.model.Event;
import com.is4103.backend.repository.RoleRepository;
import com.is4103.backend.repository.UserRepository;
import com.is4103.backend.repository.EventOrganiserRepository;
import com.is4103.backend.repository.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitRunner implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventOrganiserRepository eventOrganiserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // init roles
        if (roleRepository.findByRoleEnum(RoleEnum.ADMIN) == null) {
            roleRepository.save(new Role(RoleEnum.ADMIN, "System Admin"));
        }
        if (roleRepository.findByRoleEnum(RoleEnum.EVNTORG) == null) {
            roleRepository.save(new Role(RoleEnum.EVNTORG, "Event Organiser"));
        }
        if (roleRepository.findByRoleEnum(RoleEnum.BIZPTNR) == null) {
            roleRepository.save(new Role(RoleEnum.BIZPTNR, "Business Partnr"));
        }
        if (roleRepository.findByRoleEnum(RoleEnum.ATND) == null) {
            roleRepository.save(new Role(RoleEnum.ATND, "Attendee"));
        }

        if (userRepository.findByEmail("admin@abc.com") == null) {
            createAdmin();
        }

        if (userRepository.findByEmail("organiser@abc.com") == null) {
            createEventOrganisers();
        }

        if (userRepository.findByEmail("partner@abc.com") == null) {
            createBizPartner();
        }

        // Testing Entities
        if (eventRepository.findAll().isEmpty())
            createEvent();
    }

    @Transactional
    private void createAdmin() {
        User admin = new User();
        admin.setEmail("admin@abc.com");
        admin.setName("Default Admin");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setEnabled(true);
        admin.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.ADMIN)));
        userRepository.save(admin);
    }

    @Transactional
    private void createEventOrganisers() {
        EventOrganiser eo = new EventOrganiser();
        eo.setEmail("organiser@abc.com");
        eo.setName("First Organiser");
        eo.setPassword(passwordEncoder.encode("password"));
        eo.setEnabled(true);
        eo.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.EVNTORG)));
        userRepository.save(eo);

        for (int i = 2; i <= 11; i++) {
            eo = new EventOrganiser();
            eo.setEmail("organiser" + i + "@abc.com");
            eo.setName("Organiser " + i);
            eo.setPassword(passwordEncoder.encode("password"));
            eo.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.EVNTORG)));
            userRepository.save(eo);
        }
    }

    @Transactional
    private void createBizPartner() {
        BusinessPartner bp = new BusinessPartner();
        bp.setEmail("partner@abc.com");
        bp.setName("First Business Partner");
        bp.setPassword(passwordEncoder.encode("password"));
        bp.setEnabled(true);
        bp.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.BIZPTNR)));
        bp.setBusinessCategory("Travel");
        userRepository.save(bp);
    }

    // Testing Methods
    @Transactional
    private void createEvent() {
        Event event = new Event();
        event.setName("First Event");
        event.setAddress("Woodlands");
        event.setDescriptions("Some description");
        event.setPhysical(false);
        event.setEventStartDate(LocalDateTime.now());
        event.setEventEndDate(LocalDateTime.now());
        event.setSaleStartDate(LocalDateTime.now());
        event.setSalesEndDate(LocalDateTime.now());
        event.setImages(new ArrayList<>());
        event.setBoothCapacity(305);
        event.setRating(5);
        event.setEventStatus(EventStatus.COMPLETED);

        Event event2 = new Event();
        event2.setName("Second Event");
        event2.setAddress("Sembwang2");
        event2.setDescriptions("Some description two2");
        event2.setPhysical(false);
        event2.setEventStartDate(LocalDateTime.now());
        event2.setEventEndDate(LocalDateTime.now());
        event2.setSaleStartDate(LocalDateTime.now());
        event2.setSalesEndDate(LocalDateTime.now());
        event2.setImages(new ArrayList<>());
        event2.setBoothCapacity(305);
        event2.setRating(5);
        event2.setEventStatus(EventStatus.UPCOMING);

        Event event3 = new Event();
        event3.setName("Third Event");
        event3.setAddress("Sembwang3");
        event3.setDescriptions("Some description two3");
        event3.setPhysical(false);
        event3.setEventStartDate(LocalDateTime.now());
        event3.setEventEndDate(LocalDateTime.now());
        event3.setSaleStartDate(LocalDateTime.now());
        event3.setSalesEndDate(LocalDateTime.now());
        event3.setImages(new ArrayList<>());
        event3.setBoothCapacity(305);
        event3.setRating(5);
        event3.setEventStatus(EventStatus.UPCOMING);

        Event event4 = new Event();
        event4.setName("Fourth Event");
        event4.setAddress("Sembwang4");
        event4.setDescriptions("Some description 4");
        event4.setPhysical(false);
        event4.setEventStartDate(LocalDateTime.now());
        event4.setEventEndDate(LocalDateTime.now());
        event4.setSaleStartDate(LocalDateTime.now());
        event4.setSalesEndDate(LocalDateTime.now());
        event4.setImages(new ArrayList<>());
        event4.setBoothCapacity(305);
        event4.setRating(5);
        event4.setEventStatus(EventStatus.UPCOMING);

        Event event5 = new Event();
        event5.setName("Fiveth Event");
        event5.setAddress("Sembwang 5");
        event5.setDescriptions("Some description 5");
        event5.setPhysical(false);
        event5.setEventStartDate(LocalDateTime.now());
        event5.setEventEndDate(LocalDateTime.now());
        event5.setSaleStartDate(LocalDateTime.now());
        event5.setSalesEndDate(LocalDateTime.now());
        event5.setImages(new ArrayList<>());
        event5.setBoothCapacity(305);
        event5.setRating(5);
        event5.setEventStatus(EventStatus.UPCOMING);

        Event event6 = new Event();
        event6.setName("Sixth Event");
        event6.setAddress("Sembwang 6");
        event6.setDescriptions("Some description 6");
        event6.setPhysical(false);
        event6.setEventStartDate(LocalDateTime.now());
        event6.setEventEndDate(LocalDateTime.now());
        event6.setSaleStartDate(LocalDateTime.now());
        event6.setSalesEndDate(LocalDateTime.now());
        event6.setImages(new ArrayList<>());
        event6.setBoothCapacity(305);
        event6.setRating(5);
        event6.setEventStatus(EventStatus.UPCOMING);


        EventOrganiser eventOrg = eventOrganiserRepository.findByEmail("organiser@abc.com");
        event.setEventOrganiser(eventOrg);
        event2.setEventOrganiser(eventOrg);
        event3.setEventOrganiser(eventOrg);
        event4.setEventOrganiser(eventOrg);
        event5.setEventOrganiser(eventOrg);
        event6.setEventOrganiser(eventOrg);
        eventRepository.save(event);
        eventRepository.save(event2);
        eventRepository.save(event3);
        eventRepository.save(event4);
        eventRepository.save(event5);
        eventRepository.save(event6);

        // List<Event> eoEvents = new ArrayList<>();
        // eoEvents.add(event);
        // eoEvents.add(event2);
        // eoEvents.add(event3);
        // eoEvents.add(event4);
        // eoEvents.add(event5);
        // eoEvents.add(event6);
        // eventOrg.setEvents(eoEvents);
        // eventOrganiserRepository.save(eventOrg);
     
    }


}
