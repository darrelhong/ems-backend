package com.is4103.backend.dto.bpEventRegistration;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationDto {

    @NotNull
    private Long eventId;

    @NotNull
    private Integer boothQty;

    @NotNull
    private String description;

    private String comments;

    private String paymentMethodId;
}
