package com.is4103.backend.controller;

import java.util.List;

import com.is4103.backend.dto.RejectEventOrganiserDto;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.service.EventOrganiserService;
import com.is4103.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/organiser")
@PreAuthorize("hasRole('EVNTORG')")
public class EventOrganiserController {

    @Autowired
    private EventOrganiserService eoService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/all")
    public List<EventOrganiser> getAllEventOrganisers() {
        return eoService.getAllEventOrganisers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/all/paginated")
    public Page<EventOrganiser> getEventOrganisersPage(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return eoService.getEventOrganisersPage(page, size);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EVNTORG')")
    @GetMapping(path = "/{id}")
    public EventOrganiser getEventOrganiserById(@PathVariable Long id) {
        return eoService.getEventOrganiserById(id);
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
}