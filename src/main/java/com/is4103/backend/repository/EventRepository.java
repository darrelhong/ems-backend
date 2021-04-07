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

    @Query(value = "SELECT DISTINCT categories FROM event_categories", nativeQuery = true)
    List<String> getDistinctEventCategories();


   // @Query(value = "SELECT Count(id) as applicationCount, event_eid as eventId FROM seller_application sa GROUP BY event_eid", nativeQuery = true)
   // List<Object[]> getMostPopularEventList();
    
    // get most popular event of the day
    @Query(value = "SELECT Count(id) as applicationCount, event_eid as eventId, DATE(payment_date) as payment_date, payment_status FROM seller_application sa GROUP BY event_eid,DATE(payment_date), payment_status HAVING DATE(payment_date) = DATE(NOW()) and payment_status = 'COMPLETED'", nativeQuery = true)
    List<Object[]> getBoothDashboardDailyMostPopularEventList();
    
    // get most popular event of the month
    @Query(value = "SELECT Count(id) as applicationCount, event_eid as eventId, DATE(payment_date) as payment_date, payment_status FROM seller_application sa GROUP BY event_eid,DATE(payment_date),payment_status HAVING YEAR(payment_date) = YEAR(NOW()) and MONTH(payment_date) = MONTH(NOW()) and payment_status = 'COMPLETED'", nativeQuery = true)
    List<Object[]> getBoothDashboardMonthlyMostPopularEventList();
    
    // get most popular event of the year
     @Query(value = "SELECT Count(id) as applicationCount, event_eid as eventId, DATE(payment_date) as payment_date, payment_status FROM seller_application sa GROUP BY event_eid, DATE(payment_date), payment_status HAVING YEAR(payment_date) = YEAR(NOW()) and payment_status = 'COMPLETED'", nativeQuery = true)
    List<Object[]> getBoothDashboardYearlyMostPopularEventList();

    @Query(value = "SELECT event_eid, Count(event_eid) as applicationCount FROM seller_application sa GROUP BY sa.event_eid, sa.payment_status HAVING sa.payment_status !='CANCELLED'",nativeQuery = true)
     List<Object[]> getApplicationRankList();

//     @Query(value = "SELECT Count(category), e.category from event e GROUP BY e.category, event_status, e.is_hidden,e.event_organiser_id HAVING e.event_status = 'CREATED' and e.is_hidden = 0 and e.event_organiser_id = ?1",nativeQuery = true)
//      List<Object[]>  getCategoryRankListByEo(Long eoId);
    


        <T> Page<T> findByNameContainingAndIsPublishedAndEventStartDateGreaterThan(String name, boolean isPublished,
                        LocalDateTime eventStartDate, Pageable pageable, Class<T> type);

        <T> Page<T> findByIsPublishedAndEventStartDateGreaterThan(boolean isPublished, LocalDateTime eventStartDate,
                        Pageable pageable, Class<T> type);

        Page<Event> findByNameContainingAndIsPublishedAndEventEndDateLessThan(String name, boolean isPublished,
                        LocalDateTime eventStartDate, Pageable pageable);

        Page<Event> findByIsPublishedAndEventEndDateLessThan(boolean isPublished, LocalDateTime eventStartDate,
                        Pageable pageable);

        // @Query(value = "SELECT DISTINCT categories FROM event_categories", nativeQuery = true)
        // List<String> getDistinctEventCategories();
}