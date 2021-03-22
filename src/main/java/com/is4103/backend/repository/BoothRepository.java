package com.is4103.backend.repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

import com.is4103.backend.model.Booth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoothRepository extends JpaRepository<Booth, Long> {
    @Query("SELECT b from Booth b where b.sellerProfile.id = ?1")
    public List<Booth> findBoothsBySellerProfile(Long id);
}
