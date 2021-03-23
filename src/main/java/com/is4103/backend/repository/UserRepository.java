package com.is4103.backend.repository;

import com.is4103.backend.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    <T> T findByEmail(String email, Class<T> type);
}
