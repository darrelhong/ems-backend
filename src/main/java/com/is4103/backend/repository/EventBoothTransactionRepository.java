package com.is4103.backend.repository;

import java.util.List;

import com.is4103.backend.model.EventBoothTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventBoothTransactionRepository extends JpaRepository<EventBoothTransaction, Long> {

}
