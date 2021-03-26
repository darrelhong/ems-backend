package com.is4103.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.is4103.backend.controller.EventOrganiserController;
import com.is4103.backend.dto.FollowRequest;
import com.is4103.backend.dto.PartnerSearchCriteria;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.UpdatePartnerRequest;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.model.SellerApplication;
import com.is4103.backend.repository.BusinessPartnerRepository;
import com.is4103.backend.repository.EventOrganiserRepository;
import com.is4103.backend.repository.EventRepository;
import com.is4103.backend.repository.PartnerSpecification;
import com.is4103.backend.util.errors.UserAlreadyExistsException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.registration.OnRegistrationCompleteEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

@Service
public class BusinessPartnerService {

    @Autowired
    private BusinessPartnerRepository bpRepository;

    @Autowired
    private EventOrganiserRepository eoRepository;

    @Autowired
    private EventOrganiserController eoController;

    @Autowired
    private UserService userService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private EventService eventService;

    @Autowired
    private SellerApplicationService sellerApplicationService;

    public List<BusinessPartner> getAllBusinessPartners() {
        return bpRepository.findAll();
    }

    public List<Event> getAllEvents(Long id) {
        // BusinessPartner partner = new BusinessPartner();
        // partner = getBusinessPartnerById(id);
        // System.out.println("partner " + partner );
        // List<EventBoothTransaction> transactions = new ArrayList<>();
        // System.out.println("transactions " + partner.getEventBoothTransactions());
        // transactions = partner.getEventBoothTransactions();
        // List<Event> events = new ArrayList<>();
        // if(!transactions.isEmpty()){
        // for(int i=0; i<transactions.size(); i++){
        // events.add(transactions.get(i).getEvent());
        // }
        // }

        // return events;

        List<Event> events = eventRepository.findAll();
        // System.out.println("events" + events);

        return events;

    }

