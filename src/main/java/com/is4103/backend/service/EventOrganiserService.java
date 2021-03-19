package com.is4103.backend.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.is4103.backend.model.EventBoothTransaction;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.model.TicketTransaction;
import com.is4103.backend.model.User;
import com.is4103.backend.repository.EventOrganiserRepository;
import com.is4103.backend.repository.OrganiserSpecification;
import com.is4103.backend.repository.UserRepository;
import com.is4103.backend.util.errors.UserAlreadyExistsException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.registration.OnRegistrationCompleteEvent;

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
        return eoRepository.save(toApprove);
    }

    public EventOrganiser rejectEventOrganiser(Long eoId, String message) {
        EventOrganiser toReject = getEventOrganiserById(eoId);
        toReject.setApproved(false);
        toReject.setApprovalMessage(message);
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

    public boolean isBpInVipList(Long eoId,Long bpId){
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
        for(Event event: eventlist){
          if(event.getEventStatus().toString().equals("CREATED") && !event.isHidden()){
            validEventListForBp.add(event);
          }
        }
        return validEventListForBp;
    }

        public List<Event> getValidAttEventsByEventOrgId(Long eoId) {

        List<Event> eventlist = eventService.getAllEventsByOrganiser(eoId);
        List<Event> validEventListForAtt = new ArrayList<>();
        for(Event event: eventlist){
          if(event.getEventStatus().toString().equals("CREATED") && !event.isHidden() && event.isPublished()){
            validEventListForAtt.add(event);
          }
        }
        return validEventListForAtt;
    }


    
      public List<Event> getAllEventsByEoIdRoleStatus(Long eoId,String role, String status) {
        List<Event> eventlist = eventService.getAllEvents();
        List<Event> filterEventList = new ArrayList<>();
     
        if(role.equals("guest") || role.equals("ATND") || role.equals("EVNTORG")){
            if(status.equals("upcoming")){
                filterEventList = new ArrayList<>();
                for(int a = 0; a < eventlist.size();a++){
                    Event eventItem = eventlist.get(a);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                    LocalDateTime now = LocalDateTime.now();
             
                    if(eventItem.getEventStatus().toString().equals("CREATED") && eventItem.isPublished() == true && (eventItem.getEventStartDate().isAfter(now) ||  eventItem.getEventStartDate().isEqual(now)) && (eventItem.getSaleStartDate().isAfter(now) || eventItem.getSaleStartDate().isEqual(now))){
                        filterEventList.add(eventItem);
                    }
                }
            
            }else if(status.equals("current")){
                   filterEventList = new ArrayList<>();
                for(int a = 0; a < eventlist.size();a++){
                    Event eventItem = eventlist.get(a);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    
                    if(eventItem.getEventStatus().toString().equals("CREATED") && eventItem.isPublished() == true && (eventItem.getEventStartDate().isAfter(now) ||  eventItem.getEventStartDate().isEqual(now)) && (eventItem.getSaleStartDate().isBefore(now) || eventItem.getSaleStartDate().isEqual(now)) && (eventItem.getSalesEndDate().isAfter(now) || eventItem.getSalesEndDate().isEqual(now))){
                     
                        filterEventList.add(eventItem);
                    }
                }
            }else if(status.equals("past")){
                  filterEventList = new ArrayList<>();
                for(int a = 0; a < eventlist.size();a++){
                    Event eventItem = eventlist.get(a);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                
                    if(eventItem.getEventStatus().toString().equals("CREATED") && (eventItem.getEventEndDate().isBefore(now) ||  eventItem.getEventEndDate().isEqual(now))){
                    
                        filterEventList.add(eventItem);
                    }
                }
            }

        }else if(role.equals("BIZPTNR")){
            if (status.equals("current")) {
                        filterEventList = new ArrayList<>();
                for(int a = 0; a < eventlist.size();a++){
                    Event eventItem = eventlist.get(a);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    if(eventItem.getEventStatus().toString().equals("CREATED") && !eventItem.isHidden() && (eventItem.getEventStartDate().isAfter(now) || eventItem.getEventStartDate().isEqual(now))&& (eventItem.getSaleStartDate().isAfter(now) || eventItem.getSaleStartDate().isEqual(now)))
                    
                        filterEventList.add(eventItem);
                    }
                } else if (status.equals("past")) {
                System.out.println("partner past");
            filterEventList = new ArrayList<>();
                for(int a = 0; a < eventlist.size();a++){
                    Event eventItem = eventlist.get(a);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                   if(eventItem.getEventStatus().toString().equals("CREATED") && (eventItem.getEventEndDate().isBefore(now) ||  eventItem.getEventEndDate().isEqual(now))){
                    
                        filterEventList.add(eventItem);
                    }
                }
            }   
       
        }
        return filterEventList;
    }  
    

    @Transactional
    public User updateEoProfile(User user, UpdateUserRequest updateUserRequest,String profilepicurl) {

    
        user.setName(updateUserRequest.getName());
        user.setDescription(updateUserRequest.getDescription());
        user.setAddress(updateUserRequest.getAddress());
        user.setPhonenumber(updateUserRequest.getPhonenumber());
        if(profilepicurl != null){
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
                return eoRepository.findByNameContaining(keyword,PageRequest.of(page, size));
            } else {
                return eoRepository.findByNameContaining(keyword,
                        PageRequest.of(page, size, sort));
            }

        }
        if (sort == null) {
            return eoRepository.findAll(PageRequest.of(page, size));
        } else {
            return eoRepository.findAll(PageRequest.of(page, size, sort));
        }

    }


    public List<BusinessPartner> getEventBps(List<EventBoothTransaction> eventBoothTransactionList){
          List<BusinessPartner> eventBpList = new ArrayList<>();
          for(int i = 0;i < eventBoothTransactionList.size();i++){
                EventBoothTransaction transItem = eventBoothTransactionList.get(i);
                if(!(transItem.getPaymentStatus().toString().equals("REFUNDED"))){
                    eventBpList.add(transItem.getBusinessPartner());
                }
            }
            return eventBpList;
    }


    public List<Attendee> getEventAtts(List<TicketTransaction> eventTicketTransactionList){
          List<Attendee> eventAttList = new ArrayList<>();
          for(int i = 0;i < eventTicketTransactionList.size();i++){
                TicketTransaction transItem = eventTicketTransactionList.get(i);
                if(!(transItem.getPaymentStatus().toString().equals("REFUNDED"))){
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
        List<EventBoothTransaction> eventBoothTransactionList = new ArrayList<>();
        eventBoothTransactionList = event.getEventBoothTransactions();
        List<TicketTransaction> eventTicketTransactionList = new ArrayList<>();
        eventTicketTransactionList = event.getTicketTransactions();
        if(broadcastOption.equals("Allbp")){
        
          List<BusinessPartner> eventBpList = new ArrayList<>();
          eventBpList = this.getEventBps(eventBoothTransactionList);

        for(BusinessPartner bp:eventBpList){
           emailList.add(bp.getEmail());
        } 
            
            
        }else if(broadcastOption.equals("Allatt")){
              
          List<Attendee> eventAttList = new ArrayList<>();
          eventAttList = this.getEventAtts(eventTicketTransactionList);

        for(Attendee att:eventAttList){
           emailList.add(att.getEmail());
        }    

        }else if(broadcastOption.equals("Both")){
            List<BusinessPartner> eventBpList = new ArrayList<>();
            eventBpList = this.getEventBps(eventBoothTransactionList);    
            List<Attendee> eventAttList = new ArrayList<>();
            eventAttList = this.getEventAtts(eventTicketTransactionList);

        for(BusinessPartner bp:eventBpList){
           emailList.add(bp.getEmail());
        } 
            
        for(Attendee att:eventAttList){
           emailList.add(att.getEmail());
        } 
            
        }

        String message = broadcastMessageRequest.getContent();

        SimpleMailMessage email = new SimpleMailMessage();
        
        String [] mailArray = emailList.toArray(new String[0]);
        System.out.println("mailArray");
        System.out.println(mailArray);
        email.setFrom(fromEmail);
        email.setTo(mailArray);
        email.setSubject(subject);
        email.setText("You have received the following message from " + eo.getName() + ":" + "\r\n\r\n" + "\""
                + message + "\"" + " " + "\r\n\r\n" + "<b>"
                + "This is an automated email from EventStop. Do not reply to this email.</b>" + "\r\n" + "<b>"
                + "Please direct your reply to " + eo.getName() + " at " + eo.getEmail() + "</b>");
        // cc the person who submitted the enquiry.
        email.setCc(eo.getEmail());
        javaMailSender.send(email);
    }


      @Transactional
    public void broadcastToFollowers(User eo, BroadcastMessageToFollowersRequest broadcastMessageToFollowersRequest) {

        String subject = broadcastMessageToFollowersRequest.getSubject();
        String broadcastOption = broadcastMessageToFollowersRequest.getBroadcastOption();
        List<String> emailList = new ArrayList<>();
    
     
        if(broadcastOption.equals("AllBpFollowers")){  
       
        List<BusinessPartner> BpFollowersList = new ArrayList<>();
        BpFollowersList = this.getPartnerFollowersById(eo.getId());
        for(BusinessPartner bp:BpFollowersList){
           emailList.add(bp.getEmail());
        }
            
            
        }else if(broadcastOption.equals("AllAttFollowers")){

        List<Attendee> AttFollowersList = new ArrayList<>();
        AttFollowersList = this.getAttendeeFollowersById(eo.getId());

        for(Attendee att:AttFollowersList){
           emailList.add(att.getEmail());
        }    

        }else if(broadcastOption.equals("Both")){
            List<BusinessPartner> BpFollowersList = new ArrayList<>();
            BpFollowersList = this.getPartnerFollowersById(eo.getId()); 
            List<Attendee> AttFollowersList = new ArrayList<>();
            AttFollowersList = this.getAttendeeFollowersById(eo.getId());

        for(BusinessPartner bp: BpFollowersList){
           emailList.add(bp.getEmail());
        } 
            
        for(Attendee att: AttFollowersList){
           emailList.add(att.getEmail());
        } 
            
        }

        String message = broadcastMessageToFollowersRequest.getContent();

        SimpleMailMessage email = new SimpleMailMessage();
        
        String [] mailArray = emailList.toArray(new String[0]);
        System.out.println("mailArray");
        System.out.println(mailArray);
        email.setFrom(fromEmail);
        email.setTo(mailArray);
        email.setSubject(subject);
        email.setText("You have received the following message from " + eo.getName() + ":" + "\r\n\r\n" + "\""
                + message + "\"" + " " + "\r\n\r\n" + "<b>"
                + "This is an automated email from EventStop. Do not reply to this email.</b>" + "\r\n" + "<b>"
                + "Please direct your reply to " + eo.getName() + " at " + eo.getEmail() + "</b>");
        // cc the person who submitted the enquiry.
        email.setCc(eo.getEmail());
        javaMailSender.send(email);
    }



    
}

