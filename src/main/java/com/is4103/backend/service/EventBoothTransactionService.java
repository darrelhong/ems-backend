package com.is4103.backend.service;

import java.util.List;

import com.is4103.backend.model.EventBoothTransaction;
import com.is4103.backend.repository.EventBoothTransactionRepository;
import com.is4103.backend.util.errors.EventBoothTransactionNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventBoothTransactionService {
    @Autowired
    private EventBoothTransactionRepository eventBoothRepository;

    public EventBoothTransaction getEventBoothTransactionById(Long eoId) {

        return eventBoothRepository.findById(eoId).orElseThrow(() -> new EventBoothTransactionNotFoundException());
    }

    public List<EventBoothTransaction> getAllEventBoothTransactions() {
        return eventBoothRepository.findAll();
    }




}
