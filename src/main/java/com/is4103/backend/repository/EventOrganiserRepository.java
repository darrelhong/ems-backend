package com.is4103.backend.repository;

import com.is4103.backend.model.EventOrganiser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventOrganiserRepository extends JpaRepository<EventOrganiser, Long>, JpaSpecificationExecutor<EventOrganiser> {
    EventOrganiser findByEmail(String email);
    Page<EventOrganiser> findByNameContaining(String name,  Pageable pageable);
}