    public Page<BusinessPartner> getBusinessPartnersPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return bpRepository.findAll(pageRequest);
    }

    public BusinessPartner getBusinessPartnerById(Long id) {
        return bpRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
    }

    public BusinessPartner getPartnerByEmail(String email) {
        return bpRepository.findByEmail(email);
    }

    public List<Attendee> getFollowersById(Long id) {
        BusinessPartner partner = getBusinessPartnerById(id);
        List<Attendee> followers = new ArrayList<>();
        followers = partner.getAttendeeFollowers();
        return followers;
    }

    public List<EventOrganiser> getFollowingById(Long id) {
        BusinessPartner partner = getBusinessPartnerById(id);
        List<EventOrganiser> following = new ArrayList<>();
        following = partner.getFollowEventOrganisers();
        return following;
    }

    @Transactional
    public BusinessPartner registerNewBusinessPartner(SignupRequest signupRequest, boolean enabled)
            throws UserAlreadyExistsException {
        if (userService.emailExists(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Account with email " + signupRequest.getEmail() + " already exists");
        }

        BusinessPartner newBp = new BusinessPartner();
        newBp.setName(signupRequest.getName());
        newBp.setEmail(signupRequest.getEmail());

        newBp.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        Role role = roleService.findByRoleEnum(RoleEnum.BIZPTNR);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        newBp.setRoles(roles);

        if (enabled) {
            newBp.setEnabled(true);
        }

        if (enabled) {
            newBp.setEnabled(true);
        }

        newBp = bpRepository.save(newBp);

        if (!newBp.isEnabled()) {
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(newBp));
        }

        return newBp;
    }

    @Transactional
    public BusinessPartner updatePartner(BusinessPartner user, UpdatePartnerRequest updatePartnerRequest,
            String profilepicurl) {

        user.setName(updatePartnerRequest.getName());
        user.setDescription(updatePartnerRequest.getDescription());
        user.setAddress(updatePartnerRequest.getAddress());
        user.setPhonenumber(updatePartnerRequest.getPhonenumber());
        user.setBusinessCategory(updatePartnerRequest.getBusinessCategory());
        if (profilepicurl != null) {
            user.setProfilePic(profilepicurl);
        }

        return bpRepository.save(user);
    }

    public BusinessPartner likeEvent(BusinessPartner user, Long eid) {
        Event event = eventService.getEventById(eid);
        List<Event> likedEvents = user.getFavouriteEventList();
        likedEvents.add(event);
        user.setFavouriteEventList(likedEvents);
        return bpRepository.save(user);
    }

    public BusinessPartner unlikeEvent(BusinessPartner user, Long eid) {
        Event event = eventService.getEventById(eid);
        List<Event> likedEvents = user.getFavouriteEventList();
        likedEvents.remove(event);
        user.setFavouriteEventList(likedEvents);
        return bpRepository.save(user);
    }

    @Transactional
    public BusinessPartner followEventOrganiser(BusinessPartner user, FollowRequest followEORequest) {

        EventOrganiser eo = eoController.getEventOrganiserById(followEORequest.getId());
        List<EventOrganiser> follow = user.getFollowEventOrganisers();
        follow.add(eo);
        user.setFollowEventOrganisers(follow);
        List<BusinessPartner> followers = eo.getBusinessPartnerFollowers();
        followers.add(user);
        eoRepository.save(eo);
        return bpRepository.save(user);
    }

    @Transactional
    public BusinessPartner unfollowEventOrganiser(BusinessPartner user, FollowRequest followEORequest) {

        EventOrganiser eo = eoController.getEventOrganiserById(followEORequest.getId());
        List<EventOrganiser> follow = user.getFollowEventOrganisers();
        follow.remove(eo);
        user.setFollowEventOrganisers(follow);
        List<BusinessPartner> followers = eo.getBusinessPartnerFollowers();
        followers.remove(user);
        eoRepository.save(eo);
        return bpRepository.save(user);
    }

    public Page<BusinessPartner> getPartners(int page, int size) {
        // return eventRepository.findByEventStatus(EventStatus.PUBLISHED,
        // PageRequest.of(page, size));
        return bpRepository.findAll(PageRequest.of(page, size));
    }

    public Page<BusinessPartner> getAllPartners(int page, int size, String sortBy, String sortDir, String keyword) {
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
                return bpRepository.findByNameContaining(keyword, PageRequest.of(page, size));
            } else {
                return bpRepository.findByNameContaining(keyword, PageRequest.of(page, size, sort));
            }

        }
        if (sort == null) {
            return bpRepository.findAll(PageRequest.of(page, size));
        } else {
            return bpRepository.findAll(PageRequest.of(page, size, sort));
        }

    }

    public Page<BusinessPartner> getAllPartnersCat(int page, int size, String sortBy, String sortDir, String keyword,
            String businessCategory, String clear) {

        if (clear != null && clear.equals("true")) {
            return bpRepository.findAll(PageRequest.of(page, size));

        } else {
            Sort sort = null;
            if (sortBy != null && sortDir != null) {
                if (sortDir.equals("desc")) {
                    sort = Sort.by(sortBy).descending();
                } else if (sortDir.equals("asc")) {
                    sort = Sort.by(sortBy).ascending();
                }
            }

            if (businessCategory != null) {

                if (sort == null) {
                    return bpRepository.findByBusinessCategoryContaining(businessCategory, PageRequest.of(page, size));

                } else {
                    return bpRepository.findByBusinessCategoryContaining(businessCategory,
                            PageRequest.of(page, size, sort));
                }
            }

            if (keyword != null) {

                if (sort == null) {
                    return bpRepository.findByNameContaining(keyword, PageRequest.of(page, size));
                } else {
                    return bpRepository.findByNameContaining(keyword, PageRequest.of(page, size, sort));
                }

            }

            if (sort == null) {
                return bpRepository.findAll(PageRequest.of(page, size));
            } else {

                return bpRepository.findAll(PageRequest.of(page, size, sort));
            }

        }

    }

    public Page<BusinessPartner> search(PartnerSearchCriteria partnerSearchCriteria) {
        return bpRepository.findAll(new PartnerSpecification(partnerSearchCriteria),
                partnerSearchCriteria.toPageRequest());
    }

    public List<Event> getAllEventsByBp(Long id) {
        List<SellerApplication> eventTransList = sellerApplicationService.getAllSellerApplications();
        List<Event> eventList = new ArrayList<>();
        for (SellerApplication trans : eventTransList) {
            if (!(trans.getPaymentStatus().toString().equals("REFUNDED")) && trans.getBusinessPartner().getId() == id
                    && !(eventList.contains(trans.getEvent()))) {
                // Event event = new Event();
                // event = eventService.getEventById(trans.getEid());
                // event = eventService.getEventById(trans.getEid());
                // eventList.add(event);
                eventList.add(trans.getEvent());
            }
        }
        return eventList;
    }

}
