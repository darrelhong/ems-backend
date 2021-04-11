package com.is4103.backend.repository;

import java.math.BigInteger;

import com.is4103.backend.model.Attendee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
    Attendee findByEmail(String email);
    Attendee findById(String id);
}
