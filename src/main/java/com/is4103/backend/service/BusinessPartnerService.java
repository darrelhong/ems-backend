package com.is4103.backend.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.is4103.backend.controller.EventOrganiserController;
import com.is4103.backend.dto.FollowRequest;
import com.is4103.backend.dto.PartnerSearchCriteria;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.UpdatePartnerRequest;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BoothApplicationStatus;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.Product;
import com.is4103.backend.model.Role;
import com.is4103.backend.model.RoleEnum;
import com.is4103.backend.model.SellerApplication;
import com.is4103.backend.model.SellerApplicationStatus;
import com.is4103.backend.repository.BusinessPartnerRepository;
import com.is4103.backend.repository.EventOrganiserRepository;
import com.is4103.backend.repository.EventRepository;
import com.is4103.backend.repository.PartnerSpecification;
import com.is4103.backend.util.errors.UserAlreadyExistsException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.registration.OnRegistrationCompleteEvent;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.Stripe;
import com.is4103.backend.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private EventOrganiserService eventOrganiserService;

    @Autowired
    private SellerApplicationService sellerApplicationService;

    @Value("${stripe.apikey}")
    private String stripeApiKey;

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
        user.getFavouriteEventList().add(event);
        return bpRepository.save(user);
    }

    public BusinessPartner unlikeEvent(BusinessPartner user, Long eid) {
        Event event = eventService.getEventById(eid);
        user.getFavouriteEventList().remove(event);
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

    public List<Event> getAllEventsByBpIdStatus(Long id, String role, String status) {

        List<SellerApplication> eventTransList = sellerApplicationService.getAllSellerApplications();
        List<Event> eventList = new ArrayList<>();
        for (SellerApplication trans : eventTransList) {
            if (!(trans.getPaymentStatus().toString().equals("REFUNDED")) && trans.getBusinessPartner().getId() == id
                    && trans.getSellerApplicationStatus().equals(SellerApplicationStatus.APPROVED)) {
                Event event = new Event();
                event = eventService.getEventById(trans.getEvent().getEid());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
                LocalDateTime now = LocalDateTime.now();
                if (role.equals("guest") || role.equals("ATND") || role.equals("EVNTORG")) {
                    if (status.equals("current")) {
                        if (event.getEventStatus().toString().equals("CREATED") && event.isPublished() == true
                                && (event.getEventStartDate().isAfter(now) || event.getEventStartDate().isEqual(now))) {
                            eventList.add(event);

                        }
                    } else if (status.equals("past")) {
                        if (event.getEventStatus().toString().equals("CREATED")
                                && (event.getEventEndDate().isBefore(now) || event.getEventEndDate().isEqual(now))) {

                            eventList.add(event);
                        }
                    }
                } else if (role.equals("BIZPTNR")) {

                    if (status.equals("current")) {
                        // System.out.println("id" + event.getEid());
                        // System.out.println("start" + event.getEventStartDate());
                        // System.out.println("startsales" + event.getSaleStartDate());
                        if (event.getEventStatus().toString().equals("CREATED") && !event.isHidden()
                                && (event.getEventStartDate().isAfter(now) || event.getEventStartDate().isEqual(now))) {
                            eventList.add(event);

                        }

                    } else if (status.equals("past")) {
                        if (event.getEventStatus().toString().equals("CREATED")
                                && (event.getEventEndDate().isBefore(now) || event.getEventEndDate().isEqual(now))) {
                            // System.out.println("in past");

                            eventList.add(event);
                        }

                    }
                }

            }
        }

        return eventList;
    }

    public List<Event> getEventsByBpFollowers(Long bpId) {
        List<Event> filterEventList = new ArrayList<>();
        List<EventOrganiser> eoFollowerList = getFollowingById(bpId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirdDay = now.plusDays(3);
        thirdDay = thirdDay.withHour(0).withMinute(0).withSecond(0).withNano(0);

        for (int i = 0; i < eoFollowerList.size(); i++) {
            EventOrganiser eo = eoFollowerList.get(i);
            List<Event> eoEventList = eoController.getAllEventsByEventOrgId(eo.getId());
            for (Event event : eoEventList) {
                if (event.getEventStatus().toString().equals("CREATED") && event.isHidden() == false
                        && !(event.getEventStartDate().isBefore(thirdDay))
                        && !(event.getSalesEndDate().isBefore(now))) {
                    filterEventList.add(event);
                }
            }
        }

        return filterEventList;
    }

    public List<Event> getEventsByBpFollowers(Long bpId, Long page) {
        List<Event> filterEventList = new ArrayList<>();
        List<EventOrganiser> eoFollowerList = getFollowingById(bpId);
        int currentEventNo = 0;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirdDay = now.plusDays(3);
        thirdDay = thirdDay.withHour(0).withMinute(0).withSecond(0).withNano(0);

        for (int i = 0; i < eoFollowerList.size(); i++) {
            EventOrganiser eo = eoFollowerList.get(i);
            List<Event> eoEventList = eoController.getAllEventsByEventOrgId(eo.getId());
            for (Event event : eoEventList) {
                if (event.getEventStatus().toString().equals("CREATED") && event.isHidden() == false
                        && !(event.getEventStartDate().isBefore(thirdDay))
                        && !(event.getSalesEndDate().isBefore(now))) {
                    currentEventNo += 1;

                    if (!(currentEventNo < (10 * (page - 1) + 1))) {
                        filterEventList.add(event);
                        if (filterEventList.size() == 10) {
                            return filterEventList;
                        }
                    }
                }
            }
        }

        return filterEventList;
    }

    public List<Event> getEventsByBpBusinessCategory(Long bpId) {
        List<Event> eventList = eventService.getAllEvents();
        String bizCat = getBusinessPartnerById(bpId).getBusinessCategory();
        List<Event> filterEventList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirdDay = now.plusDays(3);
        thirdDay = thirdDay.withHour(0).withMinute(0).withSecond(0).withNano(0);

        for (Event event : eventList) {
            if (event.getCategory().equals(bizCat) && event.getEventStatus().toString().equals("CREATED")
                    && event.isHidden() == false && !(event.getEventStartDate().isBefore(thirdDay))
                    && !(event.getSalesEndDate().isBefore(now))) {
                filterEventList.add(event);
            }
        }

        return filterEventList;
    }

    public List<Event> getEventsByBpBusinessCategory(Long bpId, Long page) {
        List<Event> eventList = eventService.getAllEvents();
        String bizCat = getBusinessPartnerById(bpId).getBusinessCategory();
        List<Event> filterEventList = new ArrayList<>();
        int currentEventNo = 0;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirdDay = now.plusDays(3);
        thirdDay = thirdDay.withHour(0).withMinute(0).withSecond(0).withNano(0);

        for (Event event : eventList) {
            if (event.getCategory().equals(bizCat) && event.getEventStatus().toString().equals("CREATED")
                    && event.isHidden() == false && !(event.getEventStartDate().isBefore(thirdDay))
                    && !(event.getSalesEndDate().isBefore(now))) {
                currentEventNo += 1;

                if (!(currentEventNo < (10 * (page - 1) + 1))) {
                    filterEventList.add(event);
                    if (filterEventList.size() == 10) {
                        return filterEventList;
                    }
                }
            }
        }
        return filterEventList;
    }

    public List<SellerApplication> getLatestSellerApplicationsbyBp(long id) {
        BusinessPartner partner = getBusinessPartnerById(id);
        List<SellerApplication> apps = partner.getSellerApplications();
        Collection<SellerApplication> nonDuplicate = apps.stream()
                .collect(Collectors.toMap(SellerApplication::generateUniqueKey, Function.identity(), (a, b) -> a))
                .values();
        return new ArrayList<>(nonDuplicate);
    }

    public List<Product> getProductsByBp(Long id) {
        return bpRepository.findById(id).get().getProducts();
    }

    public void removePaymentMethod(String paymentMethodId) throws StripeException {

        Stripe.apiKey = stripeApiKey;
        PaymentMethod pm = PaymentMethod.retrieve(paymentMethodId);
        pm.detach();
    }

    public Boolean checkIfBPIsVIP(long eoid, long bpid){
        List<BusinessPartner> vipList = eventOrganiserService.getAllVips(eoid);
        // BusinessPartner partner = getBusinessPartnerById(bpid);
        for(BusinessPartner vip : vipList){
            if(vip.getId() == bpid){
                return true;
            }
        }
        return false;

    }
}
