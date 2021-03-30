package com.is4103.backend.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.is4103.backend.model.EventOrganiser;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class FollowRequest {
    @NotNull
    private Long id;
}
