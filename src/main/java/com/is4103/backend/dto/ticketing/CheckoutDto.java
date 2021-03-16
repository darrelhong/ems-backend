package com.is4103.backend.dto.ticketing;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckoutDto {

    @NotNull
    private Integer ticketQty;
}
