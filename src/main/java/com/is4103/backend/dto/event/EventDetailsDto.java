package com.is4103.backend.dto.event;

import java.time.LocalDateTime;
import java.util.List;

import com.is4103.backend.model.EventStatus;

public interface EventDetailsDto {
    Long getEid();

    String getName();

    String getAddress();

    Boolean getSellingTicket();

    Float getTicketPrice();

    Boolean getPhysical();

    LocalDateTime getEventStartDate();

    LocalDateTime getEventEndDate();

    LocalDateTime getSaleStartDate();

    LocalDateTime getSalesEndDate();

    List<String> getImages();

    Integer getRating();

    EventStatus getEventStatus();

    Boolean getAvailableForSale();

    EventOrganiserDetails getEventOrganiser();

    interface EventOrganiserDetails {
        String getName();
    }
}
