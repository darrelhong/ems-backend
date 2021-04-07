package com.is4103.backend.repository;

import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Event;
import com.is4103.backend.model.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    public SellerProfile findByEventAndBusinessPartner(Event event, BusinessPartner businessPartner);
}
