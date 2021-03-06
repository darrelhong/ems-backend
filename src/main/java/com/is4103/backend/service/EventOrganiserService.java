package com.is4103.backend.service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.temporal.ChronoUnit;

import java.util.stream.Collectors;


import javax.mail.internet.InternetAddress;
import javax.transaction.Transactional;

import com.is4103.backend.dto.BroadcastMessageRequest;
import com.is4103.backend.dto.BroadcastMessageToFollowersRequest;
import com.is4103.backend.dto.OrganiserSearchCriteria;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.UpdateUserRequest;
import com.is4103.backend.dto.UploadBizSupportFileRequest;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
// import com.is4103.backend.model.EventBoothTransaction;
import com.is4103.backend.model.SellerApplication;
import com.is4103.backend.model.EventOrganiser;

import com.is4103.backend.model.Review;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.model.TicketTransaction;
import com.is4103.backend.model.User;
import com.is4103.backend.repository.EventOrganiserRepository;
import com.is4103.backend.repository.EventRepository;
import com.is4103.backend.repository.OrganiserSpecification;
import com.is4103.backend.repository.UserRepository;
import com.is4103.backend.util.errors.UserAlreadyExistsException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.registration.OnRegistrationCompleteEvent;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.qos.logback.core.util.Duration;

@Service
public class EventOrganiserService {

    @Value("${backend.from.email}")
    private String fromEmail;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EventOrganiserRepository eoRepository;

    @Autowired
    private BusinessPartnerService bpService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SellerApplicationService sellerAppService;

    @Autowired
    private TicketingService ttService;

    @Autowired
    private ReviewService reviewService;

    @Value("${stripe.apikey}")
    private String stripeApiKey;

    @Value("${stripe.secretkey}")
    private String stripeSecretKey;

    
    public List<EventOrganiser> getAllEventOrganisers() {
        return eoRepository.findAll();
    }

