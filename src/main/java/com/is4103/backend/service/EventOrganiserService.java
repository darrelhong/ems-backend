package com.is4103.backend.service;

import java.util.List;

import com.is4103.backend.model.EventOrganiser;
import com.is4103.backend.repository.EventOrganiserRepository;
import com.is4103.backend.util.errors.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventOrganiserService {
    @Autowired
    private EventOrganiserRepository eoRepository;

    public List<EventOrganiser> getAllEventOrganisers() {
        return eoRepository.findAll();
    }

    public EventOrganiser getEventOrganiserById(Long eoId) {
        return eoRepository.findById(eoId).orElseThrow(() -> new UserNotFoundException());
    }

    public EventOrganiser approveEventOrganiser(Long eoId) {
        EventOrganiser toApprove = getEventOrganiserById(eoId);
        toApprove.setApproved(true);
        toApprove.setApprovalMessage(null);
        return eoRepository.save(toApprove);
    }

    public EventOrganiser rejectEventOrganiser(Long eoId, String message) {
        EventOrganiser toReject = getEventOrganiserById(eoId);
        toReject.setApproved(false);
        toReject.setApprovalMessage(message);
        return eoRepository.save(toReject);
    }
}
