package com.is4103.backend.repository;

import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByEventStatus(EventStatus eventStatus, Pageable pageable);
}
