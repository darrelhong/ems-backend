package com.is4103.backend.repository;

import java.util.UUID;

import com.is4103.backend.model.SellerApplication;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerApplicationRepository extends JpaRepository<SellerApplication, UUID> {
}
