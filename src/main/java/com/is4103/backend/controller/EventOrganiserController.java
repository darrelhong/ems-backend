package com.is4103.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.is4103.backend.dto.BroadcastMessageRequest;
import com.is4103.backend.dto.BroadcastMessageToFollowersRequest;
import com.is4103.backend.dto.FileStorageProperties;
import com.is4103.backend.dto.OrganiserSearchCriteria;
import com.is4103.backend.dto.RejectEventOrganiserDto;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.SignupResponse;
import com.is4103.backend.dto.UpdateUserRequest;
import com.is4103.backend.dto.UploadBizSupportFileRequest;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.SellerApplication;
import com.is4103.backend.model.User;
import com.is4103.backend.service.EventOrganiserService;
import com.is4103.backend.service.FileStorageService;
import com.is4103.backend.service.UserService;
import com.is4103.backend.util.errors.UserAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.is4103.backend.dto.UploadFileResponse;
import com.stripe.exception.StripeException;

@RestController
@RequestMapping(path = "/organiser")
// @PreAuthorize("hasRole('EVNTORG')")
public class EventOrganiserController {

    @Autowired
    private EventOrganiserService eoService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/all")
    public List<EventOrganiser> getAllEventOrganisers() {
        return eoService.getAllEventOrganisers();
    }

    @Autowired
    private FileStorageService fileStorageService;

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/all/paginated")
    public Page<EventOrganiser> getEventOrganisersPage(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return eoService.getEventOrganisersPage(page, size);
    }

    // @PreAuthorize("hasAnyRole('ADMIN', 'EVNTORG')")
    @GetMapping(path = "/{id}")
    public EventOrganiser getEventOrganiserById(@PathVariable Long id) {
        return eoService.getEventOrganiserById(id);
    }

    @GetMapping(path = "/attendeeFollowers/{id}")
    public List<Attendee> getAttendeeFollowers(@PathVariable Long id) {
        return eoService.getAttendeeFollowersById(id);
    }

    @GetMapping(path = "/partnerFollowers/{id}")
    public List<BusinessPartner> getPartnerFollowers(@PathVariable Long id) {
        return eoService.getPartnerFollowersById(id);
    }

