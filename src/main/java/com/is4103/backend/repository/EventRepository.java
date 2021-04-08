package com.is4103.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.is4103.backend.model.Event;
import com.is4103.backend.model.EventStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    <T> Optional<T> findByEid(Long id, Class<T> type);

    Page<Event> findByEventStatus(EventStatus eventStatus, Pageable pageable);

    @Query("SELECT e from Event e where e.eventOrganiser.id = ?1")
    List<Event> getAllEventsByOrganiser(Long oid);

    Page<Event> findByIsPublished(boolean isPublished, Pageable pageable);

    Page<Event> findByNameContainingAndIsPublished(String name, boolean isPublished, Pageable pageable);

    List<Event> findByName(String name);

    Page<Event> findByNameContainingAndIsPublishedAndEventStartDateGreaterThan(String name, boolean isPublished,
            LocalDateTime eventStartDate, Pageable pageable);

    Page<Event> findByIsPublishedAndEventStartDateGreaterThan(boolean isPublished, LocalDateTime eventStartDate,
            Pageable pageable);

    <T> Page<T> findByNameContainingAndIsPublishedAndEventStartDateGreaterThan(String name, boolean isPublished,
            LocalDateTime eventStartDate, Pageable pageable, Class<T> type);

    <T> Page<T> findByIsPublishedAndEventStartDateGreaterThan(boolean isPublished, LocalDateTime eventStartDate,
            Pageable pageable, Class<T> type);

    Page<Event> findByNameContainingAndIsPublishedAndEventEndDateLessThan(String name, boolean isPublished,
            LocalDateTime eventStartDate, Pageable pageable);

    Page<Event> findByIsPublishedAndEventEndDateLessThan(boolean isPublished, LocalDateTime eventStartDate,
            Pageable pageable);

    @Query(value = "SELECT DISTINCT e.eventCategory FROM Event e")
    List<String> getDistinctEventCategories();
}