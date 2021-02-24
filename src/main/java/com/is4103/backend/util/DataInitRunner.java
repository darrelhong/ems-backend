package com.is4103.backend.util;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

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
    private BusinessPartnerRepository businessPartnerRepository;

    @Autowired
    private EventBoothTransactionRepository eventBoothTransactionRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    private EventOrganiser eoTest;
    private BusinessPartner bpTest;
    private Event eventTest;

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
        if (eventRepository.findAll().isEmpty()){
            createEvent();
           
        }

        if (eventBoothTransactionRepository.findAll().isEmpty()){
           
           createEventTransaction();
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
    private void createBizPartner() {
        BusinessPartner bp = new BusinessPartner();
        bp.setEmail("partner@abc.com");
        bp.setName("First Business Partner");
        bp.setPassword(passwordEncoder.encode("password"));
        bp.setEnabled(true);
        bp.setRoles(Set.of(roleRepository.findByRoleEnum(RoleEnum.BIZPTNR)));
        bp.setBusinessCategory("Travel");
        userRepository.save(bp);

        this.bpTest = bp;
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
        event.setEventOrganiser(eoTest);
        eventRepository.save(event);

        this.eventTest = event;
    }

    @Transactional
    private void createEventTransaction() {
       
        EventBoothTransaction eventBooth = new EventBoothTransaction();
        eventBooth.setBoothApplicationstatus(BoothApplicationStatus.APPROVED);
        eventBooth.setPaymentStatus(PaymentStatus.COMPLETED);
        eventBooth.setBusinessPartner(this.bpTest);
        eventBooth.setEvent(this.eventTest);   
        eventBoothTransactionRepository.save(eventBooth);  

        
        List<EventBoothTransaction> transactions = new ArrayList<>();

        
        transactions.add(eventBooth);
        this.bpTest.setEventBoothTransactions(transactions);
        // System.out.println("test " + transactions);
        businessPartnerRepository.save(this.bpTest);
        this.eventTest.setEventBoothTransactions(transactions); 
        eventRepository.save(this.eventTest);
   
         System.out.println("bptest " + this.bpTest.getEventBoothTransactions().size());
         System.out.println("eventtest " + this.eventTest.getEventBoothTransactions().size());
        
       
    }
}
