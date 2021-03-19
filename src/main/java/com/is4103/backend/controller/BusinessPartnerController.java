package com.is4103.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.is4103.backend.dto.DisabledAccountRequest;
import com.is4103.backend.dto.FileStorageProperties;
import com.is4103.backend.dto.FollowRequest;
import com.is4103.backend.dto.PartnerSearchCriteria;
import com.is4103.backend.dto.SignupRequest;
import com.is4103.backend.dto.SignupResponse;
import com.is4103.backend.dto.UpdatePartnerRequest;
import com.is4103.backend.dto.UpdateUserRequest;
import com.is4103.backend.dto.UploadFileResponse;
import com.is4103.backend.model.Attendee;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.model.User;
import com.is4103.backend.service.BusinessPartnerService;
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

@RestController
@RequestMapping(path = "/partner")

public class BusinessPartnerController {

    @Autowired
    private BusinessPartnerService bpService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private FileStorageService fileStorageService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/all")
    public List<BusinessPartner> getAllBusinessPartners() {
        return bpService.getAllBusinessPartners();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/all/paginated")
    public Page<BusinessPartner> getBusinessPartnersPage(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return bpService.getBusinessPartnersPage(page, size);
    }

    // @PreAuthorize("hasAnyRole('ADMIN', 'BIZPTNR')")
    @GetMapping(path = "/{id}")
    public BusinessPartner getBusinessPartnerById(@PathVariable Long id) {
        return bpService.getBusinessPartnerById(id);
    }

    @GetMapping(path = "/events/{id}")
    public List<Event> getEventsById(@PathVariable Long id) {
        return bpService.getAllEvents(id);
    }

    @PostMapping(value = "/register")
    public SignupResponse registerNewBusinessPartner(@RequestBody @Valid SignupRequest signupRequest) {
        // return
        try {

            if (userService.emailExists(signupRequest.getEmail())) {
                throw new UserAlreadyExistsException(
                        "Account with email " + signupRequest.getEmail() + " already exists");
            } else {

                bpService.registerNewBusinessPartner(signupRequest, false);

            }

        } catch (UserAlreadyExistsException userAlrExistException) {
            return new SignupResponse("alreadyExisted");
        }

        return new SignupResponse("success");
    }


    @GetMapping(path = "/get-partners")
    public Page<BusinessPartner> getPartners(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size, @RequestParam(required = false) String sort,
            @RequestParam(required = false) String sortDir, @RequestParam(required = false) String keyword) {
        return bpService.getAllPartners(page, size, sort, sortDir, keyword);
    }

    
    @GetMapping(path = "/get-partners-cat")
    public Page<BusinessPartner> getPartnersCat(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size, @RequestParam(required = false) String sort,
            @RequestParam(required = false) String sortDir, @RequestParam(required = false) String keyword, @RequestParam(required = false) String businessCategory, @RequestParam(required = false) String clear) {
            System.out.println("clear" + clear);
        return bpService.getAllPartnersCat(page, size, sort, sortDir, keyword, businessCategory, clear);
    }

    @GetMapping(path = "/search")
    public Page<BusinessPartner> search(PartnerSearchCriteria partnerSearchCriteria) {
        return bpService.search(partnerSearchCriteria);
    }


    @GetMapping(path = "/followers/{id}")
    public List<Attendee> getFollowers(@PathVariable Long id) {
        return bpService.getFollowersById(id);
    }

    @GetMapping(path = "/following/{id}")
    public List<EventOrganiser> getFollowing(@PathVariable Long id) {
        return bpService.getFollowingById(id);
    }

    @PreAuthorize("hasAnyRole('BIZPTNR')")
    @PostMapping(value ="/followEO")
    public ResponseEntity<BusinessPartner> followEventOrganiser(@RequestBody @Valid FollowRequest followEORequest){
        BusinessPartner user = bpService.getPartnerByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = bpService.followEventOrganiser(user, followEORequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('BIZPTNR')")
    @PostMapping(value ="/unfollowEO")
    public ResponseEntity<BusinessPartner> unfollowEventOrganiser(@RequestBody @Valid FollowRequest followEORequest){
        BusinessPartner user = bpService.getPartnerByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        user = bpService.unfollowEventOrganiser(user, followEORequest);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/register/noverify")
    public BusinessPartner registerNewBusinessPartnerNoVerify(@RequestBody @Valid SignupRequest signupRequest) {
        return bpService.registerNewBusinessPartner(signupRequest, true);
    }

    // @PreAuthorize("hasAnyRole('BIZPTNR')")
    // @PostMapping(value = "/update")
    // public ResponseEntity<BusinessPartner> updatePartner(
    //         @RequestBody @Valid UpdatePartnerRequest updatePartnerRequest) {
    //     BusinessPartner user = bpService
    //             .getPartnerByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

    //     // verify user id
    //     if (updatePartnerRequest.getId() != user.getId()) {
    //         throw new AuthenticationServiceException("An error has occured");
    //     }

    //     user = bpService.updatePartner(user, updatePartnerRequest);
    //     return ResponseEntity.ok(user);
    // }

    @PreAuthorize("hasAnyRole('BIZPTNR')")
    @PostMapping(value = "/update")
    public UploadFileResponse updateUser(
            UpdatePartnerRequest updatePartnerRequest,
            @RequestParam(value = "profilepicfile", required = false) MultipartFile file) {

      BusinessPartner user = bpService.getPartnerByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        String fileDownloadUri = null;
        String filename = null;
        // verify user id
        if (updatePartnerRequest.getId() != user.getId()) {
            throw new AuthenticationServiceException("An error has occured");
        }
        System.out.println("file");

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

        user = bpService.updatePartner(user, updatePartnerRequest, fileDownloadUri);

        return new UploadFileResponse(user.getProfilePic());
    }

    @GetMapping(value = "/getAllEventByBpId/{bpId}")
    public List<Event> getAllEventByBpId(@PathVariable Long bpId) {

        return bpService.getAllEventsByBp(bpId);
    }
    


}
