package com.is4103.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.is4103.backend.dto.FollowRequest;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.SignupResponse;
import com.is4103.backend.dto.UpdateAttendeeRequest;
import com.is4103.backend.dto.event.FavouriteEventDto;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.service.AttendeeService;
import com.is4103.backend.service.UserService;
import com.is4103.backend.util.errors.UserAlreadyExistsException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping(path = "/attendee")
public class AttendeeController {

    @Autowired
    private AttendeeService atnService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelmapper;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/all")
    public List<Attendee> getAllAttendees() {
        return atnService.getAllAttendees();
    }

    // @PreAuthorize("hasAnyRole('ADMIN', 'ATND')")
    @GetMapping(path = "/{id}")
    public Attendee getAttendeeById(@PathVariable Long id) {
        return atnService.getAttendeeById(id);
    }

    @GetMapping(path = "listFollowingBP/{id}")
    public List<BusinessPartner> getFollowingBP(@PathVariable Long id) {
        return atnService.getFollowingBp(id);
    }

    @GetMapping(path = "listFollowingEo/{id}")
    public List<EventOrganiser> getFollowingEo(@PathVariable Long id) {
        return atnService.getFollowingEo(id);
    }

    @PostMapping(value = "/register")
    public SignupResponse registerNewAttendee(@RequestBody @Valid SignupRequest signupRequest) {
        // return
        try {

            if (userService.emailExists(signupRequest.getEmail())) {
                throw new UserAlreadyExistsException(
                        "Account with email " + signupRequest.getEmail() + " already exists");
            } else {

                atnService.registerNewAttendee(signupRequest, false);

            }

        } catch (UserAlreadyExistsException userAlrExistException) {
            return new SignupResponse("alreadyExisted");
        }

        return new SignupResponse("success");
    }

    @PreAuthorize("hasAnyRole('ATND')")
    @PostMapping(value = "/followBP")
    public ResponseEntity<Attendee> followBusinessPartner(@RequestBody @Valid FollowRequest followRequest) {
        Attendee user = atnService.getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = atnService.followBusinessPartner(user, followRequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ATND')")
    @PostMapping(value = "/unfollowBP")
    public ResponseEntity<Attendee> unfollowBusinessPartner(@RequestBody @Valid FollowRequest followRequest) {
        Attendee user = atnService.getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = atnService.unfollowBusinessPartner(user, followRequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ATND')")
    @PostMapping(value = "/followEO")
    public ResponseEntity<Attendee> followEventOrganiser(@RequestBody @Valid FollowRequest followRequest) {
        Attendee user = atnService.getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = atnService.followEventOrganiser(user, followRequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ATND')")
    @PostMapping(value = "/unfollowEO")
    public ResponseEntity<Attendee> unfollowEventOrganiser(@RequestBody @Valid FollowRequest followRequest) {
        Attendee user = atnService.getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = atnService.unfollowEventOrganiser(user, followRequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ATND')")
    @PostMapping(value = "/update")
    public ResponseEntity<Attendee> updateAttendee(@RequestBody @Valid UpdateAttendeeRequest updateAttendeeRequest) {
        Attendee user = atnService.getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        // verify user id
        if (updateAttendeeRequest.getId() != user.getId()) {
            throw new AuthenticationServiceException("An error has occured");
        }

        user = atnService.updateAttendee(user, updateAttendeeRequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ATND')")
    @PostMapping(value = "/favourite-event")
    public ResponseEntity<?> favouriteEvent(@RequestParam Long eventId) {
        Attendee attendee = atnService
                .getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        List<Event> favourites = atnService.favouriteEvent(attendee, eventId);
        List<FavouriteEventDto> resp = favourites.stream().map(event -> modelmapper.map(event, FavouriteEventDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resp);

    }

    @PreAuthorize("hasRole('ATND')")
    @GetMapping(value = "/get-favourite-events")
    public ResponseEntity<?> getFavouriteEvent() {
        Attendee attendee = atnService
                .getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        List<Event> favourites = attendee.getFavouriteEvents();
        List<FavouriteEventDto> resp = favourites.stream().map(event -> modelmapper.map(event, FavouriteEventDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @PreAuthorize("hasRole('ATND')")
    @GetMapping(path = "/getEventsByAtnFollowers/{id}")
    public List<Event> getEventsByAtnFollowers(@PathVariable Long id) {
        return atnService.getEventsByAtnFollowers(id);
    }

    @PreAuthorize("hasRole('ATND')")
    @GetMapping(path = "/getEventsByAtnFollowers/{id}/{pageParam}")
    public List<Event> getEventsByAtnFollowers(@PathVariable Long id, @PathVariable Long pageParam) {
        return atnService.getEventsByAtnFollowers(id, pageParam);
    }

    @PreAuthorize("hasRole('ATND')")
    @GetMapping(path = "/getEventsByAtnCategoryPreferences/{id}")
    public List<Event> getEventsByAtnCategoryPreferences(@PathVariable Long id) {
        return atnService.getEventsByAtnCategoryPreferences(id);
    }

    @PreAuthorize("hasRole('ATND')")
    @GetMapping(path = "/getEventsByAtnCategoryPreferences/{id}/{pageParam}")
    public List<Event> getEventsByAtnCategoryPreferences(@PathVariable Long id, @PathVariable Long pageParam) {
        return atnService.getEventsByAtnCategoryPreferences(id, pageParam);
    }
}
