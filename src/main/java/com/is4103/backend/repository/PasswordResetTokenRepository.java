package com.is4103.backend.repository;

import com.is4103.backend.model.PasswordResetToken;
import com.is4103.backend.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);

}
