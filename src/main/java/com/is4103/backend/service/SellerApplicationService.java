package com.is4103.backend.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import com.is4103.backend.model.SellerApplication;
import com.is4103.backend.repository.SellerApplicationRepository;

public class SellerApplicationService {

    @Autowired
    private SellerApplicationRepository sellerApplicationRepository;

    public List<SellerApplication> getAllSellerApplications() {
        return sellerApplicationRepository.findAll();
    }

}
