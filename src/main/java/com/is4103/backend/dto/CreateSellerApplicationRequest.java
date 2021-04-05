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
public class CreateSellerApplicationRequest {
    // @NotNull
    // @NotEmpty
    // //BP ID
    // private long id;

    // @NotNull
    // @NotEmpty
    // //EVENT ID
    // private long eid;

    @NotNull
    @NotEmpty
    private String description;

    private String comments;

    @NotNull
    @NotEmpty
    private int boothQuantity;
}
