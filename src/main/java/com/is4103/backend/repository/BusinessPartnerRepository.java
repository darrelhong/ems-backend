package com.is4103.backend.repository;

import com.is4103.backend.model.BusinessPartner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BusinessPartnerRepository
        extends JpaRepository<BusinessPartner, Long>, JpaSpecificationExecutor<BusinessPartner> {
    BusinessPartner findByEmail(String email);

    // Page<BusinessPartner> findAll(PartnerSpecification partnerSpecification,
    // Pageable pageable );

    Page<BusinessPartner> findByNameContaining(String name, Pageable pageable);

    Page<BusinessPartner> findByBusinessCategoryContaining(String businessCategory, Pageable pageable);

    Page<BusinessPartner> findByNameContainingAndBusinessCategoryContaining(String name, String businessCategory,
            Pageable pageable);
    // Page<BusinessPartner> findAll(PartnerSpecification partnerSpecification,
    // PageRequest pageRequest);
}
