package com.is4103.backend.dto;


import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateReview {
    private int rating;

    private String review;

    @Nullable
    private long attendeeId;

    @Nullable
    private long partnerId;

    private long eventId;

}
