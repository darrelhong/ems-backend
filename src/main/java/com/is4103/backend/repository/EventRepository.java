package com.is4103.backend.repository;

import java.util.List;

import com.is4103.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
