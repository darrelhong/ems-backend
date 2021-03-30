package com.is4103.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.is4103.backend.repository.SellerProfileRepository;
import com.is4103.backend.repository.BusinessPartnerRepository;
import com.is4103.backend.repository.EventRepository;

import java.util.List;

import com.is4103.backend.model.SellerProfile;

@Service
public class SellerProfileService {
    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    @Autowired
    private BusinessPartnerRepository businessPartnerRepository;

    @Autowired
    private EventRepository eventRepository;

    public List<SellerProfile> getAllSellerProfiles() {
        return sellerProfileRepository.findAll();
    }

    public SellerProfile getSellerProfileById(Long id) {
        return sellerProfileRepository.findById(id).get();
    }

    public List<SellerProfile> getSellerProfilesByBpId(Long id) {
        return businessPartnerRepository.findById(id).get().getSellerProfiles();
    }

    public List<SellerProfile> getSellerProfilesByEventId(Long id) {
        return eventRepository.findById(id).get().getSellerProfiles();
    }
}
