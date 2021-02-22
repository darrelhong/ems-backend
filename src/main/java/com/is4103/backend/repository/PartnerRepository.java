package com.is4103.backend.repository;

import com.is4103.backend.model.BusinessPartner;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<BusinessPartner, Long> {
    BusinessPartner findByEmail(String email);
}