package com.is4103.backend.repository;

import java.util.List;

import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findByEventStatus(EventStatus eventStatus, Pageable pageable);

    @Query("SELECT e from Event e where e.eventOrganiser.id = ?1")
    List<Event> getAllEventsByOrganiser(Long oid);

    Page<Event> findByIsPublished(boolean isPublished, Pageable pageable);

    List<Event> findByName(String name);
}