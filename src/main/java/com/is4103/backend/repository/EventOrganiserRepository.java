package com.is4103.backend.repository;

import com.is4103.backend.model.EventOrganiser;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface EventOrganiserRepository extends JpaRepository<EventOrganiser, Long> {
    EventOrganiser findByEmail(String email);
}
