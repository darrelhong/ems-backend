package com.is4103.backend.controller;

import java.io.Console;
import java.util.List;

import javax.validation.Valid;

import com.is4103.backend.dto.FollowRequest;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.SignupResponse;
import com.is4103.backend.dto.UpdateAttendeeRequest;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.service.AttendeeService;
import com.is4103.backend.service.UserService;
import com.is4103.backend.util.errors.UserAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/attendee")
public class AttendeeController {

    @Autowired
    private AttendeeService atnService;

    @Autowired
    private UserService userService;

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
    @PostMapping(value ="/followBP")
    public ResponseEntity<Attendee> followBusinessPartner(@RequestBody @Valid FollowRequest followRequest){
        Attendee user = atnService.getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = atnService.followBusinessPartner(user, followRequest);
        return ResponseEntity.ok(user);
    }

    
    @PreAuthorize("hasAnyRole('ATND')")
    @PostMapping(value ="/unfollowBP")
    public ResponseEntity<Attendee> unfollowBusinessPartner(@RequestBody @Valid FollowRequest followRequest){
        Attendee user = atnService.getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = atnService.unfollowBusinessPartner(user, followRequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ATND')")
    @PostMapping(value ="/followEO")
    public ResponseEntity<Attendee> followEventOrganiser(@RequestBody @Valid FollowRequest followRequest){
        Attendee user = atnService.getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = atnService.followEventOrganiser(user, followRequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ATND')")
    @PostMapping(value ="/unfollowEO")
    public ResponseEntity<Attendee> unfollowEventOrganiser(@RequestBody @Valid FollowRequest followRequest){
        Attendee user = atnService.getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = atnService.unfollowEventOrganiser(user, followRequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ATND')")
    @PostMapping(value = "/update")
    public ResponseEntity<Attendee> updateAttendee(
            @RequestBody @Valid UpdateAttendeeRequest updateAttendeeRequest) {
        Attendee user = atnService
                .getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        // verify user id
        if (updateAttendeeRequest.getId() != user.getId()) {
            throw new AuthenticationServiceException("An error has occured");
        }

        user = atnService.updateAttendee(user, updateAttendeeRequest);
        return ResponseEntity.ok(user);
    }

}
