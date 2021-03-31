package com.is4103.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.is4103.backend.dto.FileStorageProperties;
import com.is4103.backend.dto.FollowRequest;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.SignupResponse;
import com.is4103.backend.dto.UpdateAttendeeRequest;
import com.is4103.backend.dto.UploadFileResponse;
import com.is4103.backend.dto.event.FavouriteEventDto;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.service.AttendeeService;
import com.is4103.backend.service.FileStorageService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileStorageProperties fileStorageProperties;

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

    // @PreAuthorize("hasAnyRole('ATND')")
    // @PostMapping(value = "/update")
    // public ResponseEntity<Attendee> updateAttendee(@RequestBody @Valid UpdateAttendeeRequest updateAttendeeRequest) {
    //     Attendee user = atnService.getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

    //     // verify user id
    //     if (updateAttendeeRequest.getId() != user.getId()) {
    //         throw new AuthenticationServiceException("An error has occured");
    //     }

    //     user = atnService.updateAttendee(user, updateAttendeeRequest);
    //     return ResponseEntity.ok(user);
    // }
 @PreAuthorize("hasAnyRole('ATND')")
    @PostMapping(value = "/updateAttProfile")
    public UploadFileResponse updateUser(UpdateAttendeeRequest updateUserRequest,
            @RequestParam(value = "profilepicfile", required = false) MultipartFile file) {

       
  Attendee user = atnService.getAttendeeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
       
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
                    
                    e.printStackTrace();
                }

            }
        }
        System.out.println("hello");
        user = atnService.updateAttendeeProfile(user, updateUserRequest, fileDownloadUri);
        System.out.println("user profile pic");
        System.out.println(user.getProfilePic());

        return new UploadFileResponse(user.getProfilePic());
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
}
