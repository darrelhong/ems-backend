package com.is4103.backend.dto.ticketing;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentCompleteDto {

    @NotNull
    @NotEmpty
    List<String> ticketTransactionIds;
}
