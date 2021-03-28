package com.is4103.backend.dto.event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventCardDto {
    Long getEid();

    String getName();

    String getDescriptions();

    List<String> getImages();

    LocalDateTime getEventStartDate();
}
