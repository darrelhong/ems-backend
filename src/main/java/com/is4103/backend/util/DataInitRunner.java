package com.is4103.backend.util;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import javax.transaction.Transactional;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.Booth;
import com.is4103.backend.model.BoothApplicationStatus;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.EventStatus;
import com.is4103.backend.model.PaymentStatus;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.model.SellerApplication;
import com.is4103.backend.model.SellerApplicationStatus;
import com.is4103.backend.model.TicketTransaction;
import com.is4103.backend.model.User;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.Product;
import com.is4103.backend.model.SellerProfile;
import com.is4103.backend.repository.RoleRepository;
import com.is4103.backend.repository.UserRepository;
import com.is4103.backend.repository.SellerApplicationRepository;
import com.is4103.backend.service.AttendeeService;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import com.is4103.backend.repository.BusinessPartnerRepository;
import com.is4103.backend.repository.EventOrganiserRepository;
import com.is4103.backend.repository.EventRepository;
import com.is4103.backend.repository.BoothRepository;
import com.is4103.backend.repository.SellerProfileRepository;
import com.is4103.backend.repository.TicketTransactionRepository;
import com.is4103.backend.repository.ProductRepository;

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
    private SellerApplicationRepository sellerApplicationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AttendeeService attendeeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessPartnerRepository businessPartnerRepository;

    @Autowired
    private EventOrganiserRepository eoRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventOrganiserRepository eventOrganiserRepository;

    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    @Autowired
    private BoothRepository boothRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TicketTransactionRepository ticketTransactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private EventOrganiser eoTest;

    // lili eo
    private EventOrganiser eoLili;

    private String[] eventCategories = { "Automotive", "Business Support & Supplies", "Computers & Electronics",
            "Computers", "Construction & Contractor", "Education", "Entertainment", "Food & Dining",
            "Health & Medicine", "Home & Garden", "Legal & Financial", "Manufacturing, Wholesale, Distribution",
            "Merchants (Retail)", "Personal Care & Services", "Real Estate", "Travel & Transportation" };

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
            roleRepository.save(new Role(RoleEnum.BIZPTNR, "Business Partner"));
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
            // lili
            createDemoEvents();
        }

        if (boothRepository.findAll().isEmpty()) {
            createProducts();
            // createBoothsAndProfiles();
        }

        if (sellerApplicationRepository.findAll().isEmpty()) {
            createBooths();
            createSellerApplications();
            //setProducts(); //not working atm, get the lazy load issue
        }
        System.out.println("Data init done");
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
        eo.setSupportDocsUrl("https://storage.googleapis.com/ems-docs/organiser-document.zip");
        // eo.setApproved(true);
        userRepository.save(eo);

        this.eoTest = eo;

        // Lili EO
        EventOrganiser eo2 = new EventOrganiser();
        eo2.setEmail("linlili7842@gmail.com");
        eo2.setName("Lili Organiser");
        eo2.setPassword(passwordEncoder.encode("password"));
        eo2.setEnabled(true);
        eo2.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.EVNTORG)));
        eo2.setSupportDocsUrl(null);
        userRepository.save(eo2);
        this.eoLili = eo2;

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
        bp.setBusinessCategory("Home & Garden");

        // lili bp
        BusinessPartner bp2 = new BusinessPartner();
        bp2.setEmail("linlili2319@gmail.com");
        bp2.setName("Lili Business Partner");
        bp2.setPassword(passwordEncoder.encode("password"));
        bp2.setEnabled(true);
        bp2.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.BIZPTNR)));
        bp2.setBusinessCategory("Home & Garden");

        // set follow eo list for bp
        List<EventOrganiser> following = new ArrayList<>();
        following.add(this.eoTest);
        // lili
        following.add(this.eoLili);
        bp.setFollowEventOrganisers(following);
        // lili
        bp2.setFollowEventOrganisers(following);
        userRepository.save(bp);
        // lili
        userRepository.save(bp2);

        // set followers bp list for eo
        List<BusinessPartner> followersBP = new ArrayList<>();
        followersBP.add(bp);
        followersBP.add(bp2);
        this.eoTest.setBusinessPartnerFollowers(followersBP);
        // lili
        this.eoLili.setBusinessPartnerFollowers(followersBP);
        userRepository.save(this.eoTest);
        userRepository.save(this.eoLili);

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
        // atn.addfollowBP(businesspartner);
        List<BusinessPartner> bpFollowing = new ArrayList<>();
        bpFollowing.add(bp);
        atn.setFollowedBusinessPartners(bpFollowing);
        atn.setStripeCustomerId("cus_JFCHLoDF4Gr3sI");
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
        // atnTwo.addfollowBP(businesspartner);
        atnTwo.setFollowedBusinessPartners(bpFollowing);
        userRepository.save(atnTwo);

        // lili att
        // create second attendee
        Attendee atnLili = new Attendee();
        atnLili.setEmail("linlili53012@gmail.com");
        atnLili.setName("Lili attendee");
        atnLili.setPassword(passwordEncoder.encode("password"));
        atnLili.setDescription("description for Second attendeeeeeeee :)");
        atnLili.setEnabled(true);
        atnLili.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.ATND)));
        atnLili.setCategoryPreferences(category);
        // atnTwo.addfollowBP(businesspartner);
        atnLili.setFollowedBusinessPartners(bpFollowing);
        userRepository.save(atnLili);

        // set atn and atn2 follow bp list

        // Set<BusinessPartner> followBp = new HashSet<>();
        // followBp.add(bp);
        // atn.setFollowedBusinessPartners(followBp);
        // atnTwo.setFollowedBusinessPartners(followBp);
        // userRepository.save(atn);
        // userRepository.save(atnTwo);

        // set bp followers list
        List<Attendee> followers = new ArrayList<>();
        followers.add(atn);
        followers.add(atnTwo);
        // lili
        followers.add(atnLili);
        bp.setAttendeeFollowers(followers);
        bp2.setAttendeeFollowers(followers);
        userRepository.save(bp);
        userRepository.save(bp2);

        for (int i = 2; i <= 11; i++) {
            bp = new BusinessPartner();
            bp.setEmail("partner" + i + "@abc.com");
            bp.setName("Partner " + i);
            bp.setEnabled(true);
            bp.setPassword(passwordEncoder.encode("password"));
            bp.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.BIZPTNR)));
            Random rand = new Random();
            int randomInt = rand.nextInt(eventCategories.length);
            bp.setBusinessCategory(eventCategories[randomInt]);
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
        // event.setCategories(Arrays.asList(eventCategories));
        event.setCategory(eventCategories[0]);
        event.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event.setPhysical(true);
        LocalDateTime eventStart1 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd1 = LocalDateTime.of(2021, Month.JUNE, 2, 9, 0).plusDays(15).plusHours(2 % 3);

        event.setSellingTicket(true);
        event.setTicketPrice(24);
        event.setTicketCapacity(100);
        event.setEventStartDate(eventStart1);
        event.setEventEndDate(eventEnd1);

        LocalDateTime salesStart1 = LocalDateTime.of(2021, Month.JANUARY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd1 = LocalDateTime.of(2021, Month.APRIL, 2, 9, 0).plusDays(15).plusHours(2 % 3);

        event.setSaleStartDate(salesStart1);
        event.setSalesEndDate(salesEnd1);
        event.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 1 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 1 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 1 + "/image-3.jpg"));
        event.setBoothPrice(39);
        event.setBoothCapacity(305);
        event.setRating(0);
        event.setEventStatus(EventStatus.CREATED);
        event.setHidden(false);
        event.setPublished(true);
        // List<Booth> booths = new ArrayList<>();
        // booths.add(new Booth(199.0, 5.0, 4.5, event));
        // booths.add(new Booth(299.0, 6.3, 5.4, event));
        // event.setBooths(booths);

        // EventBoothTransaction transaction = new EventBoothTransaction();
        // transaction.setEvent(event);
        // eventBoothTransactionRepository.save(transaction);

        // EventBoothTransaction transaction = new EventBoothTransaction();
        // transaction.setEvent(event);
        // eventBoothTransactionRepository.save(transaction);

        Event event2 = new Event();
        event2.setName("IT Fair 2021");
        event2.setAddress("Sembwang2");
        event2.setDescriptions("Description 2");
        event2.setCategory(eventCategories[0]);
        event2.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event2.setPhysical(true);
        LocalDateTime eventStart = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        event2.setEventStartDate(eventStart);
        LocalDateTime eventEnd = LocalDateTime.of(2021, Month.JUNE, 1, 9, 0).plusDays(15).plusHours(2 % 3);
        event2.setEventEndDate(eventEnd);

        LocalDateTime salesStart = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd = LocalDateTime.of(2021, Month.APRIL, 10, 9, 0).plusDays(15).plusHours(2 % 3);

        event2.setSellingTicket(true);
        event2.setTicketPrice(24);
        event2.setTicketCapacity(100);
        event2.setSaleStartDate(salesStart);
        event2.setSalesEndDate(salesEnd);
        event2.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 2 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 2 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 2 + "/image-3.jpg"));

        event2.setBoothPrice(39);
        event2.setBoothCapacity(305);
        event2.setRating(0);
        event2.setEventStatus(EventStatus.CREATED);
        event2.setHidden(false);
        event2.setPublished(true);

        Event event3 = new Event();
        event3.setName("Career Fair");
        event3.setAddress("Sembwang3");
        event3.setDescriptions("Some description two3");
        event3.setCategory(eventCategories[1]);
        event3.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event3.setPhysical(true);
        LocalDateTime eventStart3 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd3 = LocalDateTime.of(2021, Month.JUNE, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event3.setEventStartDate(eventStart3);
        event3.setEventEndDate(eventEnd3);

        LocalDateTime salesStart3 = LocalDateTime.of(2021, Month.FEBRUARY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd3 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(15).plusHours(2 % 3);
        event3.setTicketPrice(24);
        event3.setTicketCapacity(100);
        event3.setSaleStartDate(salesStart3);
        event3.setSalesEndDate(salesEnd3);
        event3.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 3 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 3 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 3 + "/image-3.jpg"));
        event3.setBoothPrice(39);
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
        event4.setCategory(eventCategories[1]);
        event4.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event4.setPhysical(true);
        LocalDateTime eventStart4 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd4 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4.setTicketPrice(24);
        event4.setTicketCapacity(100);
        event4.setEventStartDate(eventStart4);
        event4.setEventEndDate(eventEnd4);

        LocalDateTime salesStart4 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd4 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4.setSaleStartDate(salesStart4);
        event4.setSalesEndDate(salesEnd4);

        event4.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 4 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 4 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 4 + "/image-3.jpg"));
        event4.setBoothPrice(39);
        event4.setBoothCapacity(305);
        event4.setRating(0);
        event4.setEventStatus(EventStatus.CREATED);
        event4.setHidden(false);
        event4.setPublished(true);

        Event event4_1 = new Event();
        event4_1.setName("SG Career Fair 2021");
        event4_1.setAddress("Sembwang4");
        event4_1.setDescriptions("Some description 4");
        event4_1.setCategory(eventCategories[1]);
        event4_1.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event4_1.setPhysical(true);
        LocalDateTime eventStart4_1 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd4_1 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_1.setTicketPrice(24);
        event4_1.setTicketCapacity(100);
        event4_1.setEventStartDate(eventStart4_1);
        event4_1.setEventEndDate(eventEnd4_1);

        LocalDateTime salesStart4_1 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd4_1 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_1.setSaleStartDate(salesStart4_1);
        event4_1.setSalesEndDate(salesEnd4_1);

        event4_1.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 5 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 5 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 5 + "/image-3.jpg"));
        event4_1.setBoothPrice(39);
        event4_1.setBoothCapacity(305);
        event4_1.setRating(0);
        event4_1.setEventStatus(EventStatus.CREATED);
        event4_1.setHidden(false);
        event4_1.setPublished(true);

        Event event4_2 = new Event();
        event4_2.setName("Tech Festival 2021");
        event4_2.setAddress("Sembwang4");
        event4_2.setDescriptions("Some description 4");
        event4_2.setCategory(eventCategories[2]);
        event4_2.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event4_2.setPhysical(true);
        LocalDateTime eventStart4_2 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd4_2 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_2.setTicketPrice(24);
        event4_2.setTicketCapacity(100);
        event4_2.setEventStartDate(eventStart4_2);
        event4_2.setEventEndDate(eventEnd4_2);

        LocalDateTime salesStart4_2 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd4_2 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_2.setSaleStartDate(salesStart4_2);
        event4_2.setSalesEndDate(salesEnd4_2);

        event4_2.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 6 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 6 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 6 + "/image-3.jpg"));
        event4_2.setBoothPrice(39);
        event4_2.setBoothCapacity(305);
        event4_2.setRating(0);
        event4_2.setEventStatus(EventStatus.CREATED);
        event4_2.setHidden(false);
        event4_2.setPublished(true);

        Event event4_3 = new Event();
        event4_3.setName("Viva Technology 2021");
        event4_3.setAddress("Sembwang4");
        event4_3.setDescriptions("Some description 4");
        event4_3.setCategory(eventCategories[2]);
        event4_3.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event4_3.setPhysical(true);
        LocalDateTime eventStart4_3 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd4_3 = LocalDateTime.of(2021, Month.MAY, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_3.setTicketPrice(24);
        event4_3.setTicketCapacity(100);
        event4_3.setEventStartDate(eventStart4_3);
        event4_3.setEventEndDate(eventEnd4_3);

        LocalDateTime salesStart4_3 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd4_3 = LocalDateTime.of(2021, Month.APRIL, 1, 9, 0).plusDays(15).plusHours(2 % 3);

        event4_3.setSaleStartDate(salesStart4_3);
        event4_3.setSalesEndDate(salesEnd4_3);

        event4_3.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 7 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 7 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 7 + "/image-3.jpg"));
        event4_3.setBoothPrice(39);
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
        event5.setCategory(eventCategories[2]);
        event5.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event5.setPhysical(true);
        LocalDateTime eventStart5 = LocalDateTime.of(2021, Month.JANUARY, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime eventEnd5 = LocalDateTime.of(2021, Month.JANUARY, 2, 9, 0).plusDays(15).plusHours(2 % 3);
        event5.setEventStartDate(eventStart5);
        event5.setEventEndDate(eventEnd5);
        LocalDateTime salesStart5 = LocalDateTime.of(2020, Month.DECEMBER, 1, 9, 0).plusDays(2).plusHours(2 % 3);
        LocalDateTime salesEnd5 = LocalDateTime.of(2020, Month.DECEMBER, 2, 9, 0).plusDays(15).plusHours(2 % 3);

        event5.setTicketPrice(24);
        event5.setTicketCapacity(100);
        event5.setSaleStartDate(salesStart5);
        event5.setSalesEndDate(salesEnd5);
        event5.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 8 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 8 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 8 + "/image-3.jpg"));
        event5.setBoothPrice(39);
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
        event6.setCategory(eventCategories[2]);
        event6.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event6.setPhysical(true);
        event6.setEventStartDate(LocalDateTime.now());
        event6.setEventEndDate(LocalDateTime.now());
        event6.setTicketPrice(24);
        event6.setTicketCapacity(100);
        event6.setSaleStartDate(LocalDateTime.now());
        event6.setSalesEndDate(LocalDateTime.now());
        event6.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 9 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 9 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 9 + "/image-3.jpg"));
        event6.setBoothPrice(39);
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
        event7.setCategory(eventCategories[2]);
        event7.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event7.setPhysical(true);
        event7.setEventStartDate(LocalDateTime.now());
        event7.setEventEndDate(LocalDateTime.now());
        event7.setTicketPrice(24);
        event7.setTicketCapacity(100);
        event7.setSaleStartDate(LocalDateTime.now());
        event7.setSalesEndDate(LocalDateTime.now());
        event7.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 10 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 10 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 10 + "/image-3.jpg"));
        event7.setBoothPrice(39);
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
        event8.setCategory(eventCategories[3]);
        event8.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event8.setPhysical(true);
        event8.setEventStartDate(LocalDateTime.now());
        event8.setEventEndDate(LocalDateTime.now());
        event8.setTicketPrice(24);
        event8.setTicketCapacity(100);
        event8.setSaleStartDate(LocalDateTime.now());
        event8.setSalesEndDate(LocalDateTime.now());
        event8.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 11 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 11 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 11 + "/image-3.jpg"));
        event8.setBoothPrice(39);
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
        event9.setCategory(eventCategories[4]);
        event9.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        event9.setPhysical(true);
        event9.setEventStartDate(LocalDateTime.now());
        event9.setEventEndDate(LocalDateTime.now());
        event9.setTicketPrice(24);
        event9.setTicketCapacity(100);
        event9.setSaleStartDate(LocalDateTime.now());
        event9.setSalesEndDate(LocalDateTime.now());
        event9.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 12 + "/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 12 + "/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-" + 12 + "/image-3.jpg"));
        event9.setBoothPrice(39);
        event9.setBoothCapacity(305);
        event9.setRating(5);
        event9.setEventStatus(EventStatus.CREATED);
        event9.setHidden(false);
        event9.setPublished(true);

        // EventOrganiser eventOrg =
        // eventOrganiserRepository.findByEmail("organiser@abc.com");
        // lili
        EventOrganiser eventOrg = eventOrganiserRepository.findByEmail("linlili7842@gmail.com");
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

        Event previous = new Event();
        previous.setEventOrganiser(eventOrg);
        previous.setName("Previous event");
        previous.setAddress("some location string");
        previous.setDescriptions("lorem ipsum dolor sit amet");
        previous.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
        previous.setPhysical(true);
        previous.setCategory(eventCategories[7]);
        previous.setEventStartDate(LocalDateTime.now().minusMonths(1));
        previous.setEventEndDate(LocalDateTime.now().minusWeeks(3));
        previous.setSellingTicket(true);
        previous.setTicketPrice(24);
        previous.setTicketCapacity(100);
        previous.setSaleStartDate(LocalDateTime.now().minusMonths(1).minusWeeks(2));
        previous.setSalesEndDate(LocalDateTime.now().minusMonths(1).minusWeeks(1));
        previous.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/previous-event/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/previous-event/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/previous-event/image-3.jpg"));
        previous.setBoothPrice(39);
        previous.setBoothCapacity(305);
        previous.setRating(5);
        previous.setEventStatus(EventStatus.CREATED);
        previous.setHidden(false);
        previous.setPublished(true);
        eventRepository.save(previous);

        Attendee atnd = attendeeService.getAttendeeByEmail("attendee@abc.com");
        TicketTransaction ttransaction = new TicketTransaction();
        ttransaction = new TicketTransaction();
        ttransaction.setEvent(previous);
        ttransaction.setPaymentStatus(PaymentStatus.COMPLETED);
        ttransaction.setAttendee(atnd);
        ttransaction.setStripePaymentId("test_id");
        ticketTransactionRepository.save(ttransaction);
        ttransaction = new TicketTransaction();
        ttransaction.setEvent(previous);
        ttransaction.setPaymentStatus(PaymentStatus.COMPLETED);
        ttransaction.setAttendee(atnd);
        ttransaction.setStripePaymentId("test_id");
        ticketTransactionRepository.save(ttransaction);
        ttransaction = new TicketTransaction();
        ttransaction.setEvent(previous);
        ttransaction.setPaymentStatus(PaymentStatus.COMPLETED);
        ttransaction.setAttendee(atnd);
        ttransaction.setStripePaymentId("test_id");
        ticketTransactionRepository.save(ttransaction);

        ttransaction = new TicketTransaction();
        ttransaction.setEvent(event);
        ttransaction.setPaymentStatus(PaymentStatus.COMPLETED);
        ttransaction.setAttendee(atnd);
        ttransaction.setStripePaymentId("test_id");
        ticketTransactionRepository.save(ttransaction);

        ttransaction = new TicketTransaction();
        ttransaction.setEvent(event);
        ttransaction.setAttendee(atnd);
        ttransaction.setStripePaymentId("test_id");
        ticketTransactionRepository.save(ttransaction);

        List<Event> eoEvents = new ArrayList<>();
        // eoEvents = eventOrg.getEvents();
        eoEvents.add(event);
        eoEvents.add(event2);
        eventOrg.setEvents(eoEvents);
        event.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-1/image-1.jpg",
                "https://storage.googleapis.com/ems-images/events/event-2/image-2.jpg",
                "https://storage.googleapis.com/ems-images/events/event-3/image-3.jpg"));

        EventOrganiser eo = eoRepository.findByEmail("organiser@abc.com");
        event.setEventOrganiser(eo);
        eventRepository.save(event);

        // EventBoothTransaction transaction = new EventBoothTransaction();
        // transaction.setEvent(event);
        // eventBoothTransactionRepository.save(transaction);
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
            e.setDescriptions("description testtestest");
            e.setTicketPrice(Math.round(rand.nextFloat() * 20));
            e.setTicketCapacity(rand.nextInt(100));
            e.setCategory(eventCategories[6]);
            e.setBoothLayout("https://www.ncwvhba.org/wp-content/uploads/2021-Home-Show-Packet-4.jpg");
            e.setPhysical(true);
            LocalDateTime eventStart = LocalDateTime.of(2021, Month.MARCH, 1, 9, 0).plusDays(i).plusHours(i % 3);
            e.setEventStartDate(eventStart);
            e.setEventEndDate(
                    LocalDateTime.of(2021, Month.MARCH, 2, 17, 30).plusDays(rand.nextInt(5)).minusHours(i % 2));
            e.setSaleStartDate(LocalDateTime.now());
            e.setSalesEndDate(eventStart.minusDays(2));

            e.setImages(Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + i + "/image-1.jpg",
                    "https://storage.googleapis.com/ems-images/events/event-" + i + "/image-2.jpg",
                    "https://storage.googleapis.com/ems-images/events/event-" + i + "/image-3.jpg"));
            e.setBoothPrice(17);
            e.setBoothCapacity(rand.nextInt(50));
            e.setEventStatus(EventStatus.CREATED);
            e.setPublished(true);
            eventRepository.save(e);
        }
    }

    private void setEventCategories(Event event) {
        Random rand = new Random();
        int upperBound = eventCategories.length;
        int randCategoryIndex = rand.nextInt(upperBound);
        // List<String> categories = new ArrayList<>();
        // for (int i = 0; i < numberOfCategories; i++) {
        // int categoryIndex = rand.nextInt(upperBound);
        // String categoryString = eventCategories[categoryIndex];
        // if (!categories.contains(categoryString))
        // categories.add(categoryString);
        // }
        // event.setCategories(categories);
        // return event;

        event.setCategory(eventCategories[randCategoryIndex]);
    }

    @Transactional
    private void createProducts() {
        String[] productImageArray = { "https://i.imgur.com/F90zsSB.jpg", "https://i.imgur.com/Wez0pks.jpg",
                "https://i.imgur.com/xUOtoql.jpg", "https://i.imgur.com/jyNXtfV.jpg", "https://i.imgur.com/HryBpzZ.jpg",
                "https://i.imgur.com/5pt4rUF.jpg", "https://i.imgur.com/hkiA4LQ.jpg", "https://i.imgur.com/72QT6op.jpg",
                "https://i.imgur.com/50nw6rT.jpg", "https://i.imgur.com/OrBbkQE.jpg" };
        Lorem lorem = LoremIpsum.getInstance();
        List<BusinessPartner> businessPartners = businessPartnerRepository.findAll();
        for (BusinessPartner bp : businessPartners) {
            for (int i = 1; i < 11; i++) {
                Product p = new Product();
                p.setName("Product " + i);
                p.setDescription(lorem.getWords(5, 20));
                p.setImage(productImageArray[i - 1]);
                // p.setImage("https://storage.googleapis.com/ems-images/events/event-" + i +
                // "/image-1.jpg");
                p.setBusinessPartner(bp);
                productRepository.save(p);
            }
        }
    }

    // @Transactional
    // private void createBoothsAndProfiles(Event event, BusinessPartner bp) {
    // Random rand = new Random();
    // Lorem lorem = LoremIpsum.getInstance();

    // // CREATING BOOTH PROFILES
    // SellerProfile profile = new SellerProfile();
    // profile.setEvent(event);
    // // BusinessPartner bp = businessPartnerRepository.findById(id).get();
    // // List<Product> bpProducts = businessPartnerService.getPartnerProducts(id);
    // List<Product> bpProducts =
    // productRepository.findProductsByBusinessPartner(bp.getId());
    // profile.setBusinessPartner(bp);
    // profile.setDescription(lorem.getWords(5, 20));
    // profile.setBrochureImages(
    // Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 1 +
    // "/image-1.jpg",
    // "https://storage.googleapis.com/ems-images/events/event-" + 2 +
    // "/image-2.jpg",
    // "https://storage.googleapis.com/ems-images/events/event-" + 3 +
    // "/image-3.jpg"));
    // sellerProfileRepository.save(profile);

    // // CREATING 3 BOOTHS
    // for (int i = 1; i < 4; i++) {
    // Booth b = new Booth();

    // // setting random set of products
    // bp.getProducts().size();
    // // List<Product> bpProducts= bp.getProducts();
    // List<Product> sellerProfileProducts = new ArrayList<>();
    // int numberOfProducts = rand.nextInt(bpProducts.size());
    // for (int j = 0; j < numberOfProducts; j++) {
    // sellerProfileProducts.add(bpProducts.get(j));
    // }
    // ;
    // b.setProducts(sellerProfileProducts);
    // b.setBoothNumber(i);
    // b.setDescription(lorem.getWords(5, 20));
    // b.setSellerProfile(sellerProfileRepository.findById(1L).get());
    // boothRepository.save(b);
    // }
    // ;
    // }

    @Transactional
    private void createBoothsAndProfiles() {
        Random rand = new Random();
        Lorem lorem = LoremIpsum.getInstance();

        // CREATING BOOTH PROFILES
        SellerProfile profile = new SellerProfile();
        profile.setEvent(eventRepository.findAll().get(0));
        profile.setBusinessPartner(businessPartnerRepository.findByEmail("partner@abc.com"));
        profile.setDescription(lorem.getWords(5, 20));
        profile.setBrochureImages(
                Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 1 + "/image-1.jpg",
                        "https://storage.googleapis.com/ems-images/events/event-" + 2 + "/image-2.jpg",
                        "https://storage.googleapis.com/ems-images/events/event-" + 3 + "/image-3.jpg"));
        sellerProfileRepository.save(profile);

        // CREATING 3 BOOTHS
        for (int i = 1; i < 4; i++) {
            Booth b = new Booth();

            // setting random set of products
            List<Product> allProducts = productRepository.findAll();
            List<Product> sellerProfileProducts = new ArrayList<>();
            int numberOfProducts = rand.nextInt(allProducts.size());
            for (int j = 0; j < numberOfProducts; j++) {
                sellerProfileProducts.add(allProducts.get(j));
            }
            ;
            b.setProducts(sellerProfileProducts);
            b.setBoothNumber(rand.nextInt(70) + 1);
            b.setDescription(lorem.getWords(5, 20));
            b.setSellerProfile(sellerProfileRepository.findById(1L).get());
            boothRepository.save(b);
        }
        ;
    }

    @Transactional
    private void createSellerApplications() {
        // List<Event> allEvents = eventRepository.findAll();
        Lorem lorem = LoremIpsum.getInstance();
        Random rand = new Random();

        // 4 TYPES OF APPLICATION TYPES

        // THE STATUS ARRAYS ARE TO SHOW THE 4 DIFFERENT SCENARIOS OF PAYMENTSTATUS AND
        // APPLICATIONSTATUS
        // LATER WHEN I CREATE APPLICATIONS I'LL USE THE DIFFERENT COMBINATIONS
        // COMBI 0 : APPROVED PENDING PAYMENT
        // COMBI 0-1: APPROVED PENDING PAYMENT PLUS ALLOCATED
        // COMBI 1 : FINISHED PROCESS - SELLER PROFILE CREATED
        // COMBI 2 : REJECTED
        // COMBI 3 : NEW APPLICAION

        SellerApplicationStatus[] sellerApplicationStatusArray = { SellerApplicationStatus.APPROVED,
                SellerApplicationStatus.CONFIRMED, SellerApplicationStatus.REJECTED, SellerApplicationStatus.PENDING };

        PaymentStatus[] paymentStatusArray = { PaymentStatus.PENDING, PaymentStatus.COMPLETED, PaymentStatus.PENDING,
                PaymentStatus.PENDING };

        // CREATE MORE FOR EVENT 1
        Event firstEvent = eventRepository.findAll().get(0);
        List<BusinessPartner> businessPartners = businessPartnerRepository.findAll();
        for (BusinessPartner bp : businessPartners) {
            SellerApplication application = new SellerApplication();
            int count = rand.nextInt(4); // now just make the applications randomly
            application.setBusinessPartner(bp);
            application.setEvent(firstEvent);
            application.setDescription(lorem.getWords(5, 20));
            application.setComments(lorem.getWords(5, 20));
            application.setBoothQuantity(3);
            application.setSellerApplicationStatus(sellerApplicationStatusArray[count]);
            application.setPaymentStatus(paymentStatusArray[count]);
            application.setApplicationDate(firstEvent.getEventStartDate().minusDays(rand.nextInt(20)));
            SellerApplication savedApplication = sellerApplicationRepository.save(application);

            if (count == 0) {
                // WE NEEDA ACCOUNT FOR BOTH TYPES OF THIS SCENARIO, ONE IS WITH BOOTH ONE IS
                // WITHOUT
                int ifAllocateBooth = rand.nextInt(2);
                if (ifAllocateBooth == 1) {
                    // 1 FOR ALLOCATING BOOTHS TO THAT APPLICATION
                    List<Booth> eventBooths = firstEvent.getBooths();
                    List<Booth> allocatedBooths = new ArrayList<>();
                    int allocatedBoothCount = 0;
                    for (Booth b : eventBooths) {
                        if (allocatedBoothCount >= application.getBoothQuantity())
                            break;
                        if (b.getSellerApplication() == null && b.getSellerProfile() == null) {
                            // then allocate this booth to the application
                            // allocatedBooths.add(b);
                            b.setSellerApplication(savedApplication);
                            Booth updatedBooth = boothRepository.save(b);
                            // allocatedBooths.add(updatedBooth);
                            allocatedBoothCount++;
                        }
                    }
                    // application.setBooths(allocatedBooths);
                }
                // else no need do anything, just dont allocate any booths to the guy
            }
             else if (count == 1) {
                // NEW VERSION WITH BOOTHS ALREADY CREATED PER EVENT
                SellerProfile profile = new SellerProfile();
                profile.setEvent(firstEvent);
                profile.setBusinessPartner(bp);
                profile.setDescription(lorem.getWords(5, 20));
                profile.setBrochureImages(
                        Arrays.asList("https://storage.googleapis.com/ems-images/events/event-" + 1 + "/image-1.jpg",
                                "https://storage.googleapis.com/ems-images/events/event-" + 2 + "/image-2.jpg",
                                "https://storage.googleapis.com/ems-images/events/event-" + 3 + "/image-3.jpg"));
                SellerProfile savedProfile = sellerProfileRepository.save(profile);
                List<Booth> eventBooths = firstEvent.getBooths();
                List<Booth> allocatedBooths = new ArrayList<>();
                int allocatedBoothCount = 0;
                for (Booth b : eventBooths) {
                    if (allocatedBoothCount >= application.getBoothQuantity())
                        break;
                    if (b.getSellerApplication() == null && b.getSellerProfile() == null) {
                        // then allocate this booth to the application
                        b.setSellerApplication(savedApplication);
                        b.setSellerProfile(savedProfile);
                        Booth updatedBooth = boothRepository.save(b);
                        // allocatedBooths.add(updatedBooth);
                        allocatedBoothCount++;
                    }
                }
                // profile.setBooths(allocatedBooths);
                // application.setBooths(allocatedBooths);
                // SellerProfile savedProfile = sellerProfileRepository.save(profile);
            }
        }
        // CREATE RANDOM NUMBERS FOR THE REST
        List<Event> allEvents = eventRepository.findAll();
        allEvents.remove(allEvents.get(0));
        for (Event e : allEvents) {
            for (int i = 0; i < 5; i++) {
                // MAKE 2 APPLICATIONS FOR EACH EVENT
                BusinessPartner randomBp = businessPartnerRepository.findAll()
                        .get(rand.nextInt(businessPartners.size()));
                SellerApplication application = new SellerApplication();
                application.setBusinessPartner(randomBp);
                application.setEvent(e);
                application.setDescription(lorem.getWords(5, 20));
                application.setComments(lorem.getWords(5, 20));
                application.setBoothQuantity(3);
                int statusTypeIndex = rand.nextInt(3);
                application.setSellerApplicationStatus(sellerApplicationStatusArray[statusTypeIndex]);
                application.setPaymentStatus(paymentStatusArray[statusTypeIndex]);
                application.setApplicationDate(e.getEventStartDate().minusDays(rand.nextInt(20)));
                SellerApplication savedApplication = sellerApplicationRepository.save(application);

                // NEW
                if (statusTypeIndex == 0) {
                    // WE NEEDA ACCOUNT FOR BOTH TYPES OF THIS SCENARIO, ONE IS WITH BOOTH ONE IS WITHOUT
                    int ifAllocateBooth = rand.nextInt(2);
                    if (ifAllocateBooth == 1) {
                        // 1 FOR ALLOCATING BOOTHS TO THAT APPLICATION
                        List<Booth> eventBooths = e.getBooths();
                        List<Booth> allocatedBooths = new ArrayList<>();
                        int allocatedBoothCount = 0;
                        for (Booth b : eventBooths) {
                            if (allocatedBoothCount >= application.getBoothQuantity())
                                break;
                            if (b.getSellerApplication() == null && b.getSellerProfile() == null) {
                                // then allocate this booth to the application
                                // allocatedBooths.add(b);
                                b.setSellerApplication(savedApplication);
                                Booth updatedBooth = boothRepository.save(b);
                                // allocatedBooths.add(updatedBooth);
                                allocatedBoothCount++;
                            }
                        }
                        application.setBooths(allocatedBooths);
                    }
                    // else no need do anything, just dont allocate any booths to the guy
                }

                // NEW
                else if (statusTypeIndex == 1) {
                    // NEW VERSION WITH BOOTHS ALREADY CREATED PER EVENT
                    SellerProfile profile = new SellerProfile();
                    profile.setEvent(e);
                    profile.setBusinessPartner(randomBp);
                    profile.setDescription(lorem.getWords(5, 20));
                    profile.setBrochureImages(Arrays.asList(
                            "https://storage.googleapis.com/ems-images/events/event-" + 1 + "/image-1.jpg",
                            "https://storage.googleapis.com/ems-images/events/event-" + 2 + "/image-2.jpg",
                            "https://storage.googleapis.com/ems-images/events/event-" + 3 + "/image-3.jpg"));
                    SellerProfile savedProfile = sellerProfileRepository.save(profile);
                    List<Booth> eventBooths = e.getBooths();
                    List<Booth> allocatedBooths = new ArrayList<>();
                    int allocatedBoothCount = 0;
                    for (Booth b : eventBooths) {
                        if (allocatedBoothCount >= application.getBoothQuantity())
                            break;
                        if (b.getSellerApplication() == null && b.getSellerProfile() == null) {
                            // then allocate this booth to the application
                            b.setSellerApplication(savedApplication);
                            b.setSellerProfile(savedProfile);
                            Booth updatedBooth = boothRepository.save(b);
                            // allocatedBooths.add(updatedBooth);
                            allocatedBoothCount++;
                        }
                    }
                    // profile.setBooths(allocatedBooths);
                    // application.setBooths(allocatedBooths);
                }
            }

            // CREATE RANDOM NUMBERS FOR THE REST
            // List<Event> allEvents = eventRepository.findAll();
            // allEvents.remove(allEvents.get(0));
            // for (Event e : allEvents) {
            // for (int i = 0; i < 2; i++) {
            // // MAKE 2 APPLICATIONS FOR EACH EVENT
            // BusinessPartner randomBp = businessPartnerRepository.findAll()
            // .get(rand.nextInt(businessPartners.size()));
            // SellerApplication application = new SellerApplication();
            // application.setBusinessPartner(randomBp);
            // application.setEvent(e);
            // application.setDescription(lorem.getWords(5, 20));
            // application.setComments(lorem.getWords(5, 20));
            // application.setBoothQuantity(rand.nextInt(300));
            // int statusTypeIndex = rand.nextInt(3);
            // application.setSellerApplicationStatus(sellerApplicationStatusArray[statusTypeIndex]);
            // application.setPaymentStatus(paymentStatusArray[statusTypeIndex]);
            // LocalDateTime applicaionDate = LocalDateTime.of(2021, Month.MARCH, 1, 9,
            // 0).plusDays(i)
            // .plusHours(i % 3);
            // application.setApplicationDate(applicaionDate);
            // LocalDateTime paymentDate = LocalDateTime.of(2021, Month.APRIL, 2, 9,
            // 0).plusDays(bpCount)
            // .plusHours(bpCount % 3);
            // application.setPaymentDate(paymentDate);

            // if (statusTypeIndex == 1) {
            // // SAME AS JUST NOW, NUMBER 1 IS THE CASE WHERE APPLICATION CONFIRM LIAO WITH
            // PAYMENT
            // // IN THAT CASE WE BUILD THE SELLER PROFILE FOR THE BP AND EVENT
            // SellerProfile profile = new SellerProfile();
            // profile.setEvent(e);
            // profile.setBusinessPartner(randomBp);
            // profile.setDescription(lorem.getWords(5, 20));
            // profile.setBrochureImages(Arrays.asList(
            // "https://storage.googleapis.com/ems-images/events/event-" + 1 +
            // "/image-1.jpg",
            // "https://storage.googleapis.com/ems-images/events/event-" + 2 +
            // "/image-2.jpg",
            // "https://storage.googleapis.com/ems-images/events/event-" + 3 +
            // "/image-3.jpg"));
            // SellerProfile savedProfile = sellerProfileRepository.save(profile);

            // // BOOTH SETUP FOR EACH PROFILE
            // for (int k = 1; k < 4; k++) {
            // Booth b = new Booth();

            // // setting random set of products
            // List<Product> allProducts = productRepository
            // .findProductsByBusinessPartner(randomBp.getId());
            // // List<Product> allProducts = randomBp.getProducts();
            // List<Product> sellerProfileProducts = new ArrayList<>();
            // int numberOfProducts = rand.nextInt(allProducts.size());
            // for (int j = 0; j < numberOfProducts; j++) {
            // sellerProfileProducts.add(allProducts.get(j));
            // }
            // ;
            // b.setProducts(sellerProfileProducts);
            // b.setBoothNumber(k);
            // b.setDescription(lorem.getWords(5, 20));
            // b.setSellerProfile(savedProfile);
            // boothRepository.save(b);
            // }
            // ;

            // SECOND COPY OF SAME CODE
            // List<Event> allEvents = eventRepository.findAll();
            // allEvents.remove(allEvents.get(0));
            // for (Event e : allEvents) {
            // for (int i = 0; i < 2; i++) {
            // // MAKE 2 APPLICATIONS FOR EACH EVENT
            // BusinessPartner randomBp = businessPartnerRepository.findAll()
            // .get(rand.nextInt(businessPartners.size()));
            // SellerApplication application = new SellerApplication();
            // application.setBusinessPartner(randomBp);
            // application.setEvent(e);
            // application.setDescription(lorem.getWords(5, 20));
            // application.setComments(lorem.getWords(5, 20));
            // application.setBoothQuantity(rand.nextInt(300));
            // int statusTypeIndex = rand.nextInt(3);
            // application.setSellerApplicationStatus(sellerApplicationStatusArray[statusTypeIndex]);
            // application.setPaymentStatus(paymentStatusArray[statusTypeIndex]);
            // application.setApplicationDate(e.getEventStartDate().minusDays(rand.nextInt(20)));
            // if (statusTypeIndex == 1) {
            // // SAME AS JUST NOW, NUMBER 1 IS THE CASE WHERE APPLICATION CONFIRM LIAO WITH
            // // PAYMENT
            // // IN THAT CASE WE BUILD THE SELLER PROFILE FOR THE BP AND EVENT
            // SellerProfile profile = new SellerProfile();
            // profile.setEvent(e);
            // profile.setBusinessPartner(randomBp);
            // profile.setDescription(lorem.getWords(5, 20));
            // profile.setBrochureImages(Arrays.asList(
            // "https://storage.googleapis.com/ems-images/events/event-" + 1 +
            // "/image-1.jpg",
            // "https://storage.googleapis.com/ems-images/events/event-" + 2 +
            // "/image-2.jpg",
            // "https://storage.googleapis.com/ems-images/events/event-" + 3 +
            // "/image-3.jpg"));
            // SellerProfile savedProfile = sellerProfileRepository.save(profile);

            // // BOOTH SETUP FOR EACH PROFILE
            // for (int k = 1; k < 4; k++) {
            // Booth b = new Booth();

            // // setting random set of products
            // List<Product> allProducts = productRepository
            // .findProductsByBusinessPartner(randomBp.getId());
            // // List<Product> allProducts = randomBp.getProducts();
            // List<Product> sellerProfileProducts = new ArrayList<>();
            // int numberOfProducts = rand.nextInt(allProducts.size());
            // for (int j = 0; j < numberOfProducts; j++) {
            // sellerProfileProducts.add(allProducts.get(j));
            // }
            // ;
            // b.setProducts(sellerProfileProducts);
            // b.setBoothNumber(k);
            // b.setDescription(lorem.getWords(5, 20));
            // b.setSellerProfile(savedProfile);
            // boothRepository.save(b);
            // }
            // ;

            // }
            // sellerApplicationRepository.save(application);
            // }
            // }
        }
    }

    @Transactional
    private void createBooths() {
        Lorem lorem = LoremIpsum.getInstance();
        List<Event> allEvents = eventRepository.findAll();
        for (Event e : allEvents) {
            e.getBooths().size();
            int numberOfBoothsToCreate = e.getBoothCapacity() - e.getBooths().size();
            for (int i = 1; i <= numberOfBoothsToCreate; i++) {
                Booth b = new Booth();
                b.setBoothNumber(i);
                b.setDescription(lorem.getWords(5, 20));
                b.setEvent(e);
                boothRepository.save(b);
            }
        }
    }

    @Transactional
    private void setProducts() {
        List<Booth> allBooths= boothRepository.findAll();
        for (Booth b : allBooths) {
            if (b.getSellerProfile() != null) {
                BusinessPartner bp = b.getSellerProfile().getBusinessPartner();
                int numberOfBpProducts = bp.getProducts().size();
                List<Product> bpProducts = bp.getProducts();
                List<Product> productsToAllocate = new ArrayList<>();
                for (int i=0;i<3;i++) { //set 3 products for each booth
                    Random rand = new Random();
                    int productNumber = rand.nextInt(numberOfBpProducts);
                    productsToAllocate.add(bpProducts.get(productNumber));
                }
                b.setProducts(productsToAllocate);
                boothRepository.save(b);
            }
        }
    }
}