    public Page<EventOrganiser> getEventOrganisersPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return eoRepository.findAll(pageRequest);
    }

    public EventOrganiser getEventOrganiserById(Long eoId) {
        return eoRepository.findById(eoId).orElseThrow(() -> new UserNotFoundException());
    }
    
    public EventOrganiser getEventOrganiserByEmail(String email) {
        System.out.println("getEventOrganiserByEmail");
         System.out.println(eoRepository.findByEmail(email).getEmail());
        return eoRepository.findByEmail(email);
    }

    @Transactional
    public EventOrganiser registerNewEventOrganiser(SignupRequest signupRequest, boolean enabled) {

        EventOrganiser newEo = new EventOrganiser();
        newEo.setName(signupRequest.getName());
        newEo.setEmail(signupRequest.getEmail());
        newEo.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        Role role = roleService.findByRoleEnum(RoleEnum.EVNTORG);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        newEo.setRoles(roles);
        //newEo.setSupportDocsUrl(bizsupportdocdownloadurl);

        if (enabled) {
            newEo.setEnabled(true);
        }

        newEo = eoRepository.save(newEo);

        if (!newEo.isEnabled()) {
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(newEo));
        }

        return newEo;
    }

    public EventOrganiser approveEventOrganiser(Long eoId) {
        EventOrganiser toApprove = getEventOrganiserById(eoId);
        toApprove.setApproved(true);
        toApprove.setApprovalMessage(null);
        SimpleMailMessage email = new SimpleMailMessage();
        User user = userService.getUserById(eoId);    
        email.setFrom(fromEmail);
        email.setTo(user.getEmail());
        email.setSubject("Application for Business Document");
        email.setText("You have received the following message from EventStop:" + "\r\n\r\n" + "\"" + "Your application has been approved. You may start to create new events!"
                + "\"" + " " + "\r\n\r\n" + "<b>"
                + "This is an automated email from EventStop. Do not reply to this email.</b>" + "\r\n" + "</b>");
                
        // cc the person who submitted the enquiry.
        System.out.println("You have received the following message from EventStop:" + "\r\n\r\n" + "\"" + "Your application has been approved. You may start to create new events!"
        + "\"" + " " + "\r\n\r\n" + "<b>"
        + "This is an automated email from EventStop. Do not reply to this email.</b>" + "\r\n" + "</b>");
        javaMailSender.send(email);
        return eoRepository.save(toApprove);
    }

    public EventOrganiser rejectEventOrganiser(Long eoId, String message) {
        EventOrganiser toReject = getEventOrganiserById(eoId);
        toReject.setApproved(false);
        toReject.setApprovalMessage(message);
        SimpleMailMessage email = new SimpleMailMessage();
        User user = userService.getUserById(eoId);    
        email.setFrom(fromEmail);
        email.setTo(user.getEmail());
        email.setSubject("Application for Business Document");
        email.setText("You have received the following message from EventStop:" + "\r\n\r\n" + "\"" + "Your application has been rejected due to the foolowing reason : " + message + "You may re-upload your business document. Thank you for your understanding. "
                + "\"" + " " + "\r\n\r\n" + "<b>"
                + "This is an automated email from EventStop. Do not reply to this email.</b>" + "\r\n" + "</b>");
                
        // cc the person who submitted the enquiry.
        javaMailSender.send(email);
        System.out.println("You have received the following message from EventStop:" + "\r\n\r\n" + "\"" + "Your application has been rejected due to the foolowing reason : " + message + "You may re-upload your business document. Thank you for your understanding. "
        + "\"" + " " + "\r\n\r\n" + "<b>"
        + "This is an automated email from EventStop. Do not reply to this email.</b>" + "\r\n" + "</b>");
        return eoRepository.save(toReject);
    }

    public List<BusinessPartner> addToVipList(Long eoId, Long bpId) {
        EventOrganiser eo = getEventOrganiserById(eoId);
        BusinessPartner bp = bpService.getBusinessPartnerById(bpId);

        List<BusinessPartner> current = eo.getVipList();
        current.add(bp);
        eo.setVipList(current);
        eoRepository.save(eo);
        return eo.getVipList();
    }

    public List<BusinessPartner> removeFromVipList(Long eoId, Long bpId) {
        EventOrganiser eo = getEventOrganiserById(eoId);
        BusinessPartner bp = bpService.getBusinessPartnerById(bpId);

        List<BusinessPartner> current = eo.getVipList();
        current.remove(bp);
        eo.setVipList(current);
        eoRepository.save(eo);
        return eo.getVipList();
    }

    public boolean isBpInVipList(Long eoId, Long bpId) {
        EventOrganiser eo = getEventOrganiserById(eoId);
        BusinessPartner bp = bpService.getBusinessPartnerById(bpId);
        List<BusinessPartner> current = eo.getVipList();
        boolean isBpPresent = current.contains(bp);
        return isBpPresent;
    }

    public List<BusinessPartner> getAllVips(Long eoId) {
        EventOrganiser eo = getEventOrganiserById(eoId);
        return eo.getVipList();
    }

    public List<Attendee> getAttendeeFollowersById(Long id) {
        EventOrganiser organiser = getEventOrganiserById(id);
        List<Attendee> followers = new ArrayList<>();
        followers = organiser.getAttendeeFollowers();
        return followers;
    }

    public List<BusinessPartner> getPartnerFollowersById(Long id) {
        EventOrganiser organiser = getEventOrganiserById(id);
        List<BusinessPartner> followers = new ArrayList<>();
        followers = organiser.getBusinessPartnerFollowers();
        return followers;
    }

    public List<Event> getAllEventsByEoId(Long eoId) {
        EventOrganiser eo = getEventOrganiserById(eoId);
        List<Event> eventlist = eventService.getAllEvents();

        List<Event> eoeventlist = new ArrayList<>();
        for (int i = 0; i < eventlist.size(); i++) {
            if (eventlist.get(i).getEventOrganiser().getId() == eoId) {
                eoeventlist.add(eventlist.get(i));
            }
        }
        eo.setEvents(eoeventlist);

        return eo.getEvents();

    }

    public List<Event> getValidBpEventsByEventOrgId(Long eoId) {

        List<Event> eventlist = eventService.getAllEventsByOrganiser(eoId);
        List<Event> validEventListForBp = new ArrayList<>();
        for (Event event : eventlist) {
            if (event.getEventStatus().toString().equals("CREATED") && !event.isHidden()) {
                validEventListForBp.add(event);
            }
        }
        return validEventListForBp;
    }

    public List<Event> getValidAttEventsByEventOrgId(Long eoId) {

        List<Event> eventlist = eventService.getAllEventsByOrganiser(eoId);
        List<Event> validEventListForAtt = new ArrayList<>();
        for (Event event : eventlist) {
            if (event.getEventStatus().toString().equals("CREATED") && !event.isHidden() && event.isPublished()) {
                validEventListForAtt.add(event);
            }
        }
        return validEventListForAtt;
    }


    public List<Event> getValidBpEventsByEventOrgIdForDashboard(Long eoId) {

        List<Event> eventlist = eventService.getAllEventsByOrganiser(eoId);
        List<Event> validEventListForBp = new ArrayList<>();
        for (Event event : eventlist) {
            if (event.getEventStatus().toString().equals("CREATED")) {
                validEventListForBp.add(event);
            }
        }
        return validEventListForBp;
    }
    public List<Event> getAllEventsByEoIdRoleStatus(Long eoId, String role, String status) {
        // List<Event> eventlist = eventService.getAllEvents();
        List<Event> eventlist = eventService.getAllEventsByOrganiser(eoId);
        List<Event> filterEventList = new ArrayList<>();

        if (role.equals("guest") || role.equals("ATND") || role.equals("EVNTORG")) {
            if (status.equals("upcoming")) {
                filterEventList = new ArrayList<>();
                for (int a = 0; a < eventlist.size(); a++) {
                    Event eventItem = eventlist.get(a);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                    LocalDateTime now = LocalDateTime.now();

                    if (eventItem.getEventStatus().toString().equals("CREATED") && eventItem.isPublished() == true
                            && (eventItem.getEventStartDate().isAfter(now)
                                    || eventItem.getEventStartDate().isEqual(now))
                            && (eventItem.getSaleStartDate().isAfter(now)
                                    || eventItem.getSaleStartDate().isEqual(now))) {
                        filterEventList.add(eventItem);
                    }
                }

            } else if (status.equals("current")) {
                filterEventList = new ArrayList<>();
                for (int a = 0; a < eventlist.size(); a++) {
                    Event eventItem = eventlist.get(a);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                    LocalDateTime now = LocalDateTime.now();

                    if (eventItem.getEventStatus().toString().equals("CREATED") && eventItem.isPublished() == true
                            && (eventItem.getEventStartDate().isAfter(now)
                                    || eventItem.getEventStartDate().isEqual(now))
                            && (eventItem.getSaleStartDate().isBefore(now) || eventItem.getSaleStartDate().isEqual(now))
                            && (eventItem.getSalesEndDate().isAfter(now) || eventItem.getSalesEndDate().isEqual(now))) {

                        filterEventList.add(eventItem);
                    }
                }
            } else if (status.equals("past")) {
                filterEventList = new ArrayList<>();
                for (int a = 0; a < eventlist.size(); a++) {
                    Event eventItem = eventlist.get(a);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                    LocalDateTime now = LocalDateTime.now();

                    if (eventItem.getEventStatus().toString().equals("CREATED")
                            && (eventItem.getEventEndDate().isBefore(now)
                                    || eventItem.getEventEndDate().isEqual(now))) {

                        filterEventList.add(eventItem);
                    }
                }
            }

        } else if (role.equals("BIZPTNR")) {
            if (status.equals("current")) {
                filterEventList = new ArrayList<>();
                for (int a = 0; a < eventlist.size(); a++) {
                    Event eventItem = eventlist.get(a);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                    LocalDateTime now = LocalDateTime.now();

                    if (eventItem.getEventStatus().toString().equals("CREATED") && !eventItem.isHidden()
                            && (eventItem.getEventStartDate().isAfter(now)
                                    || eventItem.getEventStartDate().isEqual(now))
                            && (eventItem.getSaleStartDate().isBefore(now) || eventItem.getSaleStartDate().isEqual(now))
                            && (eventItem.getSalesEndDate().isAfter(now) || eventItem.getSalesEndDate().isEqual(now)))

                        filterEventList.add(eventItem);
                }
            } else if (status.equals("past")) {
                System.out.println("partner past");
                filterEventList = new ArrayList<>();
                for (int a = 0; a < eventlist.size(); a++) {
                    Event eventItem = eventlist.get(a);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    if (eventItem.getEventStatus().toString().equals("CREATED")
                            && (eventItem.getEventEndDate().isBefore(now)
                                    || eventItem.getEventEndDate().isEqual(now))) {

                        filterEventList.add(eventItem);
                    }
                }

            } 
            
            // else if (status.equals("upcoming")) {
            //     filterEventList = new ArrayList<>();
            //     for (int a = 0; a < eventlist.size(); a++) {
            //         Event eventItem = eventlist.get(a);
            //         DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            //         LocalDateTime now = LocalDateTime.now();

            //         if (eventItem.getEventStatus().toString().equals("CREATED") && !eventItem.isHidden()
            //                 && (eventItem.getEventStartDate().isAfter(now)
            //                         || eventItem.getEventStartDate().isEqual(now))
            //                 && (eventItem.getSaleStartDate().isAfter(now)
            //                         || eventItem.getSaleStartDate().isEqual(now))) {
            //             filterEventList.add(eventItem);
            //         }
            //     }

            // }

        }
        return filterEventList;
    }

    @Transactional
    public EventOrganiser updateEoProfile(
            EventOrganiser user, UpdateUserRequest updateUserRequest, String profilepicurl) {

        user.setName(updateUserRequest.getName());
        user.setDescription(updateUserRequest.getDescription());
        user.setAddress(updateUserRequest.getAddress());
        user.setPhonenumber(updateUserRequest.getPhonenumber());
        if (profilepicurl != null) {
            user.setProfilePic(profilepicurl);
        }

        return userRepository.save(user);
    }

    @Transactional
    public EventOrganiser updateEoBizSupportUrl(EventOrganiser eo, String supportDocsUrl) {

        eo.setSupportDocsUrl(supportDocsUrl);
        return userRepository.save(eo);
    }

    public Page<EventOrganiser> search(OrganiserSearchCriteria organiserSearchCriteria) {
        return eoRepository.findAll(new OrganiserSpecification(organiserSearchCriteria),
                organiserSearchCriteria.toPageRequest());
    }

    public Page<EventOrganiser> getOrganisers(int page, int size) {
        // return eventRepository.findByEventStatus(EventStatus.PUBLISHED,
        // PageRequest.of(page, size));
        return eoRepository.findAll(PageRequest.of(page, size));
    }

    public Page<EventOrganiser> getAllOrganisers(int page, int size, String sortBy, String sortDir, String keyword) {
        Sort sort = null;
        if (sortBy != null && sortDir != null) {
            if (sortDir.equals("desc")) {
                sort = Sort.by(sortBy).descending();
            } else {
                sort = Sort.by(sortBy).ascending();
            }
        }
        if (keyword != null) {
            if (sort == null) {
                return eoRepository.findByNameContaining(keyword, PageRequest.of(page, size));
            } else {
                return eoRepository.findByNameContaining(keyword, PageRequest.of(page, size, sort));
            }

        }
        if (sort == null) {
            return eoRepository.findAll(PageRequest.of(page, size));
        } else {
            return eoRepository.findAll(PageRequest.of(page, size, sort));
        }

    }

    // public List<BusinessPartner> getEventBps(List<EventBoothTransaction>
    // eventBoothTransactionList){
    // List<BusinessPartner> eventBpList = new ArrayList<>();
    // for(int i = 0;i < eventBoothTransactionList.size();i++){
    // EventBoothTransaction transItem = eventBoothTransactionList.get(i);
    // if(!(transItem.getPaymentStatus().toString().equals("REFUNDED"))){
    // eventBpList.add(transItem.getBusinessPartner());
    // }
    // }
    // return eventBpList;
    // }

    public List<BusinessPartner> getEventBps(List<SellerApplication> eventBoothTransactionList) {
        List<BusinessPartner> eventBpList = new ArrayList<>();
        for (int i = 0; i < eventBoothTransactionList.size(); i++) {
            SellerApplication transItem = eventBoothTransactionList.get(i);
            if (!(transItem.getPaymentStatus().toString().equals("REFUNDED"))) {
                eventBpList.add(transItem.getBusinessPartner());
            }
        }
        return eventBpList;
    }

    public List<Attendee> getEventAtts(List<TicketTransaction> eventTicketTransactionList) {
        List<Attendee> eventAttList = new ArrayList<>();
        for (int i = 0; i < eventTicketTransactionList.size(); i++) {
            TicketTransaction transItem = eventTicketTransactionList.get(i);
            if (!(transItem.getPaymentStatus().toString().equals("REFUNDED"))) {
                eventAttList.add(transItem.getAttendee());
            }
        }
        return eventAttList;
    }

    @Transactional
    public void broadcastMessage(User eo, BroadcastMessageRequest broadcastMessageRequest) {

        String subject = broadcastMessageRequest.getSubject();
        String broadcastOption = broadcastMessageRequest.getBroadcastOption();
        List<String> emailList = new ArrayList<>();
        Event event = eventService.getEventById(broadcastMessageRequest.getEventId());
        List<SellerApplication> eventBoothTransactionList = new ArrayList<>();
        // List<EventBoothTransaction> eventBoothTransactionList = new ArrayList<>();
        eventBoothTransactionList = event.getSellerApplications();
        List<TicketTransaction> eventTicketTransactionList = new ArrayList<>();
        eventTicketTransactionList = event.getTicketTransactions();
        if (broadcastOption.equals("Allbp")) {

            List<BusinessPartner> eventBpList = new ArrayList<>();
            eventBpList = this.getEventBps(eventBoothTransactionList);

            for (BusinessPartner bp : eventBpList) {
                emailList.add(bp.getEmail());
            }

        } else if (broadcastOption.equals("Allatt")) {

            List<Attendee> eventAttList = new ArrayList<>();
            eventAttList = this.getEventAtts(eventTicketTransactionList);

            for (Attendee att : eventAttList) {
                emailList.add(att.getEmail());
            }

        } else if (broadcastOption.equals("Both")) {
            List<BusinessPartner> eventBpList = new ArrayList<>();
            eventBpList = this.getEventBps(eventBoothTransactionList);
            List<Attendee> eventAttList = new ArrayList<>();
            eventAttList = this.getEventAtts(eventTicketTransactionList);

            for (BusinessPartner bp : eventBpList) {
                emailList.add(bp.getEmail());
            }

            for (Attendee att : eventAttList) {
                emailList.add(att.getEmail());
            }

        }

        String message = broadcastMessageRequest.getContent();

        SimpleMailMessage email = new SimpleMailMessage();

        String[] mailArray = emailList.toArray(new String[0]);
        System.out.println("mailArray");
        for (int i = 0; i < mailArray.length; i++) {
            System.out.println(mailArray[i]);
        }
        email.setFrom(fromEmail);
        email.setTo(mailArray);
        email.setSubject(subject);
        email.setText("You have received the following message from " + eo.getName() + ":" + "\r\n\r\n" + "\"" + message
                + "\"" + " " + "\r\n\r\n" + "<b>"
                + "This is an automated email from EventStop. Do not reply to this email.</b>" + "\r\n" + "<b>"
                + "Please direct your reply to " + eo.getName() + " at " + eo.getEmail() + "</b>");
        // cc the person who submitted the enquiry.
        email.setCc(eo.getEmail());
        javaMailSender.send(email);
    }

    @Transactional
    public void broadcastToFollowers(User eo, BroadcastMessageToFollowersRequest broadcastMessageToFollowersRequest) {

        //String subject = broadcastMessageToFollowersRequest.getSubject();
        String broadcastOption = broadcastMessageToFollowersRequest.getBroadcastOption();
        List<String> emailList = new ArrayList<>();
        System.out.println("broadcast message");
        System.out.println(broadcastMessageToFollowersRequest.getContent());
        System.out.println(broadcastMessageToFollowersRequest.getBroadcastOption());

        if (broadcastOption.equals("AllBpFollowers")) {

            List<BusinessPartner> BpFollowersList = new ArrayList<>();
            BpFollowersList = this.getPartnerFollowersById(eo.getId());
            for (BusinessPartner bp : BpFollowersList) {
                if(bp.isEoEmailNoti()){
                emailList.add(bp.getEmail());
                }
            }

        } else if (broadcastOption.equals("AllAttFollowers")) {

            List<Attendee> AttFollowersList = new ArrayList<>();
            AttFollowersList = this.getAttendeeFollowersById(eo.getId());

            for (Attendee att : AttFollowersList) {
                 if(att.isEoEmailNoti()){
                emailList.add(att.getEmail());
                 }
            }

        } else if (broadcastOption.equals("Both")) {
            List<BusinessPartner> BpFollowersList = new ArrayList<>();
            BpFollowersList = this.getPartnerFollowersById(eo.getId());
            List<Attendee> AttFollowersList = new ArrayList<>();
            AttFollowersList = this.getAttendeeFollowersById(eo.getId());

            for (BusinessPartner bp : BpFollowersList) {
                 if(bp.isEoEmailNoti()){
                emailList.add(bp.getEmail());
                }
            }

            for (Attendee att : AttFollowersList) {
                  if(att.isEoEmailNoti()){
                emailList.add(att.getEmail());
                  }
            }

        }

        String message = broadcastMessageToFollowersRequest.getContent();

        SimpleMailMessage email = new SimpleMailMessage();

        String[] mailArray = emailList.toArray(new String[0]);
        // System.out.println("mailArray");
        // System.out.println(mailArray);
        //  System.out.println(mailArray.length);
        // System.out.println(mailArray[0]);

        email.setFrom(fromEmail);
        email.setTo(mailArray);
        email.setSubject("New Message from EventStop");
        email.setText("You have received the following message from " + eo.getName() + ":" + "\r\n\r\n" + "\"" + message
                + "\"" + " " + "\r\n\r\n" + "<b>"
                + "This is an automated email from EventStop. Do not reply to this email.</b>" + "\r\n" + "<b>"
                + "Please direct your reply to " + eo.getName() + " at " + eo.getEmail() + "</b>");
        // cc the person who submitted the enquiry.
        email.setCc(eo.getEmail());
        javaMailSender.send(email);
          System.out.println(email);
          System.out.println("sent");
    }

    //dashboards

    public List<SellerApplication> getAllPendingSellerApplicationByUser(EventOrganiser eo){
       
        List<SellerApplication> allSellerApplication = new ArrayList<>();
        List<SellerApplication> filteredSellerApplication = new ArrayList<>();
       
        allSellerApplication = sellerAppService.getAllSellerApplications();
      
        for(SellerApplication sa:allSellerApplication){
            if(sa.getEvent().getEventOrganiser().getId().equals(eo.getId()) && sa.getSellerApplicationStatus().toString().equals("PENDING")){
                 filteredSellerApplication.add(sa);
            }
        }
       
       return filteredSellerApplication;
    }

    public static boolean isSameDay(Date date1, Date date2) {
    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

    return fmt.format(date1).equals(fmt.format(date2));
    }

    public Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
    return java.sql.Timestamp.valueOf(dateToConvert);
}

    public double getDailyBoothSales(EventOrganiser eo) throws StripeException{
        Stripe.apiKey = stripeSecretKey;
        
        List<SellerApplication> allSellerApplication = new ArrayList<>();
        Date now = new Date();
        allSellerApplication = sellerAppService.getAllSellerApplications();
      
        double totalSales = 0;
        for (SellerApplication sa : allSellerApplication) {
           
            if (sa.getEvent().getEventOrganiser().getId().equals(eo.getId())
                    && sa.getPaymentStatus().toString().equals("COMPLETED") && isSameDay(convertToDateViaSqlTimestamp(sa.getPaymentDate()),now)) {

                PaymentIntent paymentIntent = PaymentIntent.retrieve(sa.getStripePaymentId());
                double amount = paymentIntent.getAmount();
                totalSales += amount;  
            }
        }
    
        return totalSales;
       
    }

    public double getMonthlyBoothSales(EventOrganiser eo) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        List<SellerApplication> allSellerApplication = new ArrayList<>();
        allSellerApplication = sellerAppService.getAllSellerApplications();

        double totalSales = 0;
         LocalDateTime now = LocalDateTime.now();
        for (SellerApplication sa : allSellerApplication) {
           
          
            if (sa.getEvent().getEventOrganiser().getId().equals(eo.getId())
                    && sa.getPaymentStatus().toString().equals("COMPLETED")
                    && (sa.getPaymentDate().getYear() == now.getYear()) && (sa.getPaymentDate().getMonth() == now.getMonth())){

                PaymentIntent paymentIntent = PaymentIntent.retrieve(sa.getStripePaymentId());
                double amount = paymentIntent.getAmount();
                totalSales += amount;
                }

            }

        return totalSales;

    }
      public double getYearlyBoothSales(EventOrganiser eo) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        List<SellerApplication> allSellerApplication = new ArrayList<>();
        allSellerApplication = sellerAppService.getAllSellerApplications();

        double totalSales = 0;
         LocalDateTime now = LocalDateTime.now();
        for (SellerApplication sa : allSellerApplication) {
         
            if (sa.getEvent().getEventOrganiser().getId().equals(eo.getId())
                    && sa.getPaymentStatus().toString().equals("COMPLETED")
                    && (sa.getPaymentDate().getYear() == now.getYear())){

                PaymentIntent paymentIntent = PaymentIntent.retrieve(sa.getStripePaymentId());
                double amount = paymentIntent.getAmount();
                totalSales += amount;
                }

            }

        return totalSales;

    }

    
    public double getDailyTicketSales(EventOrganiser eo) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        List<TicketTransaction> allticketTrans = new ArrayList<>();
        Date now = new Date();
        allticketTrans = ttService.getAllTicketTransacionByEo(eo);

        double totalSales = 0;
        for (TicketTransaction tt : allticketTrans) {

            if (tt.getEvent().getEventOrganiser().getId().equals(eo.getId())
                    && tt.getPaymentStatus().toString().equals("COMPLETED")
                    && isSameDay(convertToDateViaSqlTimestamp(tt.getDateTimeOrdered()), now)) {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(tt.getStripePaymentId());
                double amount = paymentIntent.getAmount();
                totalSales += amount;
            }
        }

        return totalSales;

    }


    public double getMonthlyTicketSales(EventOrganiser eo) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        List<TicketTransaction> allticketTrans = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        allticketTrans = ttService.getAllTicketTransacionByEo(eo);

        double totalSales = 0;
        for (TicketTransaction tt : allticketTrans) {

            if (tt.getEvent().getEventOrganiser().getId() == eo.getId()
                    && tt.getPaymentStatus().toString().equals("COMPLETED")
                    && (tt.getDateTimeOrdered().getYear() == now.getYear()) && (tt.getDateTimeOrdered().getMonth() == now.getMonth())) {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(tt.getStripePaymentId());
                double amount = paymentIntent.getAmount();
                totalSales += amount;
            }
        }

        return totalSales;

    }


    
    public double getYearlyTicketSales(EventOrganiser eo) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        List<TicketTransaction> allticketTrans = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        allticketTrans = ttService.getAllTicketTransacionByEo(eo);

        double totalSales = 0;
        for (TicketTransaction tt : allticketTrans) {

            if (tt.getEvent().getEventOrganiser().getId() == eo.getId()
                    && tt.getPaymentStatus().toString().equals("COMPLETED")
                    && (tt.getDateTimeOrdered().getYear() == now.getYear())) {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(tt.getStripePaymentId());
                double amount = paymentIntent.getAmount();
                totalSales += amount;
            }
        }
        System.out.println("totalSales yearly" + totalSales);
        return totalSales;

    }


    public double getTotalTicketSales(EventOrganiser eo)throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        List<TicketTransaction> allticketTrans = new ArrayList<>();
        // LocalDateTime now = LocalDateTime.now();
        allticketTrans = ttService.getAllTicketTransacionByEo(eo);

        double totalSales = 0;
        for (TicketTransaction tt : allticketTrans) {

            if (tt.getEvent().getEventOrganiser().getId() == eo.getId()) {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(tt.getStripePaymentId());
                double amount = paymentIntent.getAmount();
                totalSales += amount;
            }
        }

        return totalSales;

    }


    
    public double getTotalTicketSalesEvent(EventOrganiser eo, Long eventId)throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        List<TicketTransaction> allticketTrans = new ArrayList<>();
        // LocalDateTime now = LocalDateTime.now();
        allticketTrans = ttService.getAllTicketTransacionByEo(eo);

        double totalSales = 0;
        for (TicketTransaction tt : allticketTrans) {

            if (tt.getEvent().getEventOrganiser().getId() == eo.getId() && tt.getEvent().getEid() == eventId) {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(tt.getStripePaymentId());
                double amount = paymentIntent.getAmount();
                totalSales += amount;
            }
        }

        return totalSales;

    }

    public int getTotalTicketSalesNumber(EventOrganiser eo)throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        List<TicketTransaction> allticketTrans = new ArrayList<>();
        allticketTrans = ttService.getAllTicketTransacionByEo(eo);
        int count = 0; 
        for (TicketTransaction tt : allticketTrans) {

            if (tt.getEvent().getEventOrganiser().getId() == eo.getId()) {
               count++;
            }
        }

        return count;

    }

    public int getTotalTicketSalesNumberByEvent(EventOrganiser eo, Long eventId)throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        List<TicketTransaction> allticketTrans = new ArrayList<>();
        allticketTrans = ttService.getAllTicketTransacionByEo(eo);
        int count = 0; 
        for (TicketTransaction tt : allticketTrans) {

            if (tt.getEvent().getEventOrganiser().getId() == eo.getId() && tt.getEvent().getEid() == eventId) {
               count++;
            }
        }

        return count;

    }

    public List<Event> getTopTicketSalesEvents(EventOrganiser eo) throws StripeException {
    
        Stripe.apiKey = stripeSecretKey;
        List<Event> events = new ArrayList<>();
        List<Event> finalEvents = new ArrayList<>();
        List<TicketTransaction> allticketTrans = new ArrayList<>();
        allticketTrans = ttService.getAllTicketTransacionByEo(eo);
        int getAllTen =0;
        for (TicketTransaction tt : allticketTrans) {
                if(!events.contains(tt.getEvent())){
                    events.add(tt.getEvent());
                }
            }
        while(getAllTen <11){
            double highestSales= 0.0;
            Event eventToBeRemoved = new Event();
            if(events.size() > 0) {
               for(int i=0; i<events.size(); i++){
                List<TicketTransaction> transactions = events.get(i).getTicketTransactions();
                double amount =0.0;
                for(TicketTransaction tt : transactions){
                    PaymentIntent paymentIntent = PaymentIntent.retrieve(tt.getStripePaymentId());
                    amount += paymentIntent.getAmount();
                }
                if(amount > highestSales){
                    highestSales = amount;
                    // eventToBeRemoved = events.get(i);
                    eventToBeRemoved = eventService.getEventById(events.get(i).getEid());
                }
            }

            finalEvents.add(eventToBeRemoved);
            events.remove(eventToBeRemoved);
            getAllTen += 1; 
            } else{
                break;
            }
            
        }
        // System.out.println("eventsTop" + finalEvents);
        return finalEvents;
    
    }

 

    public List<Double> topSalesEvent(List<Event> events) throws StripeException{
        List<Double> sales = new ArrayList<>();
        for(int i=0; i<events.size(); i++){
            List<TicketTransaction> transactions = events.get(i).getTicketTransactions();
            double amount =0.0;
            for(TicketTransaction tt : transactions){
                PaymentIntent paymentIntent = PaymentIntent.retrieve(tt.getStripePaymentId());
                amount += paymentIntent.getAmount();
            }
            sales.add(amount);
        }

        return sales;
        
    }

    public List<Integer> topNumTicketsEvent(List<Event> events){
        List<Integer> sales = new ArrayList<>();
        for(int i=0; i<events.size(); i++){
            List<TicketTransaction> transactions = events.get(i).getTicketTransactions();
          
            sales.add(transactions.size());
        }

        return sales;
        
    }

    public List<Event> getEventsWithTicketTransactionsCurrent (EventOrganiser eo) {
        List<Event> events = new ArrayList<>();
        List<TicketTransaction> allticketTrans = new ArrayList<>();
        allticketTrans = ttService.getAllTicketTransacionByEo(eo);
        LocalDateTime now = LocalDateTime.now();

        for (TicketTransaction tt : allticketTrans) {
                if(!events.contains(tt.getEvent()) && tt.getEvent().getSalesEndDate().isAfter(now)){
                    events.add(tt.getEvent());
                }
            }
        return events;
    }

    public List<Event> getEventsWithTicketTransactionsPast (EventOrganiser eo) {
        List<Event> events = new ArrayList<>();
        List<TicketTransaction> allticketTrans = new ArrayList<>();
        allticketTrans = ttService.getAllTicketTransacionByEo(eo);
        LocalDateTime now = LocalDateTime.now();

        for (TicketTransaction tt : allticketTrans) {
                if(!events.contains(tt.getEvent()) && tt.getEvent().getSalesEndDate().isBefore(now)){
                    events.add(tt.getEvent());
                }
            }
        return events;
    }


    public Long getDaysToEndOfTicketSale(Long eventId) {
        
        LocalDateTime now = LocalDateTime.now();
        System.out.println(eventId + "eventId");
       Event event = eventService.getEventById(eventId);
    //   long daysBetween = DAYS.between(now, event.getSalesEndDate());
    return now.until(event.getSalesEndDate(), ChronoUnit.DAYS);
    }

    public Long getDaysToStartOfEvent(Long eventId) {
        
        LocalDateTime now = LocalDateTime.now();
       Event event = eventService.getEventById(eventId);
    //   long daysBetween = DAYS.between(now, event.getSalesEndDate());
    return now.until(event.getEventStartDate(), ChronoUnit.DAYS);
    }
    



    //   public List<Event> getMostPopularEventList(EventOrganiser eo) {
    //    // Stripe.apiKey = stripeSecretKey;


    //     List<Event> allEventsOfEo = new ArrayList<>();
    //     allEventsOfEo = eventService.getAllEventsByOrganiser(eo.getId());
    //     List<?> mostPopularEventList = new ArrayList<>();
    //     mostPopularEventList = eventRepository.getMostPopularEventList();
    //     System.out.println(mostPopularEventList.size());
    //     System.out.println(mostPopularEventList.toArray());
        
    //     for(Object e:mostPopularEventList){
    //     System.out.println(e[0]);
    //     System.out.println(e.toString());
    //     }
    //     return allEventsOfEo;

        
    // }

    public Event upcomingEvent(EventOrganiser eo) {
        List<Event> events = getEventsWithTicketTransactionsCurrent(eo);
        LocalDateTime now = LocalDateTime.now();
        long days =1000000;
        Event eventFinal = new Event();
        if(events.size() >0){
           for(Event event : events){           
                System.out.println("current" + event.getEid());

            if(event.getSalesEndDate().isAfter(now) && now.until(event.getSalesEndDate(), ChronoUnit.DAYS) < days){
                days = now.until(event.getSalesEndDate(), ChronoUnit.DAYS);
                eventFinal = event;
            }
        }  
        }else{
            events = getEventsWithTicketTransactionsPast(eo);
            for(Event event : events){
                System.out.println("past" + event.getEid());

                if(event.getEventStartDate().isAfter(now) && now.until(event.getEventStartDate(), ChronoUnit.DAYS) < days){
                    days = now.until(event.getSalesEndDate(), ChronoUnit.DAYS);
                    eventFinal = event;
                }
            }
        }
        System.out.println("upcoming event" + eventFinal.getEid());
        return eventFinal;


    }

    // public List<Event>  getMostPopularEventList(EventOrganiser eo){
    //    List<Object[]> result = eventRepository.getMostPopularEventList();

    // get most popular events all the time
    // public List<Event>  getMostPopularEventList(EventOrganiser eo){
    //    List<Object[]> result = eventRepository.getMostPopularEventList();
    //    Map<BigInteger,BigInteger> map = new HashMap<BigInteger,BigInteger>();
    //    List<Event> popularEventList = new ArrayList<>();
    //    List<Event> eoEvents = eventService.getAllEventsByOrganiser(eo.getId());
    //    System.out.println(eoEvents.size());
    //    if(result != null && !result.isEmpty()){
    //       map = new HashMap<BigInteger,BigInteger>();
    //       for (Object[] object : result) {
    //           for(Event e : eoEvents){
    //               if((BigInteger)object[1] == BigInteger.valueOf(e.getEid())){
    //                 //  map.put(((BigInteger)object[1]),(BigInteger)object[0]);
    //                  e.setApplicationCount((BigInteger)object[0]);
    //                  popularEventList.add(e);
    //               }
    //           }           
    //       }
    //    }
    // return popularEventList;
    //  }
     // get most popular event of the day.
        public List<Event>  getBoothDashboardMostPopularEventOfTheDay(EventOrganiser eo){
       List<Object[]> result = eventRepository.getBoothDashboardDailyMostPopularEventList();

       Map<BigInteger,BigInteger> map = new HashMap<BigInteger,BigInteger>();
       List<Event> popularEventList = new ArrayList<>();
       List<Event> eoEvents = eventService.getAllEventsByOrganiser(eo.getId());
       System.out.println(eoEvents.size());
       if(result != null && !result.isEmpty()){
          map = new HashMap<BigInteger,BigInteger>();
          for (Object[] object : result) {
              for(Event e : eoEvents){
                  if((BigInteger)object[1] == BigInteger.valueOf(e.getEid())){
                    //  map.put(((BigInteger)object[1]),(BigInteger)object[0]);
                     e.setApplicationCount((BigInteger)object[0]);
                     popularEventList.add(e);
                  }
              }           
          }
       }
     
    return popularEventList;
     }
     
     public List<Event> getBoothDashboardMostPopularEventOfTheMonth(EventOrganiser eo) {
         List<Object[]> result = eventRepository.getBoothDashboardMonthlyMostPopularEventList();
         Map<BigInteger, BigInteger> map = new HashMap<BigInteger, BigInteger>();
         List<Event> popularEventList = new ArrayList<>();
         List<Event> eoEvents = eventService.getAllEventsByOrganiser(eo.getId());
         System.out.println(eoEvents.size());
         if (result != null && !result.isEmpty()) {
             map = new HashMap<BigInteger, BigInteger>();
             for (Object[] object : result) {
                 for (Event e : eoEvents) {
                     if ((BigInteger) object[1] == BigInteger.valueOf(e.getEid())) {
                         // map.put(((BigInteger)object[1]),(BigInteger)object[0]);
                         e.setApplicationCount((BigInteger) object[0]);
                         popularEventList.add(e);
                     }
                 }
             }
         }

         return popularEventList;
     }

     public List<Event> getBoothDashboardMostPopularEventOfTheYear(EventOrganiser eo) {
         List<Object[]> result = eventRepository.getBoothDashboardYearlyMostPopularEventList();
         Map<BigInteger, BigInteger> map = new HashMap<BigInteger, BigInteger>();
         List<Event> popularEventList = new ArrayList<>();
         List<Event> eoEvents = eventService.getAllEventsByOrganiser(eo.getId());
         System.out.println(eoEvents.size());
         if (result != null && !result.isEmpty()) {
             map = new HashMap<BigInteger, BigInteger>();
             for (Object[] object : result) {
                 for (Event e : eoEvents) {
                     if ((BigInteger) object[1] == BigInteger.valueOf(e.getEid())) {
                         // map.put(((BigInteger)object[1]),(BigInteger)object[0]);
                         e.setApplicationCount((BigInteger) object[0]);
                         popularEventList.add(e);
                     }
                 }
             }
         }

         return popularEventList;
     }

     public Map<Integer, Long> getEventRatingCountList(EventOrganiser eo){
      List<Review> AllEoReviews = reviewService.getReviewsByEO(eo.getId());
      Map<Integer, Long> result = AllEoReviews.stream().collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));
               
     return result;
     }

      public double getOverAllEventRating(EventOrganiser eo){
      List<Review> AllEoReviews = reviewService.getReviewsByEO(eo.getId());
      Long totalRatingValue = Long.valueOf(0);
      Long totalRatingCount =  Long.valueOf(0);
      double rating = 0;
      Map<Integer, Long> map = new HashMap<Integer,Long>();
      map = AllEoReviews.stream().collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));
        if (map != null && !map.isEmpty()) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
        Map.Entry<Integer,Long> pair = (Map.Entry<Integer,Long>)it.next();
        System.out.println(pair.getKey() + " = " + pair.getValue());
        totalRatingValue += pair.getKey() * pair.getValue();
        totalRatingCount += pair.getValue();
        it.remove(); // avoids a ConcurrentModificationException
    }
         rating = (double)totalRatingValue/totalRatingCount;
           
         }
           System.out.println(rating);
     return rating;
     }

     public double getTotalSalesByEvent(Long eventId,EventOrganiser eo) throws StripeException{
         Stripe.apiKey = stripeSecretKey;

         List<SellerApplication> allSellerApplication = new ArrayList<>();
         Date now = new Date();
         allSellerApplication = sellerAppService.getAllSellerApplications();

         double totalSales = 0;
         for (SellerApplication sa : allSellerApplication) {

             if (sa.getEvent().getEid() == eventId && sa.getEvent().getEventOrganiser().getId().equals(eo.getId()) && sa.getPaymentStatus().toString().equals("COMPLETED")){
                 PaymentIntent paymentIntent = PaymentIntent.retrieve(sa.getStripePaymentId());
                 double amount = paymentIntent.getAmount();
                 totalSales += amount;
             }
         }

         return totalSales;
     }
  
    public Long getNumberOfBusinessPartnerByEvent(Long eventId,EventOrganiser eo){
       
        List<SellerApplication> allSellerApplication = new ArrayList<>();
        allSellerApplication = sellerAppService.getAllSellerApplications();
        Long bpNum = Long.valueOf(0);
        for (SellerApplication sa : allSellerApplication) {

            if (sa.getEvent().getEid() == eventId && sa.getEvent().getEventOrganiser().getId().equals(eo.getId()) && sa.getPaymentStatus().toString().equals("COMPLETED")){
              bpNum += 1;
            }
        }

        return bpNum;
    }  

      
    public Long getNumberOfBoothApplications(EventOrganiser eo){
       
        List<SellerApplication> allSellerApplication = new ArrayList<>();
        allSellerApplication = sellerAppService.getAllSellerApplications();
        Long boothAppNum = Long.valueOf(0);
        for (SellerApplication sa : allSellerApplication) {

            if (sa.getEvent().getEventOrganiser().getId().equals(eo.getId()) && sa.getPaymentStatus().toString().equals("COMPLETED")) {
              boothAppNum += 1;
            }
        }

        return boothAppNum;
    } 
     public double getAllEventSales(EventOrganiser eo) throws StripeException{
       
        List<SellerApplication> allSellerApplication = new ArrayList<>();
        allSellerApplication = sellerAppService.getAllSellerApplications();
        double totalSales = 0;
      
        for (SellerApplication sa : allSellerApplication) {

            if (sa.getEvent().getEventOrganiser().getId().equals(eo.getId()) && sa.getPaymentStatus().toString().equals("COMPLETED") ) {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(sa.getStripePaymentId());
                double amount = paymentIntent.getAmount();
                totalSales += amount;
            }
        }

        return totalSales;
    }  
       
    public Long getNumberOfBoothSoldByEvent(Long eventId,EventOrganiser eo) {

        List<SellerApplication> allSellerApplication = new ArrayList<>();
        allSellerApplication = sellerAppService.getAllSellerApplications();
        Long numBoothSold = Long.valueOf(0);
        for (SellerApplication sa : allSellerApplication) {

            if (sa.getEvent().getEid() == eventId && sa.getEvent().getEventOrganiser().getId().equals(eo.getId()) && sa.getPaymentStatus().toString().equals("COMPLETED") && sa.getSellerApplicationStatus().toString().equals("CONFIRMED")) {
                numBoothSold += sa.getBoothQuantity();
            }
        }

        return numBoothSold;
    }

    public Long getNumberOfBoothCapacityByEvent(Long eventId, EventOrganiser eo) {

        Event event = eventService.getEventById(eventId);
        Long boothCapacity = Long.valueOf(0);
      
            if (event.getEventOrganiser().getId() == eo.getId() && event.getEventStatus().toString().equals("CREATED")) {
               boothCapacity += event.getBoothCapacity();
            }  

        return boothCapacity;
    }

    public Long getNumberOfBoothSoldByAllEvent(EventOrganiser eo) {

        List<SellerApplication> allSellerApplication = new ArrayList<>();
        allSellerApplication = sellerAppService.getAllSellerApplications();
        Long boothSold = Long.valueOf(0);
        for (SellerApplication sa : allSellerApplication) {

            if (sa.getEvent().getEventOrganiser().getId() == eo.getId() && sa.getPaymentStatus().toString().equals("COMPLETED")) {
                boothSold += sa.getBoothQuantity();
            }
        }

        return boothSold;
    }

    public Long getNumberofAllBoothCapacity(EventOrganiser eo) {

        List<Event> allEvents = new ArrayList<>();
        allEvents = this.getValidBpEventsByEventOrgIdForDashboard(eo.getId());
        Long boothCapacity = Long.valueOf(0);

        for (Event e : allEvents) {

            if (e.getEventStatus().toString().equals("CREATED")){
               boothCapacity += e.getBoothCapacity();
            }
        }

        return boothCapacity;
    }

    public  Map<String, Long> getCategoryRankList(EventOrganiser eo) {
        List<Object[]> applicationRankList = eventRepository.getApplicationRankList();
        Map<String, BigInteger> map = new HashMap<String, BigInteger>();
        List<Event> categoryRankList = new ArrayList<>();
        List<Event> eoEvents = eventService.getAllEventsByOrganiser(eo.getId());
        System.out.println(eoEvents.size());
        if (applicationRankList != null && !applicationRankList.isEmpty()) {
            map = new HashMap<String, BigInteger>();
            for (Object[] object : applicationRankList) {
                for (Event e : eoEvents) {
                    if ((BigInteger) object[0] == BigInteger.valueOf(e.getEid()) && e.getEventStatus().toString().equals("CREATED")) {

                        e.setApplicationCount((BigInteger)object[1]);
                        categoryRankList.add(e);
                    }
                }
            }
        }
        Map<String, Long> categoryCountMap = new HashMap<String, Long>();
        categoryCountMap = categoryRankList.stream().collect(Collectors.groupingBy(Event::getCategory, Collectors.counting()));
        System.out.println(categoryCountMap);
        return categoryCountMap;
    }


}

