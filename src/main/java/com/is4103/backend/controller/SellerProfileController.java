package com.is4103.backend.controller;

import java.util.List;

import com.is4103.backend.model.SellerProfile;
import com.is4103.backend.service.SellerProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/sellerProfile")
public class SellerProfileController {

    @Autowired
    private SellerProfileService sellerProfileService;

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

}
