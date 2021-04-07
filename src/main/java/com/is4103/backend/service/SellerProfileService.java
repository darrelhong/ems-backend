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

    public SellerProfile updateSellerProfileDescription(Long id, String description) {
        SellerProfile sp = sellerProfileRepository.findById(id).get();
        sp.setDescription(description);
        return sellerProfileRepository.save(sp);
    }

    public SellerProfile addBrochureImage(SellerProfile sp, String imageUrl) {
        List<String> images = sp.getBrochureImages();
        images.add(imageUrl);
        sp.setBrochureImages(images);
        return sellerProfileRepository.save(sp);
    }

    public void deleteProfileById(Long id) {
        SellerProfile sp = sellerProfileRepository.findById(id).get();
        sellerProfileRepository.delete(sp);
    }
}
