package com.is4103.backend.util;

import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import javax.transaction.Transactional;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BoothApplicationStatus;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.EventStatus;
import com.is4103.backend.model.PaymentStatus;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.model.User;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventBoothTransaction;
import com.is4103.backend.repository.RoleRepository;
import com.is4103.backend.repository.UserRepository;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import com.is4103.backend.repository.EventOrganiserRepository;
import com.is4103.backend.repository.EventBoothTransactionRepository;
import com.is4103.backend.repository.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;

@Component
public class DataInitRunner implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventOrganiserRepository eoRepository;

    @Autowired
    private EventBoothTransactionRepository eventBoothTransactionRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventOrganiserRepository eventOrganiserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private EventOrganiser eoTest;

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
            createBizPartners();
        }

        // Testing Entities
        if (eventRepository.findAll().isEmpty()) {
            createEvent();

        }
        if (eventRepository.findByName("Event 0").isEmpty()) {
            createDemoEvents();
        }
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

        this.eoTest = eo;

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
    private void createBizPartners() {

        // create first Bp
        BusinessPartner bp = new BusinessPartner();
        bp.setEmail("partner@abc.com");
        bp.setName("First Business Partner");
        bp.setPassword(passwordEncoder.encode("password"));
        bp.setEnabled(true);
        bp.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.BIZPTNR)));
        bp.setBusinessCategory("Travel");

        // set follow eo list for bp
        List<EventOrganiser> following = new ArrayList<>();
        following.add(this.eoTest);
        bp.setFollowEventOrganisers(following);
        userRepository.save(bp);

        // set followers bp list for eo
        List<BusinessPartner> followersBP = new ArrayList<>();
        followersBP.add(bp);
        this.eoTest.setBusinessPartnerFollowers(followersBP);
        userRepository.save(this.eoTest);

        // create attendee
        Attendee atn = new Attendee();
        atn.setEmail("attendee@abc.com");
        atn.setName("first attendee");
        atn.setPassword(passwordEncoder.encode("password"));
        atn.setDescription("description for frst attendeeeeeeee :)");
        atn.setEnabled(true);
        atn.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.ATND)));
        List<String> category = new ArrayList<>();
        category.add("Travel");
        category.add("Healthcare");
        atn.setCategoryPreferences(category);
        userRepository.save(atn);
        // set following bp list for attendees got issues here
        Set<BusinessPartner> followBp = new HashSet<>();
        followBp.add(bp);
        atn.setFollowedBusinessPartners(followBp);
        userRepository.save(atn);

        // create second attendee
        Attendee atnTwo = new Attendee();
        atnTwo.setEmail("attendeeTwo@abc.com");
        atnTwo.setName("Second attendee");
        atnTwo.setPassword(passwordEncoder.encode("password"));
        atnTwo.setDescription("description for Second attendeeeeeeee :)");
        atnTwo.setEnabled(true);
        atnTwo.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.ATND)));
        atnTwo.setCategoryPreferences(category);
        // Set<BusinessPartner> followBpTwo = new HashSet<>();
        // followBpTwo.add(bp);
        // atnTwo.setFollowedBusinessPartners(followBpTwo);
        userRepository.save(atnTwo);

        // set bp followers list
        Set<Attendee> followers = new HashSet<>();
        followers.add(atn);
        followers.add(atnTwo);
        bp.setAttendeeFollowers(followers);
        userRepository.save(bp);

        for (int i = 2; i <= 11; i++) {
            bp = new BusinessPartner();
            bp.setEmail("partner" + i + "@abc.com");
            bp.setName("Partner " + i);
            bp.setPassword(passwordEncoder.encode("password"));
            bp.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.BIZPTNR)));
            userRepository.save(bp);
        }
    }

    // Testing Methods
    @Transactional
    private void createEvent() {
        // sales started = current event for attendee
        Event event = new Event();
        event.setName("2021 Academic Success Lecture");
        event.setAddress("Woodlands Avenue 6 #87-10");
        event.setDescriptions(
                "The 14th Annual Academic Success Lecture featuring Dr. Kevin Gumienny is . This year's presentation will be held physically, and will focus on the topics of accessibility and universal design.");
        event.setPhysical(false);
        LocalDateTime eventStart1 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd1 = LocalDateTime.of(2021, Month.JUNE, 2, 9, 0).plusDays(15).plusHours(2 % 3);

        event.setEventStartDate(eventStart1);
        event.setEventEndDate(eventEnd1);

        LocalDateTime salesStart1 = LocalDateTime.of(2021, Month.JANUARY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd1 = LocalDateTime.of(2021, Month.APRIL, 2, 9, 0).plusDays(15).plusHours(2 % 3);

        event.setSaleStartDate(salesStart1);
        event.setSalesEndDate(salesEnd1);
        event.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 1 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 1 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 1 + "/image-3.jpg"));
        event.setBoothCapacity(305);
        event.setRating(0);
        event.setEventStatus(EventStatus.CREATED);
        event.setHidden(false);
        event.setPublished(true);

        Event event2 = new Event();
        event2.setName("IT Fair 2021");
        event2.setAddress("Sembwang2");
        event2.setDescriptions("Description 2");
        event2.setPhysical(false);
        LocalDateTime eventStart = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        event2.setEventStartDate(eventStart);
        LocalDateTime eventEnd = LocalDateTime.of(2021, Month.JUNE, 1, 9, 0).plusDays(15).plusHours(2 % 3);
        event2.setEventEndDate(eventEnd);

        LocalDateTime salesStart = LocalDateTime.of(2021, Month.JANUARY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event2.setSaleStartDate(salesStart);
        event2.setSalesEndDate(salesEnd);
        event2.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 2 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 2 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 2 + "/image-3.jpg"));
        event2.setBoothCapacity(305);
        event2.setRating(0);
        event2.setEventStatus(EventStatus.CREATED);
        event2.setHidden(false);
        event2.setPublished(true);

        Event event3 = new Event();
        event3.setName("Career Fair");
        event3.setAddress("Sembwang3");
        event3.setDescriptions("Some description two3");
        event3.setPhysical(false);
        LocalDateTime eventStart3 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd3 = LocalDateTime.of(2021, Month.JUNE, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event3.setEventStartDate(eventStart3);
        event3.setEventEndDate(eventEnd3);

        LocalDateTime salesStart3 = LocalDateTime.of(2021, Month.FEBRUARY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd3 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(15).plusHours(2 % 3);
        event3.setSaleStartDate(salesStart3);
        event3.setSalesEndDate(salesEnd3);
        event3.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 3 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 3 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 3 + "/image-3.jpg"));
        event3.setBoothCapacity(305);
        event3.setRating(0);
        event3.setEventStatus(EventStatus.CREATED);
        event3.setHidden(false);
        event3.setPublished(true);

        // attendee/eo upcoming
        Event event4 = new Event();
        event4.setName("Fintech 2021");
        event4.setAddress("Sembwang4");
        event4.setDescriptions("Some description 4");
        event4.setPhysical(false);
        LocalDateTime eventStart4 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd4 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4.setEventStartDate(eventStart4);
        event4.setEventEndDate(eventEnd4);

        LocalDateTime salesStart4 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd4 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4.setSaleStartDate(salesStart4);
        event4.setSalesEndDate(salesEnd4);

        event4.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 4 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 4 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 4 + "/image-3.jpg"));
        event4.setBoothCapacity(305);
        event4.setRating(0);
        event4.setEventStatus(EventStatus.CREATED);
        event4.setHidden(false);
        event4.setPublished(true);

        Event event4_1 = new Event();
        event4_1.setName("SG Career Fair 2021");
        event4_1.setAddress("Sembwang4");
        event4_1.setDescriptions("Some description 4");
        event4_1.setPhysical(false);
        LocalDateTime eventStart4_1 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd4_1 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_1.setEventStartDate(eventStart4_1);
        event4_1.setEventEndDate(eventEnd4_1);

        LocalDateTime salesStart4_1 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd4_1 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_1.setSaleStartDate(salesStart4_1);
        event4_1.setSalesEndDate(salesEnd4_1);

        event4_1.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 5 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 5 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 5 + "/image-3.jpg"));
        event4_1.setBoothCapacity(305);
        event4_1.setRating(0);
        event4_1.setEventStatus(EventStatus.CREATED);
        event4_1.setHidden(false);
        event4_1.setPublished(true);

        Event event4_2 = new Event();
        event4_2.setName("Tech Festival 2021");
        event4_2.setAddress("Sembwang4");
        event4_2.setDescriptions("Some description 4");
        event4_2.setPhysical(false);
        LocalDateTime eventStart4_2 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd4_2 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_2.setEventStartDate(eventStart4_2);
        event4_2.setEventEndDate(eventEnd4_2);

        LocalDateTime salesStart4_2 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd4_2 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_2.setSaleStartDate(salesStart4_2);
        event4_2.setSalesEndDate(salesEnd4_2);

        event4_2.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 6 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 6 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 6 + "/image-3.jpg"));
        event4_2.setBoothCapacity(305);
        event4_2.setRating(0);
        event4_2.setEventStatus(EventStatus.CREATED);
        event4_2.setHidden(false);
        event4_2.setPublished(true);

        Event event4_3 = new Event();
        event4_3.setName("Viva Technology 2021");
        event4_3.setAddress("Sembwang4");
        event4_3.setDescriptions("Some description 4");
        event4_3.setPhysical(false);
        LocalDateTime eventStart4_3 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd4_3 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_3.setEventStartDate(eventStart4_3);
        event4_3.setEventEndDate(eventEnd4_3);

        LocalDateTime salesStart4_3 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd4_3 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_3.setSaleStartDate(salesStart4_3);
        event4_3.setSalesEndDate(salesEnd4_3);

        event4_3.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 7 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 7 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 7 + "/image-3.jpg"));
        event4_3.setBoothCapacity(305);
        event4_3.setRating(0);
        event4_3.setEventStatus(EventStatus.CREATED);
        event4_3.setHidden(false);
        event4_3.setPublished(true);
        // past events
        Event event5 = new Event();
        event5.setName("Singapore Food Festival");
        event5.setAddress("Sembwang 5");
        event5.setDescriptions("Some description 5");
        event5.setPhysical(false);
        LocalDateTime eventStart5 = LocalDateTime.of(2021, Month.JANUARY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd5 = LocalDateTime.of(2021, Month.JANUARY, 2, 9, 0).plusDays(15).plusHours(2 % 3);
        event5.setEventStartDate(eventStart5);
        event5.setEventEndDate(eventEnd5);
        LocalDateTime salesStart5 = LocalDateTime.of(2020, Month.DECEMBER, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd5 = LocalDateTime.of(2020, Month.DECEMBER, 2, 9, 0).plusDays(15).plusHours(2 % 3);
        event5.setSaleStartDate(salesStart5);
        event5.setSalesEndDate(salesEnd5);
        event5.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 8 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 8 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 8 + "/image-3.jpg"));
        event5.setBoothCapacity(305);
        event5.setRating(5);
        event5.setEventStatus(EventStatus.CREATED);
        event5.setHidden(false);
        event5.setPublished(true);

        // past events
        Event event6 = new Event();
        event6.setName("Singapore Tech Conferences 2020");
        event6.setAddress("Sembwang 6");
        event6.setDescriptions("Some description 6");
        event6.setPhysical(false);
        event6.setEventStartDate(LocalDateTime.now());
        event6.setEventEndDate(LocalDateTime.now());
        event6.setSaleStartDate(LocalDateTime.now());
        event6.setSalesEndDate(LocalDateTime.now());
        event6.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 9 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 9 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 9 + "/image-3.jpg"));
        event6.setBoothCapacity(305);
        event6.setRating(5);
        event6.setEventStatus(EventStatus.CREATED);
        event6.setHidden(false);
        event6.setPublished(true);

        // past events
        Event event7 = new Event();
        event7.setName("Tech Analystics 2020");
        event7.setAddress("Sembwang 6");
        event7.setDescriptions("Some description 6");
        event7.setPhysical(false);
        event7.setEventStartDate(LocalDateTime.now());
        event7.setEventEndDate(LocalDateTime.now());
        event7.setSaleStartDate(LocalDateTime.now());
        event7.setSalesEndDate(LocalDateTime.now());
        event7.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 10 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 10 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 10 + "/image-3.jpg"));
        event7.setBoothCapacity(305);
        event7.setRating(5);
        event7.setEventStatus(EventStatus.CREATED);
        event7.setHidden(false);
        event7.setPublished(true);

        // past events
        Event event8 = new Event();
        event8.setName("Tech Conferences 2020");
        event8.setAddress("Sembwang 6");
        event8.setDescriptions("Some description 6");
        event8.setPhysical(false);
        event8.setEventStartDate(LocalDateTime.now());
        event8.setEventEndDate(LocalDateTime.now());
        event8.setSaleStartDate(LocalDateTime.now());
        event8.setSalesEndDate(LocalDateTime.now());
        event8.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 11 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 11 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 11 + "/image-3.jpg"));
        event8.setBoothCapacity(305);
        event8.setRating(5);
        event8.setEventStatus(EventStatus.CREATED);
        event8.setHidden(false);
        event8.setPublished(true);

        // past events
        Event event9 = new Event();
        event9.setName("Tech Conferences 2020");
        event9.setAddress("Sembwang 6");
        event9.setDescriptions("Some description 6");
        event9.setPhysical(false);
        event9.setEventStartDate(LocalDateTime.now());
        event9.setEventEndDate(LocalDateTime.now());
        event9.setSaleStartDate(LocalDateTime.now());
        event9.setSalesEndDate(LocalDateTime.now());
        event9.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 12 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 12 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 12 + "/image-3.jpg"));
        event9.setBoothCapacity(305);
        event9.setRating(5);
        event9.setEventStatus(EventStatus.CREATED);
        event9.setHidden(false);
        event9.setPublished(true);

        EventOrganiser eventOrg = eventOrganiserRepository.findByEmail("organiser@abc.com");
        event.setEventOrganiser(eventOrg);
        event2.setEventOrganiser(eventOrg);
        event3.setEventOrganiser(eventOrg);
        event4.setEventOrganiser(eventOrg);
        event4_1.setEventOrganiser(eventOrg);
        event4_2.setEventOrganiser(eventOrg);
        event4_3.setEventOrganiser(eventOrg);
        event5.setEventOrganiser(eventOrg);
        event6.setEventOrganiser(eventOrg);

        event7.setEventOrganiser(eventOrg);
        event8.setEventOrganiser(eventOrg);
        event9.setEventOrganiser(eventOrg);

        eventRepository.save(event);
        eventRepository.save(event2);
        eventRepository.save(event3);
        eventRepository.save(event4);
        eventRepository.save(event4_1);
        eventRepository.save(event4_2);
        eventRepository.save(event4_3);
        eventRepository.save(event5);
        eventRepository.save(event6);
        eventRepository.save(event7);
        eventRepository.save(event8);
        eventRepository.save(event9);

        List<Event> eoEvents = new ArrayList<>();
        // eoEvents = eventOrg.getEvents();
        eoEvents.add(event);
        eoEvents.add(event2);
        eventOrg.setEvents(eoEvents);
        event.setImages(Arrays.asList(
                "https://storage.googleapis.com/ems-images/events/event-1/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-2/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-3/image-3.jpg"));

        EventOrganiser eo = eoRepository.findByEmail("organiser@abc.com");
        event.setEventOrganiser(eo);
        eventRepository.save(event);

        EventBoothTransaction transaction = new EventBoothTransaction();
        transaction.setEvent(event);
        eventBoothTransactionRepository.save(transaction);
    }

    private void createDemoEvents() {
        Lorem lorem = LoremIpsum.getInstance();
        Random rand = new Random();

        EventOrganiser eo = eoRepository.findByEmail("organiser@abc.com");
        for (int i = 0; i < 25; i++) {
            Event e = new Event();
            e.setName("Event " + i);
            e.setEventOrganiser(eo);
            e.setAddress("Singapore");
            e.setDescriptions(lorem.getWords(5, 20));
            e.setTicketPrice(Math.round(rand.nextFloat() * 20));
            e.setTicketCapacity(rand.nextInt(100));
            e.setPhysical(true);
            LocalDateTime eventStart = LocalDateTime.of(2021, Month.MARCH, 1, 9, 0).plusDays(i).plusHours(i % 3);
            e.setEventStartDate(eventStart);
            e.setEventEndDate(
                    LocalDateTime.of(2021, Month.MARCH, 2, 17, 30).plusDays(rand.nextInt(5)).minusHours(i % 2));
            e.setSaleStartDate(LocalDateTime.now());
            e.setSalesEndDate(eventStart.minusDays(2));

            e.setImages(Arrays.asList(
                    "https://storage.googleapis.com/ems-images/events/event-" + i + "/image-1.jpg",
                    "https://storage.googleapis.com/ems-images/events/event-" + i + "/image-2.jpg",
                    "https://storage.googleapis.com/ems-images/events/event-" + i + "/image-3.jpg"));
            e.setBoothCapacity(rand.nextInt(50));
            e.setEventStatus(EventStatus.CREATED);
            e.setPublished(true);
            eventRepository.save(e);
        }
    }

}
