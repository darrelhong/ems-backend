package com.is4103.backend.repository;
import com.is4103.backend.model.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    
}
