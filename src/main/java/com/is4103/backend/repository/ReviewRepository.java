package com.is4103.backend.repository;

import java.util.List;

import com.is4103.backend.model.Event;
import com.is4103.backend.model.Review;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
List<Review> findByEvent(Event event);
}