    @PostMapping(value = "/register")
    public SignupResponse registerNewEventOrganiser(@RequestBody @Valid SignupRequest signupRequest) {

        try {

            if (userService.emailExists(signupRequest.getEmail())) {
                throw new UserAlreadyExistsException(
                        "Account with email " + signupRequest.getEmail() + " already exists");
            } else {

                eoService.registerNewEventOrganiser(signupRequest, false);

            }

        } catch (UserAlreadyExistsException userAlrExistException) {
            return new SignupResponse("alreadyExisted");
        }

        return new SignupResponse("success");

    }



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/register/noverify")
    public EventOrganiser registerNewEventOrganiserNoVerify(@RequestBody @Valid SignupRequest signupRequest) {
        return eoService.registerNewEventOrganiser(signupRequest, true);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/approve/{eoId}")
    public EventOrganiser approveEventOrganiser(@PathVariable Long eoId) {
        return eoService.approveEventOrganiser(eoId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/reject/{eoId}")
    public EventOrganiser rejectEventOrganiser(@PathVariable Long eoId, @RequestBody RejectEventOrganiserDto data) {
        String message = data.getMessage();
        return eoService.rejectEventOrganiser(eoId, message);
    }

    @GetMapping(value = "/vip/all")
    public List<BusinessPartner> getAllVips() {
        Long currentUserId = userService.getCurrentUserId();
        return eoService.getAllVips(currentUserId);
    }

    @PostMapping(value = "/vip/add/{bpId}")
    public List<BusinessPartner> addToVipList(@PathVariable Long bpId) {
        Long currentUserId = userService.getCurrentUserId();
        return eoService.addToVipList(currentUserId, bpId);
    }

    @PostMapping(value = "/vip/remove/{bpId}")
    public List<BusinessPartner> removeFromVipList(@PathVariable Long bpId) {
        Long currentUserId = userService.getCurrentUserId();
        return eoService.removeFromVipList(currentUserId, bpId);
    }

    @PostMapping(value = "/vip/isvip/{bpId}")
    public boolean isBpInVipList(@PathVariable Long bpId) {
        Long currentUserId = userService.getCurrentUserId();
        return eoService.isBpInVipList(currentUserId, bpId);
    }

    @GetMapping(value = "/event/{eoId}")
    public List<Event> getAllEventsByEventOrgId(@PathVariable Long eoId) {

        return eoService.getAllEventsByEoId(eoId);
    }

    @GetMapping(value = "/getVaildEventForBp/{eoId}")
    public List<Event> getValidBpEventsByEventOrgId(@PathVariable Long eoId) {

        return eoService.getValidBpEventsByEventOrgId(eoId);
    }

    @GetMapping(value = "/getVaildEventForAtt/{eoId}")
    public List<Event> getValidAttEventsByEventOrgId(@PathVariable Long eoId) {

        return eoService.getValidAttEventsByEventOrgId(eoId);
    }

    @GetMapping(value = "/event/{eoId}/{role}/{status}")
    public List<Event> getAllEventsByEventOrgIdRoleStatus(@PathVariable Long eoId, @PathVariable String role,
            @PathVariable String status) {
        System.out.println("call getAllEventsByEventOrgIdRoleStatus");
        return eoService.getAllEventsByEoIdRoleStatus(eoId, role, status);

    }

    @GetMapping(path = "/get-organisers")
    public Page<EventOrganiser> getOrganisers(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size, @RequestParam(required = false) String sort,
            @RequestParam(required = false) String sortDir, @RequestParam(required = false) String keyword) {
        return eoService.getAllOrganisers(page, size, sort, sortDir, keyword);
    }

    @GetMapping(path = "/search")
    public Page<EventOrganiser> search(OrganiserSearchCriteria organiserSearchCriteria) {
        return eoService.search(organiserSearchCriteria);
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @PostMapping(value = "/updateEoProfile")
    public UploadFileResponse updateUser(UpdateUserRequest updateUserRequest,
            @RequestParam(value = "profilepicfile", required = false) MultipartFile file) {

       
  EventOrganiser user = eoService.getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
       
        String fileDownloadUri = null;
        String filename = null;
        // verify user id
        if (updateUserRequest.getId() != user.getId()) {
            throw new AuthenticationServiceException("An error has occured");
        }
      
      

        if (file != null) {
            
            filename = fileStorageService.storeFile(file, "profilepic", "");

            fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(filename)
                    .toUriString();

            // if the user current has a profile picture
            if (user.getProfilePic() != null) {
                String profilepicpath = user.getProfilePic();
                String oldpicfilename = profilepicpath.substring(profilepicpath.lastIndexOf("/") + 1);

                System.out.println(oldpicfilename);
                Path oldFilepath = Paths
                        .get(this.fileStorageProperties.getUploadDir() + "/profilePics/" + oldpicfilename)
                        .toAbsolutePath().normalize();
                System.out.println(oldFilepath);
                try {
                    Files.deleteIfExists(oldFilepath);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        System.out.println("hello");
        user = eoService.updateEoProfile(user, updateUserRequest, fileDownloadUri);
        System.out.println("user profile pic");
        System.out.println(user.getProfilePic());

        return new UploadFileResponse(user.getProfilePic());
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @PostMapping(value = "/uploadbizdoc")
    public UploadFileResponse uploadEoBizFile(UploadBizSupportFileRequest updateBizSupportFileRequest,
            @RequestParam(value = "bizSupportDoc") MultipartFile file) {

        EventOrganiser user = (EventOrganiser) userService
                .getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        String fileDownloadUri = null;
        String filename = null;
        // verify user id
        // if the request is not send by the correct user
        if (updateBizSupportFileRequest.getId() != user.getId()) {
            throw new AuthenticationServiceException("An error has occured");
        }

        if (file != null) {
            filename = fileStorageService.storeFile(file, "bizsupportdoc", user.getEmail());

            fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(filename)
                    .toUriString();

        }
        user = eoService.updateEoBizSupportUrl(user, fileDownloadUri);
        return new UploadFileResponse("success");

    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @PostMapping(value = "/broadcastEmailEnquiry")
    public ResponseEntity sendEnquiry(@RequestBody @Valid BroadcastMessageRequest broadcastMessageRequest) {
        User sender = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        // verify user id
        // if current user is null
        if (sender == null) {
            throw new AuthenticationServiceException("An error has occured");
        } else {

            eoService.broadcastMessage(sender, broadcastMessageRequest);

        }

        return ResponseEntity.ok("Success");
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @PostMapping(value = "/broadcastEmailToFollowers")
    public ResponseEntity sendEmailToFollowers(
            @RequestBody @Valid BroadcastMessageToFollowersRequest broadcastMessageToFollowersRequest) {
        User sender = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        // verify user id
        // if current user is null
        if (sender == null) {
            throw new AuthenticationServiceException("An error has occured");
        } else {

            eoService.broadcastToFollowers(sender, broadcastMessageToFollowersRequest);

        }

        return ResponseEntity.ok("Success");
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getAllPendingBoothApplicationByEo")
    public List<SellerApplication> getAllPendingBoothApplicationByEo(){

        EventOrganiser user =  eoService.getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
   
     
        return  eoService.getAllPendingSellerApplicationByUser(user);
    }
    
    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getBoothDailySales")
    public double getAllBoothDailySalesByEo() {

        EventOrganiser user = eoService.getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        try{
        return eoService.getDailyBoothSales(user);
        }catch(StripeException ex){
            System.out.println(ex);
           
        }
         return -1;
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getBoothYearlySales")
    public double getAllBoothYearlySalesByEo() {

        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            return eoService.getYearlyBoothSales(user);
        } catch (StripeException ex) {
            System.out.println(ex);

        }
        return -1;
    }


    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getBoothMonthlySales")
    public double getAllBoothMonthlySalesByEo() {

        EventOrganiser user = eoService.getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            return eoService.getMonthlyBoothSales(user);
        } catch (StripeException ex) {
            System.out.println(ex);

        }
        return -1;
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTicketDailySales")
    public double getAllTicketDailySalesByEo() {

        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            return eoService.getDailyTicketSales(user);
        } catch (StripeException ex) {
            System.out.println(ex);

        }
        return -1;
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTicketMonthlySales")
    public double getAllTicketMonthlySalesByEo() {

        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            return eoService.getMonthlyTicketSales(user);
        } catch (StripeException ex) {
            System.out.println(ex);

        }
        return -1;
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTicketYearlySales")
    public double getAllTicketYearlySalesByEo() {

        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            return eoService.getYearlyTicketSales(user);
        } catch (StripeException ex) {
            System.out.println(ex);

        }
        return -1;
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTicketTotalSales")
    public double getAllTicketTotalSalesByEo() {

        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            return eoService.getTotalTicketSales(user);
        } catch (StripeException ex) {
            System.out.println(ex);

        }
        return -1;
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTicketTotalSalesEvent/{eventId}")
    public double getAllTicketTotalSalesByEoEvent(@PathVariable Long eventId) {

        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            return eoService.getTotalTicketSalesEvent(user, eventId);
        } catch (StripeException ex) {
            System.out.println(ex);

        }
        return -1;
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTicketTotalNumberSales")
    public int getAllTicketTotalNumberSalesByEo() {

        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            return eoService.getTotalTicketSalesNumber(user);
        } catch (StripeException ex) {
            System.out.println(ex);

        }
        return -1;
    }



    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTicketTotalNumberSalesEvent/{eventId}")
    public int getAllTicketTotalNumberSalesByEoEvent(@PathVariable Long eventId) {

        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            return eoService.getTotalTicketSalesNumberByEvent(user, eventId);
        } catch (StripeException ex) {
            System.out.println(ex);

        }
        return -1;
    }


    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTopTicketSalesEvents")
    public List<Event> getTopTicketsSalesEventsByEo() throws StripeException {
       
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

            return eoService.getTopTicketSalesEvents(user);
      
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTopSales")
    public List<Double> getTopTicketsSales() throws StripeException {
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Event> events = eoService.getTopTicketSalesEvents(user);

            return eoService.topSalesEvent(events);
      
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getCurrentSales")
    public List<Double> getCurrentTicketsSales() throws StripeException {
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Event> events = eoService.getEventsWithTicketTransactionsCurrent(user);

            return eoService.topSalesEvent(events);
      
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getCurrentNumber")
    public List<Integer> getCurrentTicketsNumberSales() throws StripeException {
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Event> events = eoService.getEventsWithTicketTransactionsCurrent(user);

            return eoService.topNumTicketsEvent(events);
      
    }


    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTopNumber")
    public List<Integer> getTopTicketsNumberSales() throws StripeException {
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Event> events = eoService.getTopTicketSalesEvents(user);

            return eoService.topNumTicketsEvent(events);
      
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTicketSalesEventsCurrent")
    public List<Event> getCurrentTicketsSalesEventsByEo() {
        List<Event> events = new ArrayList<>();
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        
            return eoService.getEventsWithTicketTransactionsCurrent(user);
       
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTicketSalesEventsPast")
    public List<Event> getPastTicketsSalesEventsByEo() {
        List<Event> events = new ArrayList<>();
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        
            return eoService.getEventsWithTicketTransactionsPast(user);
       
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getDaysToEndOfSales/{eventId}")
    public Long getDays(@PathVariable Long eventId) {
             
            return eoService.getDaysToEndOfTicketSale(eventId);
       
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getDaysToStartOfEvent/{eventId}")
    public Long getDaysToStartEvent(@PathVariable Long eventId) {
             
            return eoService.getDaysToStartOfEvent(eventId);
       
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getUpcomingEvent")
    public Event getUpcomingEvent() {
        EventOrganiser user = eoService
        .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
             
            return eoService.upcomingEvent(user);
       
    }



    @GetMapping(path = "/getBoothDashboardDailyMostPopularEventList")
    public List<Event> getBoothDashboardDailyMostPopularEventList() {        
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return eoService.getBoothDashboardMostPopularEventOfTheDay(user);
    
    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getBoothDashboardMonthlyMostPopularEventList")
    public List<Event> getBoothDashboardMonthlyMostPopularEventList() {
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return eoService.getBoothDashboardMostPopularEventOfTheMonth(user);

    }


    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getBoothDashboardYearlyMostPopularEventList")
    public List<Event> getBoothDashboardYearlyMostPopularEventList() {
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return eoService.getBoothDashboardMostPopularEventOfTheYear(user);

    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getEventRatingCountList")
    public Map<Integer, Long> getEventRatingCountList() {
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return eoService.getEventRatingCountList(user);

    }
    
    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getOverallEventRating")
    public String getOverAllEventRating() {
        DecimalFormat df2 = new DecimalFormat("#.##");
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        double rating = eoService.getOverAllEventRating(user);
        return df2.format(rating);

    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getTotalSalesByEvent/{eventId}")
    public String getTotalSalesByEvent(@PathVariable Long eventId) {
        DecimalFormat df2 = new DecimalFormat("#.##");
        EventOrganiser user = eoService.getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(user != null){
        double sales;
        try {
            sales = eoService.getTotalSalesByEvent(eventId,user);
            return df2.format(sales);
        } catch (StripeException e) {
            return "An Error has occured";
        }
      
        }
        else{
            throw new AuthenticationServiceException("An error has occured");
        }
    }

     
    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getNumberOfBusinessPartnerByEvent/{eventId}")
    public Long getNumberOfBusinessPartnerByEvent(@PathVariable Long eventId) {
       
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(user == null){
            throw new AuthenticationServiceException("An error has occured");
        }
       return eoService.getNumberOfBusinessPartnerByEvent(eventId,user);

    }

    
    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getNumberOfAllBoothApplications")
    public Long getNumberOfBoothApplications() {
       
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(user == null){
            throw new AuthenticationServiceException("An error has occured");
        }
       return eoService.getNumberOfBoothApplications(user);

    }

 

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getNumberOfBoothSoldByEvent/{eventId}")
    public Long getNumberOfBoothSoldByEvent(@PathVariable Long eventId) {
       
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(user == null){
            throw new AuthenticationServiceException("An error has occured");
        }
       return eoService.getNumberOfBoothSoldByEvent(eventId,user);

    }

 
       @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getNumberOfBoothCapacityByEvent/{eventId}")
    public Long getNumberOfBoothCapacityByEvent(@PathVariable Long eventId) {
       
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(user == null){
            throw new AuthenticationServiceException("An error has occured");
        }
       return eoService.getNumberOfBoothCapacityByEvent(eventId,user);

    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getNumberOfBoothSoldByAllEvent")
    public Long getNumberOfBoothSoldByAllEvent() {
       
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(user == null){
            throw new AuthenticationServiceException("An error has occured");
        }
       return eoService.getNumberOfBoothSoldByAllEvent(user);

    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getNumberofAllBoothCapacity")
    public Long getNumberofAllBoothCapacity() {

        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            throw new AuthenticationServiceException("An error has occured");
        }

        return eoService.getNumberofAllBoothCapacity(user);

    }
    

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getAllEventSalesEarned")
    public String getAllEventSalesEarned() {
        DecimalFormat df2 = new DecimalFormat("#.##");
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            throw new AuthenticationServiceException("An error has occured");
        }
        try {
            double allEventSales =  eoService.getAllEventSales(user);
            return df2.format(allEventSales);
        } catch (StripeException e) {
            return "An Error has occured";
        }

    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getCategoryRankList")
    public Map<String, Long> getCategoryRankList() {
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return eoService.getCategoryRankList(user);

    }

    @PreAuthorize("hasAnyRole('EVNTORG')")
    @GetMapping(path = "/getAllEventForBoothDashboard")
    public List<Event> getAllEventForBoothDashboard() {
        EventOrganiser user = eoService
                .getEventOrganiserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return eoService.getValidBpEventsByEventOrgIdForDashboard(user.getId());

    }

    



   
}
