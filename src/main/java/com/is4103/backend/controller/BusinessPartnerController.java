package com.is4103.backend.controller;

import java.util.List;

import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.service.BusinessPartnerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/partner")
@PreAuthorize("hasRole('BIZPTNR')")
public class BusinessPartnerController {

    @Autowired
    private BusinessPartnerService bpService;

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

    @PreAuthorize("hasAnyRole('ADMIN', 'BIZPTNR')")
    @GetMapping(path = "/{id}")
    public BusinessPartner getBusinessPartnerById(@PathVariable Long id) {
        return bpService.getBusinessPartnerById(id);
    }
}
