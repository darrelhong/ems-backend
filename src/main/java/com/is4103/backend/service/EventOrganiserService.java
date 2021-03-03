package com.is4103.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.repository.EventOrganiserRepository;
import com.is4103.backend.util.errors.UserAlreadyExistsException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.registration.OnRegistrationCompleteEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EventOrganiserService {
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
    public EventOrganiser registerNewEventOrganiser(SignupRequest signupRequest, boolean enabled,String bizsupportdocdownloadurl) {
       

        EventOrganiser newEo = new EventOrganiser();
        newEo.setName(signupRequest.getName());
        newEo.setEmail(signupRequest.getEmail());
        newEo.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        Role role = roleService.findByRoleEnum(RoleEnum.EVNTORG);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        newEo.setRoles(roles);
        newEo.setSupportDocsUrl(bizsupportdocdownloadurl);

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

    public List<BusinessPartner> getAllVips(Long eoId) {
        EventOrganiser eo = getEventOrganiserById(eoId);
        return eo.getVipList();
    }

    public List<Attendee> getAttendeeFollowersById (Long id){
        EventOrganiser organiser = getEventOrganiserById(id);
        List<Attendee>  followers = new ArrayList<>();
        followers = organiser.getAttendeeFollowers();
        return followers;
    }

    public List<BusinessPartner> getPartnerFollowersById (Long id){
        EventOrganiser organiser = getEventOrganiserById(id);
        List<BusinessPartner>  followers = new ArrayList<>();
        followers = organiser.getBusinessPartnerFollowers();
        return followers;
    }


  public List<Event> getAllEventsByEoId(Long eoId){
        EventOrganiser eo = getEventOrganiserById(eoId);
        List<Event> eventlist = eventService.getAllEvents();
    
         List<Event> eoeventlist = new ArrayList<>();
         for(int i = 0;i < eventlist.size();i++){
            if(eventlist.get(i).getEventOrganiser().getId() == eoId){
                eoeventlist.add(eventlist.get(i));
            }
         }
         eo.setEvents(eoeventlist);
  
        return eo.getEvents();
     
    }
}
