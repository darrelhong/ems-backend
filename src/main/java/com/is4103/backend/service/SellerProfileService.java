package com.is4103.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.is4103.backend.repository.SellerProfileRepository;
import com.is4103.backend.util.errors.BrochureNotFoundException;
import com.is4103.backend.repository.BusinessPartnerRepository;
import com.is4103.backend.repository.EventRepository;

import java.util.List;

import com.is4103.backend.dto.FileStorageProperties;
import com.is4103.backend.model.SellerProfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SellerProfileService {
    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    @Autowired
    private BusinessPartnerRepository businessPartnerRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FileStorageProperties fileStorageProperties;

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

    public SellerProfile removeBrochure(SellerProfile s, int imageIndex) throws BrochureNotFoundException {
        String imageUrl = s.getBrochureImages().get(imageIndex);
        String oldpicfilename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        System.out.println(oldpicfilename);
        Path oldFilepath = Paths.get(this.fileStorageProperties.getUploadDir() + "/brochures/" + oldpicfilename)
                .toAbsolutePath().normalize();
        System.out.println(oldFilepath);
        try {
            Files.deleteIfExists(oldFilepath);
            // at this point we remove it from the arraylist
            s.getBrochureImages().remove(imageIndex);
            sellerProfileRepository.save(s);
            return s;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BrochureNotFoundException();
        }
    }

    public void deleteProfileById(Long id) {
        SellerProfile sp = sellerProfileRepository.findById(id).get();
        sellerProfileRepository.delete(sp);
    }

    public SellerProfile createSellerProfile(SellerProfile sellerProfile) {
        return sellerProfileRepository.save(sellerProfile);
    }
}
