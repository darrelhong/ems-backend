package com.is4103.backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import com.is4103.backend.dto.CreateSellerApplicationRequest;
import com.is4103.backend.dto.FileStorageProperties;
import com.is4103.backend.dto.UploadFileResponse;
import com.is4103.backend.dto.bpEventRegistration.ApplicationDto;
import com.is4103.backend.dto.bpEventRegistration.ApplicationResponse;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.PaymentStatus;
import com.is4103.backend.model.SellerApplication;
import com.is4103.backend.model.SellerApplicationStatus;
import com.is4103.backend.model.SellerProfile;
import com.is4103.backend.repository.SellerProfileRepository;
import com.is4103.backend.service.BusinessPartnerService;
import com.is4103.backend.service.EventService;
import com.is4103.backend.service.FileStorageService;
import com.is4103.backend.service.SellerApplicationService;
import com.is4103.backend.service.SellerProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.is4103.backend.util.errors.BoothCapacityExceededException;
import com.is4103.backend.util.errors.BrochureNotFoundException;
import com.is4103.backend.util.errors.UserNotFoundException;
import com.is4103.backend.util.errors.ticketing.CheckoutException;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping(path = "/sellerProfile")
public class SellerProfileController {

    @Autowired
    private SellerProfileService sellerProfileService;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private SellerApplicationService sellerApplicationService;

    @Autowired
    private EventService eventService;

    @Autowired
    private BusinessPartnerService bpService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping(path = "/all")
    public List<SellerProfile> getAllSellerProfiles() {
        return sellerProfileService.getAllSellerProfiles();
    }

    @GetMapping(path = "/{id}")
    public SellerProfile getSellerProfileById(@PathVariable Long id) {
        return sellerProfileService.getSellerProfileById(id);
    }

    @GetMapping(path = "/bp/{id}")
    public List<SellerProfile> getSellerProfilesByBpId(@PathVariable Long id) {
        return sellerProfileService.getSellerProfilesByBpId(id);
    }

    @GetMapping(path = "/event/{id}")
    public List<SellerProfile> getSellerProfilesByEventId(@PathVariable Long id) {
        return sellerProfileService.getSellerProfilesByEventId(id);
    }

    @PostMapping(path = "/update/{id}")
    public SellerProfile updateSellerProfileDescription(@PathVariable Long id, @RequestBody String description) {
        return sellerProfileService.updateSellerProfileDescription(id, description);
    }

    @PostMapping("/uploadBrochure")
    public UploadFileResponse uploadBrochure(@RequestParam("file") MultipartFile file,
            @RequestParam(name = "id", defaultValue = "1") Long id) {
        String fileName = fileStorageService.storeFile(file, "brochure", "");

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                .path(fileName).toUriString();

        SellerProfile sp = sellerProfileService.getSellerProfileById(id);
        sp = sellerProfileService.addBrochureImage(sp, fileDownloadUri);

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @PostMapping("/remove-brochure")
    public SellerProfile removeBrochure(@RequestParam(name = "id", defaultValue = "1") Long id,
            @RequestParam(name = "imageUrl", defaultValue = "") String imageUrl) throws BrochureNotFoundException {
        SellerProfile s = getSellerProfileById(id);
        int imageIndex = s.getBrochureImages().indexOf(imageUrl);
        if (imageIndex < 0) {
            throw new BrochureNotFoundException();
        } else {
            return sellerProfileService.removeBrochure(s, imageIndex);
        }
    }

    @PostMapping(path = "/create-application")
    public SellerApplication createSellerApplication(@RequestBody CreateSellerApplicationRequest request,
            @RequestParam(name = "eid", defaultValue = "1") Long eid,
            @RequestParam(name = "id", defaultValue = "13") Long id) {
        SellerApplication application = new SellerApplication();
        Event event = eventService.getEventById(eid);
        BusinessPartner bp = bpService.getBusinessPartnerById(id);
        application.setBusinessPartner(bp);
        application.setEvent(event);
        application.setDescription(request.getDescription());
        application.setComments(request.getComments());
        application.setBoothQuantity(request.getBoothQuantity());
        application.setSellerApplicationStatus(SellerApplicationStatus.PENDING);
        application.setPaymentStatus(PaymentStatus.PENDING);
        application.setApplicationDate(LocalDateTime.now());
        return sellerApplicationService.createSellerApplication(application);
    }

    @PostMapping(value = "/checkout")
    public ResponseEntity<ApplicationResponse> createTransaction(@RequestBody @Valid ApplicationDto applicationDto) {
        try {
            // System.out.println("boothqty " + applicationDto.getBoothQty());
            // System.out.println("eventid " + applicationDto.getEventId());
            // System.out.println("comments " + applicationDto.getComments());
            // System.out.println("description " + applicationDto.getDescription());
            BusinessPartner bp = bpService
                    .getPartnerByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

            // ApplicationResponse result =
            // sellerApplicationService.createTransaction(applicationDto.getEventId(),
            // applicationDto.getBoothQty(), bp);
            ApplicationResponse result = sellerApplicationService.createTransaction(applicationDto, bp);

            if (result != null) {
                return ResponseEntity.ok(result);
            }
            throw new BoothCapacityExceededException();
        } catch (StripeException | UserNotFoundException e) {
            System.out.println(e.getMessage());
            throw new CheckoutException();
        }
    }

    @PostMapping(value = "/payment-complete/{id}")
    public ResponseEntity<SellerApplication> paymentComplete(@PathVariable String id) {
        try {
            SellerApplication app = sellerApplicationService.paymentComplete(id);
            return ResponseEntity.ok(app);
        } catch (Exception e) {
            throw new CheckoutException();
        }
    }

}
