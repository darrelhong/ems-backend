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
import com.is4103.backend.repository.RoleRepository;
import com.is4103.backend.repository.UserRepository;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import com.is4103.backend.repository.EventOrganiserRepository;
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
        if (eventRepository.findAll().isEmpty()){
            createEvent();
           
        }
        if (eventRepository.findById((long) 2).isEmpty()) {
            createDemoEvents();
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

        //create first Bp
        BusinessPartner bp = new BusinessPartner();
        bp.setEmail("partner@abc.com");
        bp.setName("First Business Partner");
        bp.setPassword(passwordEncoder.encode("password"));
        bp.setEnabled(true);
        bp.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.BIZPTNR)));
        bp.setBusinessCategory("Travel");
        

        //set follow eo list for bp
        List<EventOrganiser> following = new ArrayList<>();
        following.add(this.eoTest);
        bp.setFollowEventOrganisers(following);
        userRepository.save(bp);

        
        //set followers bp list for eo
        List<BusinessPartner> followersBP = new ArrayList<>();
        followersBP.add(bp);
        this.eoTest.setBusinessPartnerFollowers(followersBP);
        userRepository.save(this.eoTest);

        //create attendee
        Attendee atn = new Attendee();
        atn.setEmail("attendee@abc.com");
        atn.setName("first attendee");
        atn.setPassword(passwordEncoder.encode("password"));
        atn.setDescription("description for frst attendeeeeeeee :)");
        atn.setEnabled(true);
        atn.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.ATND)));
        List<String> category= new ArrayList<>();
        category.add("Travel");
        category.add("Healthcare");
        atn.setCategoryPreferences(category);
        userRepository.save(atn);
        //set following bp list for attendees got issues here
        Set<BusinessPartner> followBp = new HashSet<>();
        followBp.add(bp);
        atn.setFollowedBusinessPartners(followBp);
        userRepository.save(atn);

        //create second attendee
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

        //set bp followers list 
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
        event.setHidden(true);
        event.setPublished(true);

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
        event2.setHidden(false);
        event2.setPublished(true);

        Event event3 = new Event();
        event3.setName("Third Event");
        event3.setAddress("Sembwang3");
        event3.setDescriptions("Some description two3");
        event3.setPhysical(false);
        event3.setEventStartDate(LocalDateTime.now());
        event3.setEventEndDate(LocalDateTime.now());
        event3.setSaleStartDate(LocalDateTime.now());
        event3.setSalesEndDate(LocalDateTime.now());
        event3.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 3 + "/image-1.jpg",
                    "https://storage.googleapis.com/ems-images/events//event-" + 3 + "/image-2.jpg",
                    "https://storage.googleapis.com/ems-images/events//event-" + 3 + "/image-3.jpg"));
        event3.setBoothCapacity(305);
        event3.setRating(5);
        event3.setEventStatus(EventStatus.UPCOMING);
        event3.setHidden(false);
        event3.setPublished(false);

        Event event4 = new Event();
        event4.setName("Fourth Event");
        event4.setAddress("Sembwang4");
        event4.setDescriptions("Some description 4");
        event4.setPhysical(false);
        event4.setEventStartDate(LocalDateTime.now());
        event4.setEventEndDate(LocalDateTime.now());
        event4.setSaleStartDate(LocalDateTime.now());
        event4.setSalesEndDate(LocalDateTime.now());
        event4.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 4 + "/image-1.jpg",
                    "https://storage.googleapis.com/ems-images/events//event-" + 4 + "/image-2.jpg",
                    "https://storage.googleapis.com/ems-images/events//event-" + 4 + "/image-3.jpg"));
        event4.setBoothCapacity(305);
        event4.setRating(5);
        event4.setEventStatus(EventStatus.UPCOMING);
        event4.setHidden(true);
        event4.setPublished(false);

        Event event5 = new Event();
        event5.setName("Fiveth Event");
        event5.setAddress("Sembwang 5");
        event5.setDescriptions("Some description 5");
        event5.setPhysical(false);
        event5.setEventStartDate(LocalDateTime.now());
        event5.setEventEndDate(LocalDateTime.now());
        event5.setSaleStartDate(LocalDateTime.now());
        event5.setSalesEndDate(LocalDateTime.now());
        event5.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 5 + "/image-1.jpg",
                    "https://storage.googleapis.com/ems-images/events//event-" + 5 + "/image-2.jpg",
                    "https://storage.googleapis.com/ems-images/events//event-" +  5 + "/image-3.jpg"));
        event5.setBoothCapacity(305);
        event5.setRating(5);
        event5.setEventStatus(EventStatus.UPCOMING);
        event5.setHidden(false);
        event5.setPublished(true);


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
        event6.setHidden(false);
        event6.setPublished(true);


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


        List<Event> eoEvents = new ArrayList<>();
        // eoEvents = eventOrg.getEvents();
        eoEvents.add(event);
        eoEvents.add(event2);
        eventOrg.setEvents(eoEvents);
    }


    private void createDemoEvents() {
        // Lorem lorem = LoremIpsum.getInstance();
        Random rand = new Random();

        EventOrganiser eo = eoRepository.findByEmail("organiser@abc.com");
        for (int i = 0; i < 25; i++) {
            Event e = new Event();
            e.setName("Event " + i);
            e.setEventOrganiser(eo);
            e.setAddress("Singapore");
            // e.setDescriptions(lorem.getWords(5, 20));
            e.setDescriptions("lorem.getWords(5, 20)");
            e.setTicketPrice(Math.round(rand.nextFloat() * 20));
            e.setTicketCapacity(rand.nextInt(100));
            e.setPhysical(true);
            LocalDateTime eventStart = LocalDateTime.of(2022, Month.MARCH, 1, 9, 0).plusDays(i).plusHours(i % 3);
            e.setEventStartDate(eventStart);
            e.setEventEndDate(
                    LocalDateTime.of(2022, Month.MARCH, 2, 17, 30).plusDays(rand.nextInt(5)).minusHours(i % 2));
            e.setSaleStartDate(LocalDateTime.now());
            e.setSalesEndDate(eventStart.minusDays(2));

            e.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + i + "/image-1.jpg",
                    "https://storage.googleapis.com/ems-images/events//event-" + i + "/image-2.jpg",
                    "https://storage.googleapis.com/ems-images/events//event-" + i + "/image-3.jpg"));
            e.setBoothCapacity(rand.nextInt(50));
            e.setEventStatus(EventStatus.UPCOMING);
            e.setPublished(true);
            eventRepository.save(e);
        }
    }


}
