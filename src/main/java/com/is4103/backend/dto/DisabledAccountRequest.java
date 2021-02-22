package com.is4103.backend.dto;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DisabledAccountRequest {

    @NotNull
    private Long id;

    private boolean enabled;

}
