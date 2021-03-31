package com.is4103.backend.dto.event;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCardClassDto {
    Long eid;
    String name;
    String descriptions;
    List<String> images;
    LocalDateTime eventStartDate;
}
