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

    <T> Page<T> findByNameContainingAndIsPublishedAndEventStartDateGreaterThan(String name, boolean isPublished,
            LocalDateTime eventStartDate, Pageable pageable, Class<T> type);

    <T> Page<T> findByIsPublishedAndEventStartDateGreaterThan(boolean isPublished, LocalDateTime eventStartDate,
            Pageable pageable, Class<T> type);

    @Query(value = "SELECT DISTINCT categories FROM event_categories", nativeQuery = true)
    List<String> getDistinctEventCategories();


    @Query(value = "SELECT Count(id) as applicationCount, event_eid as eventId FROM seller_application sa GROUP BY event_eid", nativeQuery = true)
    List<Object[]> getMostPopularEventList();
    
    // get most popular event of the day
    @Query(value = "SELECT Count(id) as applicationCount, event_eid as eventId FROM seller_application sa GROUP BY event_eid", nativeQuery = true)
    List<Object[]> getDailyMostPopularEventList();
    
    // get most popular event of the month
    @Query(value = "SELECT Count(id) as applicationCount, event_eid as eventId FROM seller_application sa GROUP BY event_eid", nativeQuery = true)
    List<Object[]> getMonthlyMostPopularEventList();
    
    // get most popular event of the year
     @Query(value = "SELECT Count(id) as applicationCount, event_eid as eventId FROM seller_application sa GROUP BY event_eid", nativeQuery = true)
    List<Object[]> getYearlyMostPopularEventList();



}