package com.is4103.backend.repository;

import com.is4103.backend.model.User;
import com.is4103.backend.model.VerificationToken;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

}
