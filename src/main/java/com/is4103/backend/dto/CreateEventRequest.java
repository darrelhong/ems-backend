package com.is4103.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.is4103.backend.model.EventStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateEventRequest {
    @NotNull
    @NotEmpty
    private long eventOrganiserId;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String address;

    @NotNull
    @NotEmpty
    private String descriptions;

    @ElementCollection(targetClass = String.class)
    private List<String> categories;

    private String website;

    private boolean isSellingTicket;

    private float ticketPrice;

    private int ticketCapacity;

    @NotNull
    @NotEmpty
    private boolean isPhysical;

    @NotNull
    @NotEmpty
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime eventStartDate;

    @NotNull
    @NotEmpty
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime eventEndDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime saleStartDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime salesEndDate;

    @NotNull
    @NotEmpty
    @ElementCollection(targetClass = String.class)
    private List<String> images;

    private float boothPrice;

    @NotNull
    @NotEmpty
    private int boothCapacity;

    private int rating;

    @NotNull
    @NotEmpty
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    private boolean isVip;

    private boolean isHidden;

    private boolean isPublished;

}
