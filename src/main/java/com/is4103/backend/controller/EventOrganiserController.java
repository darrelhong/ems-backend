package com.is4103.backend.controller;

import java.util.List;

import com.is4103.backend.dto.RejectEventOrganiserDto;
import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.service.EventOrganiserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/organiser")
@PreAuthorize("hasRole('ADMIN')")
public class EventOrganiserController {

    @Autowired
    EventOrganiserService eoService;

    @GetMapping(path = "/all")
    public List<EventOrganiser> getAllEventOrganisers() {
        return eoService.getAllEventOrganisers();
    }

    @PostMapping(value = "/approve/{eoId}")
    public EventOrganiser approveEventOrganiser(@PathVariable Long eoId) {
        return eoService.approveEventOrganiser(eoId);
    }

    @PostMapping(value = "/reject/{eoId}")
    public EventOrganiser rejectEventOrganiser(@PathVariable Long eoId, @RequestBody RejectEventOrganiserDto data) {
        String message = data.getMessage();
        return eoService.rejectEventOrganiser(eoId, message);
    }
}
