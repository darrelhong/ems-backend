package com.is4103.backend.util;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;

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
import com.is4103.backend.repository.BusinessPartnerRepository;
import com.is4103.backend.repository.EventBoothTransactionRepository;
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
        userRepository.save(bp);

        //set follow eo list for bp
        List<EventOrganiser> following = new ArrayList<>();
        following.add(this.eoTest);
        bp.setFollowEventOrganisers(following);

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

        //create second attendee
        Attendee atnTwo = new Attendee();
        atnTwo.setEmail("attendeeTwo@abc.com");
        atnTwo.setName("Second attendee");
        atnTwo.setPassword(passwordEncoder.encode("password"));
        atnTwo.setDescription("description for Second attendeeeeeeee :)");
        atnTwo.setEnabled(true);
        atnTwo.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.ATND)));

        //set following bp list for attendees
        List<BusinessPartner> followBp = new ArrayList<>();
        followBp.add(bp);
        atn.setFollowedBusinessPartners(followBp);
        // atnTwo.setFollowedBusinessPartners(followBp);

        //save atn
        userRepository.save(atn);
        userRepository.save(atnTwo);
        
        // atnTwo.setFollowedBusinessPartners(followBp);

        // userRepository.save(atnTwo);

        //set bp followers list 
        List<Attendee> followers = new ArrayList<>();
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
        eventRepository.save(event);
    }


}
